package ai.shodh.shodhacode.controller;

import ai.shodh.shodhacode.model.Contest;
import ai.shodh.shodhacode.service.ContestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contests")
@RequiredArgsConstructor
@CrossOrigin
public class ContestController {
  private final ContestService contestService;

  @GetMapping("/{contestId}")
  public ResponseEntity<?> getContest(@PathVariable("contestId") Long contestId) {
    return contestService.getContest(contestId)
        .<ResponseEntity<?>>map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }
}
