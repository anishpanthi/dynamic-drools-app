package com.app.dynamic.drools.controller;

import com.app.dynamic.drools.entity.Rule;
import com.app.dynamic.drools.service.RuleService;
import com.app.dynamic.drools.utils.RuleUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author apanthi
 */
@RestController
@RequiredArgsConstructor
public class RuleController {

  private final RuleService ruleService;

  private final RuleUtil ruleUtil;

  @GetMapping("/rules")
  public List<Rule> findAll() {
    return ruleService.findAll();
  }

  @PostMapping("/rules")
  public String addRule(@RequestBody Rule rule) {
    ruleService.addRule(rule);
    return "added successfully";
  }

  @PutMapping("/rules")
  public String updateRule(@RequestBody Rule rule) {
    ruleService.updateRule(rule);
    return "modified successfully";
  }

  @DeleteMapping("/rules")
  public String deleteRule(Long ruleId, String ruleName) {
    ruleService.deleteRule(ruleId, ruleName);
    return "deleted successfully";
  }

  @GetMapping("/fireRule/{kieBaseName}/{param}")
  public String fireRule(@PathVariable String kieBaseName, @PathVariable Integer param) {
    return ruleUtil.fireRule(kieBaseName, param);
  }
}
