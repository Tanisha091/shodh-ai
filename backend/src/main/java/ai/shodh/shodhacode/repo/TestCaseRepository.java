package ai.shodh.shodhacode.repo;

import ai.shodh.shodhacode.model.TestCase;
import ai.shodh.shodhacode.model.Problem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TestCaseRepository extends JpaRepository<TestCase, Long> {
  List<TestCase> findByProblem(Problem problem);
}
