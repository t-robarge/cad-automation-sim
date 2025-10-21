package com.example.plm.web;

import com.example.plm.service.ChangePackageService;
import com.example.plm.service.RuleService;
import com.example.plm.service.SvgViewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/automation")
public class AutomationController {
  private final RuleService rules;
  private final SvgViewService views;
  private final ChangePackageService changes;

  public AutomationController(RuleService rules, SvgViewService views, ChangePackageService changes){
    this.rules = rules; this.views = views; this.changes = changes;
  }

  @PostMapping("/run/{partId}")
  public ResponseEntity<?> run(@PathVariable Long partId) throws Exception {
    var annotations = rules.runForPart(partId);
    var svg = views.renderTopView(partId);
    record Payload(Object annotations, String topSvg) {}
    return ResponseEntity.ok(new Payload(annotations, svg));
  }
}
