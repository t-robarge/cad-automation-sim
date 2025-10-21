package com.example.plm.domain;

import com.example.plm.domain.enums.ViewType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity @Data @NoArgsConstructor
public class Annotation {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String label;
  private String value;
  private double x;
  private double y;

  @Enumerated(EnumType.STRING)
  private ViewType viewType = ViewType.TOP;

  @ManyToOne @JoinColumn(name = "part_id")
  private Part part;
}
