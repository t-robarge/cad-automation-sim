package com.example.plm.service;

import com.example.plm.domain.ChangePackage;
import com.example.plm.repo.ChangePackageRepository;
import org.springframework.stereotype.Service;

@Service
public class ChangePackageService {
  private final ChangePackageRepository repo;
  public ChangePackageService(ChangePackageRepository repo){ this.repo = repo; }

  public ChangePackage diffAndCreate(String partNumber, String reason, String beforeJson, String afterJson){
    String diff = simpleDiff(beforeJson, afterJson);
    ChangePackage cp = new ChangePackage();
    cp.setPartNumber(partNumber);
    cp.setReason(reason);
    cp.setDiff(diff);
    return repo.save(cp);
  }

  private String simpleDiff(String a, String b){ return "--- BEFORE\n"+a+"\n+++ AFTER\n"+b; }
}
