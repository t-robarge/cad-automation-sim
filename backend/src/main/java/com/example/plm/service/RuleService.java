package com.example.plm.service;

import com.example.plm.domain.Annotation;
import com.example.plm.domain.Part;
import com.example.plm.repo.AnnotationRepository;
import com.example.plm.repo.PartRepository;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RuleService {
  private final KieContainer kc;
  private final AnnotationRepository annotationRepo;
  private final PartRepository partRepo;

  public RuleService(KieContainer kc, AnnotationRepository annotationRepo, PartRepository partRepo){
    this.kc = kc; this.annotationRepo = annotationRepo; this.partRepo = partRepo;
  }

  @Transactional
  public List<Annotation> runForPart(Long partId){
    Part p = partRepo.findById(partId).orElseThrow();
    KieSession ks = kc.newKieSession();
    try {
      ks.insert(p);
      if (p.getFeatures() != null) p.getFeatures().forEach(ks::insert);
      ks.fireAllRules();
      List<Annotation> generated = ks.getObjects(o -> o instanceof Annotation)
        .stream().map(o -> (Annotation)o).peek(a -> a.setPart(p)).collect(Collectors.toList());
      return annotationRepo.saveAll(generated);
    } finally {
      ks.dispose();
    }
  }
}
