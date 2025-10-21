package com.example.plm.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity @Data @NoArgsConstructor
public class ChangePackage {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String partNumber;
  private String reason;
  @Column(length = 10000)
  private String diff;
  private Instant createdAt = Instant.now();
}
