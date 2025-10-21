package com.example.plm.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity @Data @NoArgsConstructor
public class Feature {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String type; // HOLE, EDGE, FACE, etc.
  private double x;
  private double y;
  private double d1;
  private double d2;

  @ManyToOne @JoinColumn(name = "part_id")
  private Part part;
}
