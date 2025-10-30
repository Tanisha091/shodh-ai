package ai.shodh.shodhacode.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.*;
import java.time.*;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Contest {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String name;
  @Column(length = 2000)
  private String description;
  private Instant startTime;
  private Instant endTime;
  @OneToMany(mappedBy = "contest", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
  private List<Problem> problems = new ArrayList<>();
}
