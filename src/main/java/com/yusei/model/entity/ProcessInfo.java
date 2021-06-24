package com.yusei.model.entity;

import lombok.Data;

/**
 * 保存流程参数实体
 */
@Data
public class ProcessInfo {

  private Long processInfoId;
  private Long formId;

  private String processName;
  /**
   * 流程id
   */
  private String processDefinitionId;

  private String processDefinitionKey;

  private Integer version;
  /**
   * 流程定义信息 json格式保存
   */
  private String processParam;

  private byte[] processBytearray;

  private Integer processVersion;

  private Integer status;

  private Boolean deleted;
}
