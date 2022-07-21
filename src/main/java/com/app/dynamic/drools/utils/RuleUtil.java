package com.app.dynamic.drools.service;

import com.app.dynamic.drools.entity.Rule;
import lombok.extern.slf4j.Slf4j;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.compiler.kie.builder.impl.KieContainerImpl;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.runtime.KieContainer;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * @author apanthi
 */
@Service
@Slf4j
public class RuleService {

  private final KieServices kieServices = KieServices.get();

  private final KieFileSystem kieFileSystem = kieServices.newKieFileSystem();

  private final KieModuleModel kieModuleModel = kieServices.newKieModuleModel();

  private KieContainer kieContainer;

  /** Determine whether the kbase exists */
  public boolean existsKieBase(String kieBaseName) {
    if (null == kieContainer) {
      return false;
    }
    Collection<String> kieBaseNames = kieContainer.getKieBaseNames();
    if (kieBaseNames.contains(kieBaseName)) {
      return true;
    }
    log.info("need to create kiebase:{}", kieBaseName);
    return false;
  }

  public void deleteDroolsRule(String kieBaseName, String packageName, String ruleName) {
    if (existsKieBase(kieBaseName)) {
      KieBase kieBase = kieContainer.getKieBase(kieBaseName);
      kieBase.removeRule(packageName, ruleName);
      log.info("delete the rule: {}, {}, {}", kieBaseName, packageName, ruleName);
    }
  }

  /** Add or update drools rules */
  public void addOrUpdateRule(Rule rule) {
    // Get the name of kbase
    String kieBaseName = rule.getKieBaseName();
    // Determine whether the kbase exists
    boolean existsKieBase = existsKieBase(kieBaseName);
    // This object corresponds to kmodule Kbase tag in XML
    KieBaseModel kieBaseModel = null;
    if (!existsKieBase) {
      // Create a kbase
      kieBaseModel = kieModuleModel.newKieBaseModel(kieBaseName);
      // Not the default kiebase
      kieBaseModel.setDefault(false);
      // Set the package path that the kiebase needs to load
      kieBaseModel.addPackage(rule.getKiePackageName());
      // Set kiesession
      kieBaseModel
          .newKieSessionModel(kieBaseName + "-session")
          // Not the default session
          .setDefault(false);
    } else {
      // Get the existing kbase object
      kieBaseModel = kieModuleModel.getKieBaseModels().get(kieBaseName);
      // Get packages
      List<String> packages = kieBaseModel.getPackages();
      if (!packages.contains(rule.getKiePackageName())) {
        kieBaseModel.addPackage(rule.getKiePackageName());
        log.info("kiebase:{} add a new package: {}", kieBaseName, rule.getKiePackageName());
      } else {
        kieBaseModel = null;
      }
    }
    String file = "src/main/resources/" + rule.getKiePackageName() + "/" + rule.getId() + ".drl";
    log.info("load virtual rule file: {}", file);
    kieFileSystem.write(file, rule.getRuleContent());

    if (kieBaseModel != null) {
      String kModuleXml = kieModuleModel.toXML();
      log.info("load kmodule.xml:[\n{}]", kModuleXml);
      kieFileSystem.writeKModuleXML(kModuleXml);
    }

    KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem);
    // Build all kiebases under kiemodule through kiebuilder
    kieBuilder.buildAll();
    // Get the results of the build process
    Results results = kieBuilder.getResults();
    // Get error information
    List<Message> messages = results.getMessages(Message.Level.ERROR);
    if (null != messages && !messages.isEmpty()) {
      for (Message message : messages) {
        log.error(message.getText());
      }
      throw new RuntimeException("exception in loading rules");
    }
    // Kiecontainer only needs to be created for the first time, and then it is used
    if (null == kieContainer) {
      kieContainer = kieServices.newKieContainer(kieServices.getRepository().getDefaultReleaseId());
    } else {
      // Realize dynamic update
      ((KieContainerImpl) kieContainer)
          .updateToKieModule((InternalKieModule) kieBuilder.getKieModule());
    }
  }

  
}
