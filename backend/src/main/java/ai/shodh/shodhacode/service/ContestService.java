package ai.shodh.shodhacode.service;

import ai.shodh.shodhacode.model.Contest;
import ai.shodh.shodhacode.repo.ContestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ContestService {
  private final ContestRepository contestRepository;

  public Optional<Contest> getContest(Long id) {
    return contestRepository.findById(id);
  }
}
