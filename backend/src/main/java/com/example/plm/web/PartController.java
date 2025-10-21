package com.example.plm.web;

import com.example.plm.domain.Part;
import com.example.plm.service.PartService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/parts")
public class PartController {
  private final PartService service;
  public PartController(PartService service){ this.service = service; }

  @PostMapping
  public Part create(@RequestBody Part p){ return service.create(p); }

  @GetMapping("/{id}")
  public Part get(@PathVariable Long id){ return service.get(id); }
}
