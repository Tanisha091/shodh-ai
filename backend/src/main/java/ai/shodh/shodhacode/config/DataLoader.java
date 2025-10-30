package ai.shodh.shodhacode.config;

import ai.shodh.shodhacode.model.*;
import ai.shodh.shodhacode.repo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class DataLoader {
  @Bean
  CommandLineRunner init(ContestRepository contests, ProblemRepository problems, TestCaseRepository tests) {
    return args -> {
      // Ensure a sample contest with problems exists (idempotent)
      java.time.Instant now = java.time.Instant.now();
      Contest c = contests.findByName("Sample Contest")
          .orElseGet(() -> contests.save(Contest.builder()
              .name("Sample Contest")
              .description("Demo contest with 2 problems")
              .startTime(now.minusSeconds(300))
              .endTime(now.plusSeconds(3600))
              .build()));

      boolean needsSeed = c.getProblems() == null || c.getProblems().isEmpty();
      if (needsSeed) {
        Problem p1 = Problem.builder()
            .title("Sum A+B")
            .description("Read two integers and print their sum.")
            .contest(c)
            .sampleInput("2 3")
            .sampleOutput("5\n")
            .build();
        Problem p2 = Problem.builder()
            .title("Hello N times")
            .description("Read N and print 'Hello' N times, each on new line.")
            .contest(c)
            .sampleInput("3")
            .sampleOutput("Hello\nHello\nHello\n")
            .build();
        problems.save(p1);
        problems.save(p2);
        // Seed official tests
        tests.save(TestCase.builder().problem(p1).inputData("1 4").expectedOutput("5\n").build());
        tests.save(TestCase.builder().problem(p1).inputData("10 20").expectedOutput("30\n").build());
        tests.save(TestCase.builder().problem(p1).inputData("100 200").expectedOutput("300\n").build());

        tests.save(TestCase.builder().problem(p2).inputData("1").expectedOutput("Hello\n").build());
        tests.save(TestCase.builder().problem(p2).inputData("2").expectedOutput("Hello\nHello\n").build());
        tests.save(TestCase.builder().problem(p2).inputData("5").expectedOutput("Hello\nHello\nHello\nHello\nHello\n").build());
      }
    };
  }
}
