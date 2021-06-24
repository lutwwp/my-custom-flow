package com.yusei.model.dto.process;

import java.util.Date;
import java.util.Map;
import lombok.Data;

@Data
public class ActTaskDto {

  private String id;
  private String name;
  private String taskDefinitionKey;
  private String assignee;
  private Date createTime;

  private Map<String, Object> taskLocalVariables;

  private Map<String, Object> processVariables;
}
