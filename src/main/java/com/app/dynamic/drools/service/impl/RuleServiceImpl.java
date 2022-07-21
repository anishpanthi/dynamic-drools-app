package com.app.dynamic.drools.service.impl;

import com.app.dynamic.drools.entity.Rule;
import com.app.dynamic.drools.service.RuleService;
import com.app.dynamic.drools.utils.RuleUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author apanthi
 */
@Service
@Slf4j
public class RuleServiceImpl implements RuleService {

  @Resource private RuleUtil ruleUtil;

  /** Simulation database */
  private final Map<Long, Rule> ruleMap = new HashMap<>();

  @Override
  public List<Rule> findAll() {
    return new ArrayList<>(ruleMap.values());
  }

  @Override
  public void addRule(Rule rule) {
    rule.validate();
    rule.setCreatedDateTime(LocalDateTime.now());
    ruleMap.put(rule.getId(), rule);
    ruleUtil.addOrUpdateRule(rule);
  }

  @Override
  public void updateRule(Rule rule) {
    rule.validate();
    rule.setUpdatedDateTime(LocalDateTime.now());
    ruleMap.put(rule.getId(), rule);
    ruleUtil.addOrUpdateRule(rule);
  }

  @Override
  public void deleteRule(Long ruleId, String ruleName) {
    Rule rule = ruleMap.get(ruleId);
    if (null != rule) {
      ruleMap.remove(ruleId);
      ruleUtil.deleteDroolsRule(rule.getKieBaseName(), rule.getKiePackageName(), ruleName);
    }
  }
}
