package com.app.dynamic.drools.service;

import com.app.dynamic.drools.entity.Rule;

import java.util.List;

/**
 * @author apanthi
 */
public interface RuleService {

  List<Rule> findAll();

  void addRule(Rule rule);

  void updateRule(Rule rule);

  void deleteRule(Long ruleId, String ruleName);
}
