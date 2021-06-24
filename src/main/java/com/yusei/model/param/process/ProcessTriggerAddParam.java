package com.yusei.model.param.process;

import lombok.Data;

@Data
public class ProcessTriggerAddParam {

  private String processFunction;

  private Long slaveFormId;

  private String slaveProcessDefinitionKey;
}
