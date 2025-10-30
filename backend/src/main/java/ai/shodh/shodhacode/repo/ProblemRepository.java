package ai.shodh.shodhacode.repo;

import ai.shodh.shodhacode.model.Problem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProblemRepository extends JpaRepository<Problem, Long> {}
