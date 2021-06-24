package com.yusei.model.param.process;

import com.yusei.model.workFlow.ProcessParam;
import java.util.List;
import lombok.Data;

/**
 * 流程信息的修改参数实体.
 */
@Data
public class ProcessInfoUpdateParam {

  private Long processInfoId;

  //流程图相关参数
  private ProcessParam processParam;

  //业务相关属性
  //流程监控人列表
  private List<ProcessGuarderAddParam> processGuarderAddParams;

  //流程操作功能
  private List<ProcessTriggerAddParam> processTriggerAddParams;

  //节点关联的表单字段
  private List<ProcessNodeFieldAddParam> processNodeFieldAddParams;
}
