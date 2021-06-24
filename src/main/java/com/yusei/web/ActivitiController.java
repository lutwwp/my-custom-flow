package com.yusei.web;

import com.yusei.common.PageParam;
import com.yusei.common.PageResult;
import com.yusei.common.ReturnResult;
import com.yusei.model.dto.process.ProcessDetailDto;
import com.yusei.model.dto.process.ProcessInstanceDto;
import com.yusei.model.dto.process.ProcessLogDto;
import com.yusei.model.dto.process.ProcessTaskDto;
import com.yusei.model.param.FieldValueAddParam;
import com.yusei.model.param.process.ProcessQueryForm;
import com.yusei.model.workFlow.FlowChart;
import com.yusei.service.IActivitiService;
import com.yusei.utils.ResponseUtils;
import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/activiti")
public class ActivitiController {

  @Autowired
  private IActivitiService activitiService;

  /**
   * 发起流程.
   */
  @PostMapping("/startProcess")
  public ReturnResult startProcess(Long formId, String sponsorId,
      @RequestBody List<FieldValueAddParam> fieldValueAddParams) {
    activitiService.startProcess(formId, sponsorId, fieldValueAddParams);
    return ResponseUtils.ok();
  }

  /**
   * 分页查询我的待办流程.
   */
  @GetMapping("/getPreHandleProcess")
  public ReturnResult getPreHandleProcess(PageParam pageParam, ProcessQueryForm queryForm) {
    List<ProcessTaskDto> dtos = activitiService.getPreHandleProcess(pageParam, queryForm);
    Long total = activitiService.getPreHandleProcessCount(queryForm);
    PageResult<ProcessTaskDto> result = new PageResult<>(dtos, total);
    return ResponseUtils.ok(result);
  }

  /**
   * 分页查询我发起的流程.
   */
  @GetMapping("/getMyStartedProcess")
  public ReturnResult getMyStartedProcess(PageParam pageParam, ProcessQueryForm queryForm) {
    List<ProcessInstanceDto> dtos = activitiService.getMyStartedProcess(pageParam, queryForm);
    Long total = activitiService.getMyStartedProcessCount(queryForm);
    PageResult<ProcessInstanceDto> result = new PageResult<>(dtos, total);
    return ResponseUtils.ok(result);
  }

  /**
   * 分页查询我处理的流程.
   *
   * @param pageParam 分页参数
   * @return 我处理的流程列表
   */
  @GetMapping("/getHandleProcess")
  public ReturnResult getHandleProcess(PageParam pageParam, ProcessQueryForm queryForm) {
    List<ProcessInstanceDto> dtos = activitiService.getHandleProcess(pageParam, queryForm);
    Long total = activitiService.getHandleProcessCount(queryForm);
    PageResult<ProcessInstanceDto> result = new PageResult<>(dtos, total);
    return ResponseUtils.ok(result);
  }

  /**
   * 分页查询我监控的流程.
   *
   * @param pageParam 分页参数
   * @param queryForm 查询参数
   * @return
   */
  @GetMapping("/getMyMonitoredProcess")
  public ReturnResult getMyMonitoredProcess(PageParam pageParam, ProcessQueryForm queryForm) {
    PageResult<ProcessInstanceDto> result = activitiService.getMyMonitoredProcess(pageParam, queryForm);
    return ResponseUtils.ok(result);
  }

  /**
   * 查询流程详情.
   *
   * @param processInstanceId
   * @param taskDefinitionKey
   * @param userId
   * @return
   */
  @GetMapping("/getProcessDetail")
  public ReturnResult getProcessDetail(@RequestParam String processInstanceId,
      String taskDefinitionKey, String userId) {
    ProcessDetailDto dto = activitiService.getProcessDetail(processInstanceId, taskDefinitionKey, userId);
    return ResponseUtils.ok(dto);
  }

  /**
   * 领取任务.
   */
  @PostMapping("/claimTask")
  public ReturnResult claimTask(String userId, @RequestParam String taskId) {
    activitiService.claimTask(taskId, userId);
    return ResponseUtils.ok();
  }

  /**
   * 放弃任务.
   */
  @PostMapping("/unclaimTask")
  public ReturnResult unclaimTask(String userId, @RequestParam String taskId) {
    activitiService.unclaimTask(taskId, userId);
    return ResponseUtils.ok();
  }

  /**
   * 提交任务.
   *
   * @param taskId
   * @param userId
   * @param fieldValueAddParams
   * @return
   */
  @PostMapping("/completeTask")
  public ReturnResult completeTask(@RequestParam String taskId, String userId,
      @RequestBody List<FieldValueAddParam> fieldValueAddParams) {
    activitiService.completeTask(taskId, userId, fieldValueAddParams);
    return ResponseUtils.ok();
  }

  /**
   * 挂起流程.
   *
   * @param processInstanceId
   * @return
   */
  @PostMapping("/suspendProcess")
  public ReturnResult suspendProcess(String processInstanceId) {
    activitiService.suspendProcess(processInstanceId);
    return ResponseUtils.ok();
  }

  /**
   * 激活流程.
   *
   * @param processInstanceId
   * @return
   */
  @PostMapping("/activeProcess")
  public ReturnResult activeProcess(String processInstanceId) {
    activitiService.activeProcess(processInstanceId);
    return ResponseUtils.ok();
  }

  /**
   * 结束流程.
   *
   * @param processInstanceId
   * @param processFunction
   * @return
   */
  @PostMapping("/endProcessInstance")
  public ReturnResult endProcessInstance(String processInstanceId, String processFunction) {
    activitiService.endProcessInstance(processInstanceId, processFunction);
    return ResponseUtils.ok();
  }

  /**
   * 回退（默认上一级）
   */
  @PostMapping("/rollBackTask")
  public ReturnResult rollBackTask(String taskId, String userId) {
    activitiService.rollBackTask(taskId, userId);
    return ResponseUtils.ok();
  }

  /**
   * 撤销.
   *
   * @param taskId
   * @param userId
   * @return
   */
  @PostMapping("/revokeTask")
  public ReturnResult revokeTask(String taskId, String userId) {
    activitiService.revokeTask(taskId, userId);
    return ResponseUtils.ok();
  }

  /**
   * 查询流程图.
   */
  @GetMapping("/getFlowChartByProcessId")
  public ReturnResult getFlowChartByProcessId(@RequestParam String processInstanceId)
      throws IOException {
    FlowChart flowChart = activitiService.getFlowChartByProcessId(processInstanceId);
    return ResponseUtils.ok(flowChart);
  }

  @GetMapping("/getProcessLog")
  public ReturnResult getProcessLog(@RequestParam String processInstanceId) {
    List<ProcessLogDto> dtos = activitiService.getProcessLog(processInstanceId);
    return ResponseUtils.ok(dtos);
  }
}
