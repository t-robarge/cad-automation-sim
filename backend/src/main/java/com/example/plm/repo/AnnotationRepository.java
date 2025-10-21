package com.example.plm.repo;

import com.example.plm.domain.Annotation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnnotationRepository extends JpaRepository<Annotation, Long> {
  List<Annotation> findByPart_Id(Long partId);
}
