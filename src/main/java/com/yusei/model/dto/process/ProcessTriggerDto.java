package com.yusei.model.dto.process;

import lombok.Data;

@Data
public class ProcessTriggerDto {

  private String triggerType;

  private String processFunction;

  private String flowDefinitionKey;

  private Long slaveFormId;

  private String slaveFormName;

  private String slaveProcessDefinitionKey;
}
