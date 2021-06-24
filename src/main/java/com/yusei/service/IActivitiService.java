package com.yusei.service;

import com.yusei.common.PageParam;
import com.yusei.common.PageResult;
import com.yusei.model.dto.process.ProcessDetailDto;
import com.yusei.model.dto.process.ProcessInstanceDto;
import com.yusei.model.dto.process.ProcessLogDto;
import com.yusei.model.dto.process.ProcessTaskDto;
import com.yusei.model.entity.ProcessTrigger;
import com.yusei.model.param.FieldValueAddParam;
import com.yusei.model.param.process.ProcessQueryForm;
import com.yusei.model.workFlow.FlowChart;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface IActivitiService {

  void startTriggerProcess(Map<String,Object> variables, ProcessTrigger processTrigger);

  void startProcess(Long formId, String sponsorId, List<FieldValueAddParam> fieldValueAddParams);

  List<ProcessTaskDto> getPreHandleProcess(PageParam pageParam, ProcessQueryForm queryForm);

  Long getPreHandleProcessCount(ProcessQueryForm queryForm);

  List<ProcessInstanceDto> getMyStartedProcess(PageParam pageParam, ProcessQueryForm queryForm);

  Long getMyStartedProcessCount(ProcessQueryForm queryForm);

  List<ProcessInstanceDto> getHandleProcess(PageParam pageParam, ProcessQueryForm queryForm);

  Long getHandleProcessCount(ProcessQueryForm queryForm);

  PageResult<ProcessInstanceDto> getMyMonitoredProcess(PageParam pageParam, ProcessQueryForm queryForm);

  ProcessDetailDto getProcessDetail(String processInstanceId, String taskDefinitionKey, String userId);

  void claimTask(String taskId, String userId);

  void unclaimTask(String taskId, String userId);

  void completeTask(String taskId, String userId, List<FieldValueAddParam> fieldValueAddParams);

  void suspendProcess(String processInstanceId);

  void activeProcess(String processInstanceId);

  void endProcessInstance(String processInstanceId, String processFunction);

  void rollBackTask(String taskId, String userId);

  void revokeTask(String taskId, String userId);

  FlowChart getFlowChartByProcessId(String processInstanceId) throws IOException;

  List<ProcessLogDto> getProcessLog(String processInstanceId);
}
