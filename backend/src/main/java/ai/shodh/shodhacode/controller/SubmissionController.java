package ai.shodh.shodhacode.controller;

import ai.shodh.shodhacode.model.Submission;
import ai.shodh.shodhacode.service.SubmissionService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/submissions")
@RequiredArgsConstructor
@CrossOrigin
public class SubmissionController {
  private final SubmissionService submissionService;

  @PostMapping
  public ResponseEntity<?> create(@RequestBody SubmissionRequest req) {
    Submission sub = submissionService.createSubmission(req.getUsername(), req.getProblemId(), req.getContestId(), req.getCode(), req.getLanguage());
    return ResponseEntity.ok(new SubmissionId(sub.getId()));
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> get(@PathVariable("id") Long id) {
    return submissionService.getSubmission(id)
        .<ResponseEntity<?>>map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @Data
  public static class SubmissionRequest { private String username; private Long problemId; private Long contestId; private String code; private String language; }
  public record SubmissionId(Long id) {}
}
