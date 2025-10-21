package com.example.plm.domain;

import com.example.plm.domain.enums.Unit;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity @Data @NoArgsConstructor
public class Part {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String partNumber;
  private String name;
  @Enumerated(EnumType.STRING)
  private Unit unit = Unit.MM;
  private Instant createdAt = Instant.now();

  @OneToMany(mappedBy = "part", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
  private List<Feature> features = new ArrayList<>();
}
