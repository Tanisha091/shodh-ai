package ai.shodh.shodhacode.service;

import ai.shodh.shodhacode.model.Submission;
import ai.shodh.shodhacode.model.Problem;
import ai.shodh.shodhacode.model.TestCase;
import ai.shodh.shodhacode.repo.SubmissionRepository;
import ai.shodh.shodhacode.repo.TestCaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JudgeService {
  private final SubmissionRepository submissionRepository;
  private final TestCaseRepository testCaseRepository;

  @Async
  public void judgeAsync(Long submissionId) {
    Submission sub = submissionRepository.findById(submissionId).orElse(null);
    if (sub == null) return;
    sub.setStatus("Running");
    submissionRepository.save(sub);

    try {
      Problem problem = sub.getProblem();
      List<TestCase> tests = testCaseRepository.findByProblem(problem);
      if (tests == null || tests.isEmpty()) {
        tests = java.util.List.of(TestCase.builder().inputData(problem.getSampleInput()).expectedOutput(problem.getSampleOutput()).problem(problem).build());
      }

      StringBuilder allOut = new StringBuilder();
      int idx = 1;
      for (TestCase tc : tests) {
        ProcessBuilder pb = new ProcessBuilder(
            "docker", "run", "--rm",
            "--memory", "256m",
            "--cpus", "0.5",
            "shodh-judge:latest",
            "--language", sub.getLanguage(),
            "--code", sub.getCode(),
            "--input", tc.getInputData(),
            "--expected", tc.getExpectedOutput()
        );
        pb.redirectErrorStream(true);
        Process p = pb.start();
        StringBuilder out = new StringBuilder();
        try (BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8))) {
          String line; while ((line = r.readLine()) != null) out.append(line).append('\n');
        }
        int exit = p.waitFor();
        String output = out.toString();
        allOut.append("[Case ").append(idx).append("]\n").append(output).append('\n');
        boolean accepted = (exit == 0 && output.contains("ACCEPTED"));
        if (!accepted) {
          if (output.contains("WRONG_ANSWER")) sub.setStatus("Wrong Answer");
          else if (output.contains("TLE")) sub.setStatus("Time Limit Exceeded");
          else if (output.contains("COMPILE_ERROR")) sub.setStatus("Error");
          else sub.setStatus("Error");
          sub.setResult(allOut.toString());
          submissionRepository.save(sub);
          return;
        }
        idx++;
      }
      sub.setStatus("Accepted");
      sub.setResult(allOut.toString());
      submissionRepository.save(sub);
    } catch (Exception ex) {
      sub.setStatus("Error");
      sub.setResult("Judge error: " + ex.getMessage());
      sub.setCreatedAt(Instant.now());
      submissionRepository.save(sub);
    }
  }
}
