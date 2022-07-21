package com.app.dynamic.drools.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author apanthi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rule {

  private Long id;

  private String kieBaseName;

  private String kiePackageName;

  private String ruleContent;

  private LocalDateTime createdDateTime;

  private LocalDateTime updatedDateTime;

  public void validate() {
    if (this.id == null
        || isBlank(kieBaseName)
        || isBlank(kiePackageName)
        || isBlank(ruleContent)) {
      throw new RuntimeException("Data validation error.");
    }
  }

  private boolean isBlank(String str) {
    return str == null || str.isEmpty();
  }
}
