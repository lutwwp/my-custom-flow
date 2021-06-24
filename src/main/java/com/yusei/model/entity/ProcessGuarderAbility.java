package com.yusei.model.entity;

import lombok.Data;

/**
 * 流程监控人权限
 */
@Data
public class ProcessGuarderAbility {

  private Long processGuarderAbilityId;

  private Long processInfoId;

  private String processDefinitionKey;

  private String processDefinitionId;

  private String userId;

  private String processFunction;
}
