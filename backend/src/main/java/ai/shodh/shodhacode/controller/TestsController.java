package ai.shodh.shodhacode.controller;

import ai.shodh.shodhacode.model.Problem;
import ai.shodh.shodhacode.repo.ProblemRepository;
import ai.shodh.shodhacode.repo.TestCaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/problems")
@RequiredArgsConstructor
@CrossOrigin
public class TestsController {
  private final ProblemRepository problemRepository;
  private final TestCaseRepository testCaseRepository;

  @GetMapping("/{problemId}/tests/count")
  public ResponseEntity<?> count(@PathVariable("problemId") Long problemId) {
    Problem p = problemRepository.findById(problemId).orElse(null);
    if (p == null) return ResponseEntity.notFound().build();
    int cnt = testCaseRepository.findByProblem(p).size();
    return ResponseEntity.ok(cnt);
  }
}
