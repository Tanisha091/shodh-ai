package ai.shodh.shodhacode.controller;

import ai.shodh.shodhacode.model.*;
import ai.shodh.shodhacode.repo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/contests")
@RequiredArgsConstructor
@CrossOrigin
public class LeaderboardController {
  private final SubmissionRepository submissionRepository;
  private final ContestRepository contestRepository;
  private final UserRepository userRepository;

  @GetMapping("/{contestId}/leaderboard")
  public ResponseEntity<?> leaderboard(@PathVariable("contestId") Long contestId) {
    Contest contest = contestRepository.findById(contestId).orElse(null);
    if (contest == null) return ResponseEntity.notFound().build();
    List<Submission> subs = submissionRepository.findByContest(contest);

    Map<String, Long> solved = subs.stream()
        .filter(s -> "Accepted".equals(s.getStatus()))
        .collect(Collectors.groupingBy(s -> s.getUser().getUsername(), Collectors.counting()));

    List<Map<String, Object>> board = solved.entrySet().stream()
        .sorted((a,b)->Long.compare(b.getValue(), a.getValue()))
        .map(e -> {
          Map<String, Object> m = new HashMap<>();
          m.put("username", e.getKey());
          m.put("solved", e.getValue());
          return m;
        }).toList();

    return ResponseEntity.ok(board);
  }

  @GetMapping("/{contestId}/solved")
  public ResponseEntity<?> solvedProblems(@PathVariable("contestId") Long contestId, @RequestParam("username") String username) {
    Contest contest = contestRepository.findById(contestId).orElse(null);
    if (contest == null) return ResponseEntity.notFound().build();
    User user = userRepository.findByUsername(username).orElse(null);
    if (user == null) return ResponseEntity.ok(List.of());
    List<Submission> ok = submissionRepository.findByContestAndUserAndStatus(contest, user, "Accepted");
    Set<Long> problemIds = ok.stream().map(s -> s.getProblem().getId()).collect(Collectors.toSet());
    return ResponseEntity.ok(problemIds);
  }
}
