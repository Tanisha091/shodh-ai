package ai.shodh.shodhacode.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Submission {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false)
  private User user;

  @ManyToOne(optional = false)
  private Problem problem;

  @ManyToOne(optional = false)
  private Contest contest;

  @Column(length = 10000)
  private String code;

  private String language; // python, java, cpp

  private String status; // Pending, Running, Accepted, Wrong Answer, TLE, Error

  @Column(length = 2000)
  private String result; // details

  private Instant createdAt;
}
