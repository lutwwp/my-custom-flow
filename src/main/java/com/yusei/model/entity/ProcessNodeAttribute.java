package com.yusei.model.entity;

import lombok.Data;

@Data
public class ProcessNodeAttribute {

  private Long processNodeAttributeId;

  private Long processInfoId;

  private String processDefinitionKey;

  private String processDefinitionId;

  private String taskDefinitionKey;
  //是否回退
  private Boolean rollBack;
  //是否撤销
  private Boolean revoke;
}
