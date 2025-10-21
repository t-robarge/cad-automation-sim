package com.example.plm.config;

import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.runtime.KieContainer;
import org.kie.api.builder.KieFileSystem;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DroolsConfig {
  @Bean
  public KieContainer kieContainer() {
    KieServices ks = KieServices.Factory.get();
    KieFileSystem kfs = ks.newKieFileSystem();
    // classpath resources
    kfs.write("src/main/resources/rules/annotations.drl",
      ks.getResources().newClassPathResource("rules/annotations.drl"));
    kfs.write("src/main/resources/rules/dimensions.drl",
      ks.getResources().newClassPathResource("rules/dimensions.drl"));
    KieBuilder kb = ks.newKieBuilder(kfs).buildAll();
    Results results = kb.getResults();
    if (results.hasMessages(Message.Level.ERROR)) {
      throw new IllegalStateException(results.toString());
    }
    return ks.newKieContainer(ks.getRepository().getDefaultReleaseId());
  }
}
