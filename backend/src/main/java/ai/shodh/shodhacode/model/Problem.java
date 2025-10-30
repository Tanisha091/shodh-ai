package ai.shodh.shodhacode.model;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@JsonIgnoreProperties({"contest"})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Problem {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String title;
  @Column(length = 4000)
  private String description;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "contest_id")
  private Contest contest;
  @Column(length = 4000)
  private String sampleInput;
  @Column(length = 4000)
  private String sampleOutput;
  @OneToMany(mappedBy = "problem", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  private java.util.List<TestCase> testCases = new java.util.ArrayList<>();
}
