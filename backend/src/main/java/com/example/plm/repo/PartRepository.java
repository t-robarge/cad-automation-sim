package com.example.plm.repo;

import com.example.plm.domain.Part;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartRepository extends JpaRepository<Part, Long> {
  Part findByPartNumber(String partNumber);
}
