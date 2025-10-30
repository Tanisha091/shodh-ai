package ai.shodh.shodhacode.repo;

import ai.shodh.shodhacode.model.Submission;
import ai.shodh.shodhacode.model.Contest;
import ai.shodh.shodhacode.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {
  List<Submission> findByContest(Contest contest);
  List<Submission> findByUser(User user);
  List<Submission> findByContestAndUserAndStatus(Contest contest, User user, String status);
}
