package com.yusei.model.entity;

import lombok.Data;

@Data
public class ProcessTrigger {

  private Long processTriggerId;

  private Long processInfoId;

  private String masterProcessDefinitionKey;

  private String masterProcessDefinitionId;

  private String triggerType;

  private String processFunction;

  private String flowDefinitionKey;

  private Long slaveFormId;

  private String slaveProcessDefinitionKey;


}
