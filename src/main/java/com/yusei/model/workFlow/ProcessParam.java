package com.yusei.model.workFlow;

import java.util.List;
import lombok.Data;

@Data
public class ProcessParam {

  private String id;
  private String name;
  private StartEvent startEvent;
  private List<EndEvent> endEventList;
  private List<UserTask> userTaskList;
  private List<SequenceFlow> sequenceFlowList;
  private List<SubProcessParam> subProcessList;
  private List<String> selectedIdList;//选中的节点id列表
  private List<ParallelGateway> parallelGatewayList; //并行网关
}
