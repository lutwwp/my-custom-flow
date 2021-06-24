package com.yusei.web;

import com.yusei.model.dto.process.ActTaskDto;
import com.yusei.common.PageParam;
import com.yusei.common.ReturnResult;
import com.yusei.exception.BaseException;
import com.yusei.utils.ResponseUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/actTest")
@Deprecated
public class ActivitiTestController {

  @Autowired
  private RepositoryService repositoryService;

  @Autowired
  private RuntimeService runtimeService;

  @Autowired
  private HistoryService historyService;

  @Autowired
  private TaskService taskService;

  @Autowired
  private IdentityService identityService;

  /**
   * 发布
   */
  @PostMapping("/deploy")
  public ReturnResult deployProcess(String url) {

    Deployment deployment = repositoryService.createDeployment()
        .name("流程测试")
        .addClasspathResource(url)
        .deploy();
    return ResponseUtils.ok();
  }

  @PostMapping("/startProcess")
  public ReturnResult startProcess(String processDefinitionKey, String sponsorId, String number) {
    identityService.setAuthenticatedUserId(sponsorId);
    ProcessInstance processInstance;
    if (number != null) {
      Map<String, Object> map = new HashMap<>();
      map.put("weekend", number);
      processInstance = runtimeService
          .startProcessInstanceByKey(processDefinitionKey, map);
    } else {
      processInstance = runtimeService
          .startProcessInstanceByKey(processDefinitionKey);
    }
    return ResponseUtils.ok(processInstance.getProcessDefinitionId());
  }

  @GetMapping("/getPreHandleProcess")
  @Transactional
  public ReturnResult getPreHandleProcess(String userId, PageParam pageParam) {
    //获取我的待办任务
    List<Task> tasks = taskService.createTaskQuery().taskCandidateOrAssigned(userId).list();
    List<ActTaskDto> taskDtoList = new ArrayList<>();
    for (Task task : tasks) {
      ActTaskDto taskDto = new ActTaskDto();
      taskDto.setId(task.getId());
      taskDto.setName(task.getName());
      taskDto.setTaskDefinitionKey(task.getTaskDefinitionKey());
      taskDtoList.add(taskDto);
    }
    return ResponseUtils.ok(taskDtoList);
  }

  @PostMapping("/completeTask")
  public ReturnResult completeTask(String taskId, String userId, Integer number) {
    // 根据任务ID查询任务实例
    Task task = taskService.createTaskQuery().taskId(taskId).singleResult();

    if (task == null) {
      return ResponseUtils.error("任务不存在");
    }
    //设置任务的指向人
    taskService.setAssignee(taskId, userId);
    if (number != null) {
      Map<String, Object> map = new HashMap<>();
      map.put("number", number);
      //完成任务
      taskService.complete(taskId, map);
    } else {
      taskService.complete(taskId);
    }
    return ResponseUtils.ok();
  }

  /**
   * 回退（默认上一级）
   */
  @PostMapping("/rollBackTask")
  public ResponseEntity<?> rollBackTask(String taskId) {
// 1,根据任务ID查询任务实例
    Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
    if (task == null) {
      throw new BaseException("当前任务不存在");
    }
    Execution execution = runtimeService.createExecutionQuery().executionId(task.getExecutionId())
        .singleResult();
    String currentActId = execution.getActivityId();

    BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
    FlowNode flowNode = (FlowNode) bpmnModel.getFlowElement(currentActId);
    List<SequenceFlow> incomingFlows = flowNode.getIncomingFlows();
    List<SequenceFlow> outgoingFlows = flowNode.getOutgoingFlows();
    List<SequenceFlow> newOutFlows = new ArrayList<>();
    int i = 0;
    for (SequenceFlow sequenceFlow : incomingFlows) {
      i++;
      SequenceFlow newOutFlow = new SequenceFlow();
      FlowElement source = sequenceFlow.getSourceFlowElement();
      newOutFlow.setSourceRef(flowNode.getId());
      newOutFlow.setSourceFlowElement(flowNode);
      newOutFlow.setTargetRef(source.getId());
      newOutFlow.setTargetFlowElement(source);
      newOutFlow.setId("autoFlow" + i);
      newOutFlow.setName("回退临时连线" + i);
      newOutFlows.add(newOutFlow);
    }
    flowNode.setOutgoingFlows(newOutFlows);
    taskService.complete(taskId);
    //回复连线
    flowNode.setOutgoingFlows(outgoingFlows);
    return ResponseEntity.ok("");
  }

  @PostMapping("/revokeTask")
  public ResponseEntity<?> revokeTask(String taskId) {
    //1.查询当前历史任务
    HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery()
        .taskId(taskId).singleResult();
    //2.判断当前流程实例是否还在进行中
    ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
        .processInstanceId(historicTaskInstance.getProcessInstanceId()).singleResult();
    if (processInstance == null) {
      throw new BaseException("当前流程已结束，不能撤销");
    }
    return null;
  }
}
