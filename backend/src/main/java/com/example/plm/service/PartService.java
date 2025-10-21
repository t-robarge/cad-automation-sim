package com.example.plm.service;

import com.example.plm.domain.Feature;
import com.example.plm.domain.Part;
import com.example.plm.repo.PartRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PartService {
  private final PartRepository repo;
  public PartService(PartRepository repo){ this.repo = repo; }

  @Transactional
  public Part create(Part p){
    if (p.getFeatures() != null) {
      for (Feature f : p.getFeatures()) f.setPart(p);
    }
    return repo.save(p);
  }

  public Part get(Long id){ return repo.findById(id).orElseThrow(); }
}
