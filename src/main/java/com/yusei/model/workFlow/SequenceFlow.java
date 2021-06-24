package com.yusei.model.workFlow;

import com.yusei.model.param.process.ProcessTriggerAddParam;
import java.util.List;
import lombok.Data;

@Data
public class SequenceFlow {

  private String id;
  private String name;
  private String action;//例如 提交，同意，驳回
  private String sourceRef;//连线的前节点id
  private String targetRef;//连线的后节点id
  private String fromX;
  private String fromY;
  private String toX;
  private String toY;

  private List<SequenceFlowFilter> sequenceFlowFilters;

  private String expression;

  private String filterRelation;

  //触发流程设置
  private List<ProcessTriggerAddParam> processTriggerAddParams;
}
