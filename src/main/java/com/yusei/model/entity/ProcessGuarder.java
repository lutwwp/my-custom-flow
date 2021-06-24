package com.yusei.model.entity;

import lombok.Data;

/**
 * 流程监控人
 */
@Data
public class ProcessGuarder {

  private Long processGuarderId;

  private Long processInfoId;

  private String processDefinitionKey;

  private String processDefinitionId;

  private String userId;
}
