package com.example.plm.service;

import com.example.plm.domain.Annotation;
import com.example.plm.repo.AnnotationRepository;
import com.example.plm.repo.PartRepository;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

@Service
public class SvgViewService {
  private final AnnotationRepository annotationRepo;
  private final PartRepository partRepo;
  public SvgViewService(AnnotationRepository annotationRepo, PartRepository partRepo){
    this.annotationRepo = annotationRepo; this.partRepo = partRepo;
  }

  public String renderTopView(Long partId) throws Exception {
    partRepo.findById(partId).orElseThrow(); // ensure exists
    String base = Files.readString(Path.of(new ClassPathResource("svg/base-view.svg").getURI()));
    String overlays = annotationRepo.findByPart_Id(partId).stream()
      .map(a -> String.format("<text x=\"%f\" y=\"%f\" class=\"label\">%s %s</text>", a.getX(), a.getY(), a.getLabel(), a.getValue()))
      .collect(Collectors.joining());
    return base.replace("<!--OVERLAYS-->", overlays);
  }
}
