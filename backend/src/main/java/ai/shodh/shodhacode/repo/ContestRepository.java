package ai.shodh.shodhacode.repo;

import ai.shodh.shodhacode.model.Contest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ContestRepository extends JpaRepository<Contest, Long> {
  Optional<Contest> findByName(String name);
}
