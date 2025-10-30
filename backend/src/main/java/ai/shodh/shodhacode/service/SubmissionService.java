package ai.shodh.shodhacode.service;

import ai.shodh.shodhacode.model.*;
import ai.shodh.shodhacode.repo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class SubmissionService {
  private final SubmissionRepository submissionRepository;
  private final UserRepository userRepository;
  private final ProblemRepository problemRepository;
  private final ContestRepository contestRepository;
  private final JudgeService judgeService;

  public Submission createSubmission(String username, Long problemId, Long contestId, String code, String language) {
    User user = userRepository.findByUsername(username).orElseGet(() -> userRepository.save(User.builder().username(username).build()));
    Problem problem = problemRepository.findById(problemId).orElseThrow();
    Contest contest = contestRepository.findById(contestId).orElseThrow();
    Submission sub = Submission.builder()
        .user(user)
        .problem(problem)
        .contest(contest)
        .code(code)
        .language(language)
        .status("Pending")
        .createdAt(Instant.now())
        .build();
    sub = submissionRepository.save(sub);
    judgeService.judgeAsync(sub.getId());
    return sub;
  }

  public Optional<Submission> getSubmission(Long id) { return submissionRepository.findById(id); }
}
