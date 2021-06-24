package com.yusei.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yusei.common.PageParam;
import com.yusei.common.PageResult;
import com.yusei.constant.FlowConstant;
import com.yusei.enums.DefaultValueTypeEnum;
import com.yusei.enums.FieldTypeEnum;
import com.yusei.enums.ProcessFunctionEnum;
import com.yusei.enums.ProcessStatusEnum;
import com.yusei.exception.BaseException;
import com.yusei.extend.MyCreateExecutionCmd;
import com.yusei.extend.MyCreateTaskCmd;
import com.yusei.extend.MyDeleteTaskCmd;
import com.yusei.extend.MyEndProcessCmd;
import com.yusei.extend.MyUpdateHistoricTaskCmd;
import com.yusei.model.dto.FieldValueDetailDto;
import com.yusei.model.dto.process.ActTaskDto;
import com.yusei.model.dto.process.ProcessDetailDto;
import com.yusei.model.dto.process.ProcessInstanceDto;
import com.yusei.model.dto.process.ProcessLogDto;
import com.yusei.model.dto.process.ProcessTaskDto;
import com.yusei.model.entity.Field;
import com.yusei.model.entity.ProcessGuarder;
import com.yusei.model.entity.ProcessGuarderAbility;
import com.yusei.model.entity.ProcessInfo;
import com.yusei.model.entity.ProcessNodeAttribute;
import com.yusei.model.entity.ProcessNodeField;
import com.yusei.model.entity.ProcessTrigger;
import com.yusei.model.param.FieldValueAddParam;
import com.yusei.model.param.process.ProcessQueryForm;
import com.yusei.model.workFlow.FlowChart;
import com.yusei.model.workFlow.ProcessParam;
import com.yusei.service.IActivitiService;
import com.yusei.service.IFieldService;
import com.yusei.service.IFieldValueService;
import com.yusei.service.ILinkFilterService;
import com.yusei.service.IProcessGuarderAbilityService;
import com.yusei.service.IProcessGuarderService;
import com.yusei.service.IProcessInfoService;
import com.yusei.service.IProcessNodeAttributeService;
import com.yusei.service.IProcessNodeFieldService;
import com.yusei.service.IProcessTriggerService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.TaskServiceImpl;
import org.activiti.engine.impl.interceptor.CommandExecutor;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ExecutionEntityImpl;
import org.activiti.engine.impl.persistence.entity.HistoricActivityInstanceEntity;
import org.activiti.engine.impl.persistence.entity.HistoricTaskInstanceEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntityImpl;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ActivitiService implements IActivitiService {

  //流程
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

  //业务
  @Autowired
  private IProcessNodeFieldService processNodeFieldService;

  @Autowired
  private IFieldService fieldService;

  @Autowired
  private IFieldValueService fieldValueService;

  @Autowired
  private ILinkFilterService linkFilterService;

  @Autowired
  private IProcessInfoService processInfoService;

  @Autowired
  private IProcessNodeAttributeService processNodeAttributeService;

  @Autowired
  private IProcessGuarderService processGuarderService;

  @Autowired
  private IProcessGuarderAbilityService processGuarderAbilityService;

  @Autowired
  private IProcessTriggerService processTriggerService;

  @Override
  public void startTriggerProcess(Map<String, Object> variables, ProcessTrigger processTrigger) {
    Long formId = processTrigger.getSlaveFormId();
    String processDefinitionKey = processTrigger.getSlaveProcessDefinitionKey();
    ProcessInfo processInfo = processInfoService
        .getActiveProcessByProcessDefinitionKey(processDefinitionKey);
    if (processInfo == null) {
      return;
    }
    //流程启动时表单需要新增的数据
    List<FieldValueAddParam> fieldValueAddParams = new ArrayList<>();
    if (variables != null) {
      //获取当前流程表单启动时需要新增的字段
      List<ProcessNodeField> processNodeFields = processNodeFieldService
          .getByInfoIdAndTaskKey(processInfo.getProcessInfoId(), "theStart");
      for (ProcessNodeField processNodeField : processNodeFields) {
        if (processNodeField.getEditField() != null && processNodeField.getEditField()) {
          FieldValueAddParam fieldValueAddParam = new FieldValueAddParam();
          Long fieldId = processNodeField.getFieldId();
          fieldValueAddParam.setFieldId(fieldId);
          Field fieldInfoById = fieldService.getFieldInfoById(fieldId);
          //查询此字段的关联值
          if (FieldTypeEnum.SELECT.name().equals(fieldInfoById.getFieldType())) {
            if (DefaultValueTypeEnum.LINK_FORM.name().equals(fieldInfoById.getDefaultValueType())) {
              //下拉框的关联表单
              Long linkFieldId = fieldInfoById.getLinkFieldId();
              Object fieldValue = variables.get("field" + linkFieldId);
              if (fieldValue != null) {
                fieldValueAddParam.setFieldValue(fieldValue.toString());
                fieldValueAddParams.add(fieldValueAddParam);
              }
            }
          }
        }
      }
    }
    Long primaryKeyValue = fieldValueService.addFormData(formId, fieldValueAddParams);
    Map<String, Object> thisVariables = new HashMap<>();
    thisVariables.put(FlowConstant.PROCESS_FAIL, false);
    for (FieldValueAddParam param : fieldValueAddParams) {
      thisVariables.put("field" + param.getFieldId(), param.getFieldValue());
    }
    //启动触发流程,暂时未设置启动人
    runtimeService
        .startProcessInstanceById(processInfo.getProcessDefinitionId(), primaryKeyValue.toString(),
            thisVariables);
  }

  /**
   * 发起流程.
   */
  @Override
  @Transactional
  public void startProcess(Long formId, String sponsorId,
      List<FieldValueAddParam> fieldValueAddParams) {
    //1.获取此表单的已发布版本
    ProcessInfo processInfo = processInfoService.getActiveProcessByFormId(formId);
    if (processInfo == null) {
      throw new BaseException("当前表单流程未发布");
    }
    //2.找到此流程开启流程节点对应的流程字段
    List<ProcessNodeField> processNodeFields = processNodeFieldService
        .getByInfoIdAndTaskKey(processInfo.getProcessInfoId(), "theStart");

    //开启流程时新增表单新建数据
    Long primaryKeyValue = fieldValueService.addFormData(formId, fieldValueAddParams);
    if (primaryKeyValue == null) {
      throw new BaseException("创建流程记录失败");
    }
    identityService.setAuthenticatedUserId(sponsorId);
    //3.将流程表单数据作为全局参数写入流程实例中并开启流程
    Map<String, Object> variables = new HashMap<>();
    variables.put(FlowConstant.PROCESS_FAIL, false);
    for (FieldValueAddParam param : fieldValueAddParams) {
      variables.put("field" + param.getFieldId(), param.getFieldValue());
    }
    try {
      ProcessInstance processInstance = runtimeService
          .startProcessInstanceById(processInfo.getProcessDefinitionId(),
              primaryKeyValue.toString(), variables);
    } catch (ActivitiException e) {
      throw new BaseException("无法流转到下个流程节点");
    }
  }

  @Override
  public List<ProcessTaskDto> getPreHandleProcess(PageParam pageParam,
      ProcessQueryForm queryForm) {
    List<ProcessTaskDto> dtos = new ArrayList<>();
    String querySql = getPreHandleProcessQuerySql(queryForm);
    List<Task> tasks = taskService.createNativeTaskQuery()
        .sql(querySql)
        .listPage(pageParam.getOffset(), pageParam.getEndIndex());
    for (Task task : tasks) {
      ProcessTaskDto dto = new ProcessTaskDto();
      ProcessInstance processInstance = runtimeService
          .createProcessInstanceQuery().processInstanceId(task.getProcessInstanceId())
          .singleResult();
      dto.setProcessInstanceId(task.getProcessInstanceId());
      dto.setProcessName(processInstance.getProcessDefinitionName());
      dto.setStartTime(processInstance.getStartTime());
      dto.setSponsorId(processInstance.getStartUserId());
      if (processInstance.isSuspended()) {
        dto.setProcessStatus(ProcessStatusEnum.SUSPEND.getProcessStatus());
      } else {
        dto.setProcessStatus(ProcessStatusEnum.PENDING.getProcessStatus());
      }
      dto.setTaskId(task.getId());
      dto.setTaskName(task.getName());
      dto.setTaskDefinitionKey(task.getTaskDefinitionKey());
      dto.setTaskCreateTime(task.getCreateTime());
      dto.setClaimTask(task.getAssignee() != null);
      //根据流程id查询流程摘要
      List<FieldValueDetailDto> summaryList = getProcessSummary(
          processInstance.getProcessDefinitionId(), processInstance.getBusinessKey());
      dto.setSummaryList(summaryList);
      dtos.add(dto);
    }
    return dtos;
  }

  private String getPreHandleProcessQuerySql(ProcessQueryForm queryForm) {
    String mainSql = "select distinct a.*, b.start_time_ from act_ru_task a "
        + "inner join act_hi_procinst b on a.proc_inst_id_ = b.id_ "
        + "inner join act_re_procdef c on a.proc_def_id_ = c.id_ "
        + "left join act_ru_identitylink d on a.id_ = d.task_id_ "
        + "where case "
        + "when a.assignee_ is null then d.user_id_ = '" + queryForm.getUserId() + "' "
        + "else a.assignee_ = '" + queryForm.getUserId() + "' end";
    StringBuilder sb = new StringBuilder(mainSql);
    if (StringUtils.isNotEmpty(queryForm.getProcessName())) {
      sb.append(" and c.name_ ilike '%").append(queryForm.getProcessName()).append("%'");
    }
    if (StringUtils.isNotEmpty(queryForm.getSponsorId())) {
      sb.append(" and b.start_user_id_ = '").append(queryForm.getSponsorId()).append("'");
    }
    sb.append(" order by b.start_time_ desc");
    return sb.toString();
  }

  @Override
  public Long getPreHandleProcessCount(ProcessQueryForm queryForm) {
    String querySql = getPreHandleProcessCountQuerySql(queryForm);
    long total = taskService.createNativeTaskQuery()
        .sql(querySql)
        .count();
    return total;
  }

  private String getPreHandleProcessCountQuerySql(ProcessQueryForm queryForm) {
    String mainSql = "select count(distinct a.*) from act_ru_task a "
        + "inner join act_hi_procinst b on a.proc_inst_id_ = b.id_ "
        + "inner join act_re_procdef c on a.proc_def_id_ = c.id_ "
        + "left join act_ru_identitylink d on a.id_ = d.task_id_ "
        + "where case "
        + "when a.assignee_ is null then d.user_id_ = '" + queryForm.getUserId() + "' "
        + "else a.assignee_ = '" + queryForm.getUserId() + "' end";
    StringBuilder sb = new StringBuilder(mainSql);
    if (StringUtils.isNotEmpty(queryForm.getProcessName())) {
      sb.append(" and c.name_ ilike '%").append(queryForm.getProcessName()).append("%'");
    }
    if (StringUtils.isNotEmpty(queryForm.getSponsorId())) {
      sb.append(" and b.start_user_id_ = '").append(queryForm.getSponsorId()).append("'");
    }
    return sb.toString();
  }

  @Override
  public List<ProcessInstanceDto> getMyStartedProcess(PageParam pageParam,
      ProcessQueryForm queryForm) {
    List<ProcessInstanceDto> dtos = new ArrayList<>();
    List<HistoricProcessInstance> historicProcessInstanceList;
    if (StringUtils.isNotEmpty(queryForm.getProcessName())) {
      historicProcessInstanceList = historyService.createNativeHistoricProcessInstanceQuery()
          .sql("select p.* from act_hi_procinst p "
              + "inner join act_re_procdef d on p.proc_def_id_ = d.id_ "
              + "where p.start_user_id_ = #{userId} and d.name_ ilike '%" + queryForm
              .getProcessName() + "%' "
              + "order by p.start_time_ desc")
          .parameter("userId", queryForm.getUserId())
          .listPage(pageParam.getOffset(), pageParam.getEndIndex());
    } else {
      historicProcessInstanceList = historyService.createHistoricProcessInstanceQuery()
          .startedBy(queryForm.getUserId())
          .orderByProcessInstanceStartTime().desc()
          .listPage(pageParam.getOffset(), pageParam.getEndIndex());
    }
    for (HistoricProcessInstance historicProcessInstance : historicProcessInstanceList) {
      ProcessInstanceDto processInstanceDto = new ProcessInstanceDto();
      processInstanceDto.setProcessInstanceId(historicProcessInstance.getId());
      ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
          .processDefinitionId(historicProcessInstance.getProcessDefinitionId()).singleResult();
      processInstanceDto
          .setProcessName(processDefinition.getName());
      processInstanceDto.setStartTime(historicProcessInstance.getStartTime());
      processInstanceDto.setEndTime(historicProcessInstance.getEndTime());
      ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
          .processInstanceId(historicProcessInstance.getId()).singleResult();
      if (processInstance == null) {
        processInstanceDto.setProcessStatus(ProcessStatusEnum.OVER.getProcessStatus());
      } else {
        if (processInstance.isSuspended()) {
          processInstanceDto.setProcessStatus(ProcessStatusEnum.SUSPEND.getProcessStatus());
        } else {
          processInstanceDto.setProcessStatus(ProcessStatusEnum.PENDING.getProcessStatus());
        }
        //流程进行中，要查出当前流程进行中的节点名称
        List<HistoricActivityInstance> historicActivities = historyService
            .createHistoricActivityInstanceQuery()
            .unfinished().processInstanceId(historicProcessInstance.getId()).list();
        List<ActTaskDto> taskDtoList = new ArrayList<>();
        for (HistoricActivityInstance historicActivity : historicActivities) {
          ActTaskDto taskDto = new ActTaskDto();
          taskDto.setId(historicActivity.getTaskId());
          taskDto.setName(historicActivity.getActivityName());
          taskDto.setTaskDefinitionKey(historicActivity.getActivityId());
          taskDtoList.add(taskDto);
        }
        processInstanceDto.setTaskDtoList(taskDtoList);
      }
      //根据流程id查询流程摘要
      List<FieldValueDetailDto> summaryList = getProcessSummary(
          historicProcessInstance.getProcessDefinitionId(),
          historicProcessInstance.getBusinessKey());
      processInstanceDto.setSummaryList(summaryList);
      dtos.add(processInstanceDto);
    }
    return dtos;
  }

  @Override
  public Long getMyStartedProcessCount(ProcessQueryForm queryForm) {
    long total;
    if (StringUtils.isNotEmpty(queryForm.getProcessName())) {
      total = historyService.createNativeHistoricProcessInstanceQuery()
          .sql("select count(p.*) from act_hi_procinst p "
              + "inner join act_re_procdef d on p.proc_def_id_ = d.id_ "
              + "where p.start_user_id_ = #{userId} and d.name_ ilike '%"
              + queryForm.getProcessName() + "%' ")
          .parameter("userId", queryForm.getUserId())
          .count();
    } else {
      total = historyService.createHistoricProcessInstanceQuery()
          .startedBy(queryForm.getUserId())
          .count();
    }
    return total;
  }

  @Override
  public List<ProcessInstanceDto> getHandleProcess(PageParam pageParam,
      ProcessQueryForm queryForm) {
    List<ProcessInstanceDto> dtos = new ArrayList<>();
    List<HistoricTaskInstance> historicTasks;
    if (StringUtils.isNotEmpty(queryForm.getProcessName())) {
      historicTasks = historyService.createNativeHistoricTaskInstanceQuery()
          .sql("select t.* from act_hi_taskinst t "
              + "inner join act_re_procdef d on p.proc_def_id_ = d.id_ "
              + "where t.assignee_ = #{userId} and d.name_ ilike '%" + queryForm.getProcessName()
              + "%'")
          .parameter("userId", queryForm.getUserId())
          .list();
    } else {
      historicTasks = historyService.createHistoricTaskInstanceQuery().finished()
          .taskAssignee(queryForm.getUserId()).list();
    }
    //获取流程实例id的set
    Set<String> processInstanceIdSet = new HashSet<>();
    for (HistoricTaskInstance HistoricTask : historicTasks) {
      processInstanceIdSet.add(HistoricTask.getProcessInstanceId());
    }
    if (processInstanceIdSet.size() > 0) {
      List<HistoricProcessInstance> historicProcessInstanceList;
      if (StringUtils.isNotEmpty(queryForm.getSponsorId())) {
        historicProcessInstanceList = historyService.createHistoricProcessInstanceQuery()
            .processInstanceIds(processInstanceIdSet)
            .startedBy(queryForm.getSponsorId())
            .orderByProcessInstanceStartTime().desc()
            .listPage(pageParam.getOffset(), pageParam.getEndIndex());
      } else {
        historicProcessInstanceList = historyService
            .createHistoricProcessInstanceQuery()
            .processInstanceIds(processInstanceIdSet)
            .orderByProcessInstanceStartTime().desc()
            .listPage(pageParam.getOffset(), pageParam.getEndIndex());
      }
      for (HistoricProcessInstance historicProcessInstance : historicProcessInstanceList) {
        ProcessInstanceDto processInstanceDto = new ProcessInstanceDto();
        processInstanceDto.setProcessInstanceId(historicProcessInstance.getId());
        processInstanceDto
            .setProcessName(historicProcessInstance.getProcessDefinitionName());
        processInstanceDto.setStartTime(historicProcessInstance.getStartTime());
        processInstanceDto.setEndTime(historicProcessInstance.getEndTime());
        processInstanceDto.setSponsorId(historicProcessInstance.getStartUserId());
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
            .processInstanceId(historicProcessInstance.getId()).singleResult();
        if (processInstance == null) {
          processInstanceDto.setProcessStatus(ProcessStatusEnum.OVER.getProcessStatus());
        } else {
          if (processInstance.isSuspended()) {
            processInstanceDto.setProcessStatus(ProcessStatusEnum.SUSPEND.getProcessStatus());
          } else {
            processInstanceDto.setProcessStatus(ProcessStatusEnum.PENDING.getProcessStatus());
          }
        }
        List<ActTaskDto> taskDtoList = new ArrayList<>();
        List<HistoricTaskInstance> myHandledTasks = historyService
            .createHistoricTaskInstanceQuery().finished()
            .processInstanceId(historicProcessInstance.getId())
            .taskAssignee(queryForm.getUserId()).list();
        for (HistoricTaskInstance myHandledTask : myHandledTasks) {
          ActTaskDto taskDto = new ActTaskDto();
          taskDto.setId(myHandledTask.getId());
          taskDto.setName(myHandledTask.getName());
          taskDto.setTaskDefinitionKey(myHandledTask.getTaskDefinitionKey());
          taskDtoList.add(taskDto);
        }
        processInstanceDto.setTaskDtoList(taskDtoList);
        //根据流程id查询流程摘要
        List<FieldValueDetailDto> summaryList = getProcessSummary(
            historicProcessInstance.getProcessDefinitionId(),
            historicProcessInstance.getBusinessKey());
        processInstanceDto.setSummaryList(summaryList);
        dtos.add(processInstanceDto);
      }
    }
    return dtos;
  }

  @Override
  public Long getHandleProcessCount(ProcessQueryForm queryForm) {
    long total = 0;
    List<HistoricTaskInstance> historicTasks;
    if (StringUtils.isNotEmpty(queryForm.getProcessName())) {
      historicTasks = historyService.createNativeHistoricTaskInstanceQuery()
          .sql("select t.* from act_hi_taskinst t "
              + "inner join act_re_procdef d on p.proc_def_id_ = d.id_ "
              + "where t.assignee_ = #{userId} and d.name_ ilike '%" + queryForm.getProcessName()
              + "%'")
          .parameter("userId", queryForm.getUserId())
          .list();
    } else {
      historicTasks = historyService.createHistoricTaskInstanceQuery().finished()
          .taskAssignee(queryForm.getUserId()).list();
    }
    //获取流程实例id的set
    Set<String> processInstanceIdSet = new HashSet<>();
    for (HistoricTaskInstance HistoricTask : historicTasks) {
      processInstanceIdSet.add(HistoricTask.getProcessInstanceId());
    }
    if (processInstanceIdSet.size() > 0) {
      if (StringUtils.isNotEmpty(queryForm.getSponsorId())) {
        total = historyService.createHistoricProcessInstanceQuery()
            .processInstanceIds(processInstanceIdSet)
            .startedBy(queryForm.getSponsorId())
            .count();
      } else {
        total = historyService.createHistoricProcessInstanceQuery()
            .processInstanceIds(processInstanceIdSet)
            .count();
      }
    }
    return total;
  }

  @Override
  public PageResult<ProcessInstanceDto> getMyMonitoredProcess(PageParam pageParam,
      ProcessQueryForm queryForm) {
    List<ProcessInstanceDto> dtos = new ArrayList<>();
    long total = 0;
    //1.获取我监控的所有流程
    List<ProcessGuarder> processGuarders = processGuarderService.getByUserId(queryForm.getUserId());
    Set<String> deploymentIdSet = new HashSet<>();
    for (ProcessGuarder processGuarder : processGuarders) {
      ProcessInfo processInfo = processInfoService
          .getProcessInfoById(processGuarder.getProcessInfoId());
      if (processInfo != null) {
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
            .processDefinitionId(processInfo.getProcessDefinitionId()).singleResult();
        if (processDefinition != null) {
          deploymentIdSet.add(processDefinition.getDeploymentId());
        }
      }
    }
    if (deploymentIdSet.size() > 0) {
      //2.根据查询条件做不同的处理
      List<String> deploymentIdList = new ArrayList<>(deploymentIdSet);
      List<HistoricProcessInstance> historicProcessInstances;
      if (StringUtils.isNotEmpty(queryForm.getProcessName())) {
        List<ProcessDefinition> processDefinitions = repositoryService
            .createNativeProcessDefinitionQuery()
            .sql("select * from act_re_procdef where name_ ilike '%"
                + queryForm.getProcessName() + "%'")
            .list();
        if (processDefinitions.size() > 0) {
          deploymentIdList = new ArrayList<>();
          for (ProcessDefinition processDefinition : processDefinitions) {
            if (deploymentIdSet.contains(processDefinition.getDeploymentId())) {
              deploymentIdList.add(processDefinition.getDeploymentId());
            }
          }
        }
      }
      //3.查询最终结果
      if (deploymentIdList.size() > 0) {
        if (StringUtils.isNotEmpty(queryForm.getSponsorId())) {
          historicProcessInstances = historyService.createHistoricProcessInstanceQuery()
              .deploymentIdIn(deploymentIdList)
              .startedBy(queryForm.getSponsorId())
              .orderByProcessInstanceStartTime().desc()
              .listPage(pageParam.getOffset(), pageParam.getEndIndex());
          total = historyService.createHistoricProcessInstanceQuery()
              .deploymentIdIn(deploymentIdList)
              .startedBy(queryForm.getSponsorId())
              .count();
        } else {
          historicProcessInstances = historyService.createHistoricProcessInstanceQuery()
              .deploymentIdIn(deploymentIdList)
              .orderByProcessInstanceStartTime().desc()
              .listPage(pageParam.getOffset(), pageParam.getEndIndex());
          total = historyService.createHistoricProcessInstanceQuery()
              .deploymentIdIn(deploymentIdList)
              .count();
        }
        for (HistoricProcessInstance historicProcessInstance : historicProcessInstances) {
          ProcessInstanceDto processInstanceDto = new ProcessInstanceDto();
          processInstanceDto.setProcessInstanceId(historicProcessInstance.getId());
          processInstanceDto
              .setProcessName(historicProcessInstance.getProcessDefinitionName());
          processInstanceDto.setStartTime(historicProcessInstance.getStartTime());
          processInstanceDto.setEndTime(historicProcessInstance.getEndTime());
          processInstanceDto.setSponsorId(historicProcessInstance.getStartUserId());
          ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
              .processInstanceId(historicProcessInstance.getId()).singleResult();
          if (processInstance == null) {
            processInstanceDto.setProcessStatus(ProcessStatusEnum.OVER.getProcessStatus());
          } else {
            if (processInstance.isSuspended()) {
              processInstanceDto.setProcessStatus(ProcessStatusEnum.SUSPEND.getProcessStatus());
            } else {
              processInstanceDto.setProcessStatus(ProcessStatusEnum.PENDING.getProcessStatus());
            }
            List<HistoricActivityInstance> historicActivities = historyService
                .createHistoricActivityInstanceQuery()
                .unfinished().processInstanceId(historicProcessInstance.getId()).list();
            List<ActTaskDto> taskDtoList = new ArrayList<>();
            for (HistoricActivityInstance historicActivity : historicActivities) {
              ActTaskDto taskDto = new ActTaskDto();
              taskDto.setId(historicActivity.getTaskId());
              taskDto.setName(historicActivity.getActivityName());
              taskDto.setTaskDefinitionKey(historicActivity.getActivityId());
              taskDtoList.add(taskDto);
            }
            processInstanceDto.setTaskDtoList(taskDtoList);
          }
          //根据流程id查询流程摘要
          List<FieldValueDetailDto> summaryList = getProcessSummary(
              historicProcessInstance.getProcessDefinitionId(),
              historicProcessInstance.getBusinessKey());
          processInstanceDto.setSummaryList(summaryList);
          dtos.add(processInstanceDto);
        }
      }
    }
    return new PageResult<>(dtos, total);
  }

  private List<FieldValueDetailDto> getProcessSummary(String processDefinitionId,
      String businessKey) {
    List<FieldValueDetailDto> summaryList = new ArrayList<>();
    ProcessInfo processInfo = processInfoService
        .getProcessInfoByProcessDefinitionId(processDefinitionId);
    //查询出流程开始节点的字段
    List<ProcessNodeField> theStart = processNodeFieldService
        .getByInfoIdAndTaskKey(processInfo.getProcessInfoId(), "theStart");
    if (theStart.size() > 0) {
      //根据主键查询流程表单的数据
      List<FieldValueDetailDto> formDataDetail = fieldValueService
          .getFormDataDetail(processInfo.getFormId(), Long.valueOf(businessKey));
      Map<Long, List<FieldValueDetailDto>> groupByFieldId = formDataDetail.stream()
          .collect(Collectors.groupingBy(FieldValueDetailDto::getFieldId));
      for (ProcessNodeField processNodeField : theStart) {
        if (processNodeField.getSummary() != null && processNodeField.getSummary()) {
          List<FieldValueDetailDto> fieldValues = groupByFieldId.get(processNodeField.getFieldId());
          if (fieldValues != null && fieldValues.size() > 0) {
            FieldValueDetailDto fieldValue = fieldValues.get(0);
            summaryList.add(fieldValue);
          }
        }
      }
    }
    return summaryList;
  }

  @Override
  public ProcessDetailDto getProcessDetail(String processInstanceId, String taskDefinitionKey,
      String userId) {
    ProcessDetailDto dto = new ProcessDetailDto();
    //1.查询流程实例和流程信息实体
    HistoricProcessInstance historicProcessInstance = historyService
        .createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
    if (historicProcessInstance == null) {
      throw new BaseException("此流程实例不存在");
    }
    String processDefinitionId = historicProcessInstance.getProcessDefinitionId();
    String businessKey = historicProcessInstance.getBusinessKey();
    ProcessInfo processInfo = processInfoService
        .getProcessInfoByProcessDefinitionId(processDefinitionId);
    if (processInfo == null) {
      throw new BaseException("此流程不存在");
    }
    Long formId = processInfo.getFormId();
    if (businessKey == null) {
      throw new BaseException("此流程数据不存在");
    }
    //2.根据主键查询流程表单的数据
    List<FieldValueDetailDto> formDataDetail = fieldValueService
        .getFormDataDetail(formId, Long.valueOf(businessKey));
    Map<Long, List<FieldValueDetailDto>> groupByFieldId = formDataDetail.stream()
        .collect(Collectors.groupingBy(FieldValueDetailDto::getFieldId));
    List<FieldValueDetailDto> fieldValueDetailDtos = new ArrayList<>();
    if (StringUtils.isNotEmpty(taskDefinitionKey)) {
      //找到此流程开启流程节点对应的流程字段
      List<ProcessNodeField> processNodeFields = processNodeFieldService
          .getByInfoIdAndTaskKey(processInfo.getProcessInfoId(), taskDefinitionKey);
      for (ProcessNodeField processNodeField : processNodeFields) {
        if (processNodeField.getShowField() != null && processNodeField.getShowField()) {
          List<FieldValueDetailDto> fieldValues = groupByFieldId.get(processNodeField.getFieldId());
          if (fieldValues != null && fieldValues.size() > 0) {
            FieldValueDetailDto fieldValue = fieldValues.get(0);
            fieldValue.setShowField(processNodeField.getShowField());
            fieldValue.setEditField(processNodeField.getEditField());
            fieldValueDetailDtos.add(fieldValue);
          }
        }
      }
    } else {
      List<Field> fields = fieldService.getFieldInfoByFormId(formId);
      for (Field field : fields) {
        List<FieldValueDetailDto> fieldValues = groupByFieldId.get(field.getFieldId());
        if (fieldValues != null && fieldValues.size() > 0) {
          FieldValueDetailDto fieldValue = fieldValues.get(0);
          fieldValueDetailDtos.add(fieldValue);
        }
      }
    }
    dto.setFieldValueDetailDtos(fieldValueDetailDtos);
    //4.查询当前任务节点的属性(回退撤销按钮)
    if (StringUtils.isNotEmpty(taskDefinitionKey) && !"theStart".equals(taskDefinitionKey)) {
      ProcessNodeAttribute processNodeAttribute = processNodeAttributeService
          .getByProcessInfoIdAndTaskKey(processInfo.getProcessInfoId(), taskDefinitionKey);
      dto.setProcessNodeAttribute(processNodeAttribute);
    }
    //5.查询监控人按钮
    if (StringUtils.isNotEmpty(userId)) {
      List<ProcessGuarderAbility> processGuarderAbilities = processGuarderAbilityService
          .getByProcessInfoIdAndUserId(processInfo.getProcessInfoId(), userId);
      List<String> processFunctions = new ArrayList<>();
      for (ProcessGuarderAbility ability : processGuarderAbilities) {
        processFunctions.add(ability.getProcessFunction());
      }
      dto.setProcessFunctions(processFunctions);
    }
    return dto;
  }

  @Override
  public void claimTask(String taskId, String userId) {
    Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
    if (task == null) {
      throw new BaseException("此任务不存在");
    }
    if (task.getAssignee() != null && !task.getAssignee().equals(userId)) {
      throw new BaseException("此任务已被领取，领取人:" + task.getAssignee());
    }
    taskService.claim(taskId, userId);
  }

  @Override
  public void unclaimTask(String taskId, String userId) {
    Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
    if (task == null) {
      throw new BaseException("此任务不存在");
    }
    if (task.getAssignee() == null) {
      throw new BaseException("此任务还未被领取");
    }
    if (!task.getAssignee().equals(userId)) {
      throw new BaseException("您不是此任务负责人，不能进行此操作");
    }
    taskService.unclaim(taskId);
  }

  @Override
  @Transactional
  public void completeTask(String taskId, String userId,
      List<FieldValueAddParam> fieldValueAddParams) {
    // 1,根据任务ID查询任务实例
    Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
    if (task == null) {
      throw new BaseException("此任务不存在");
    }
    if (task.isSuspended()) {
      throw new BaseException("当前流程已挂起");
    }
    if (task.getAssignee() == null || !task.getAssignee().equals(userId)) {
      throw new BaseException("您没有权限提交任务");
    }
    String processDefinitionId = task.getProcessDefinitionId();
    String processInstanceId = task.getProcessInstanceId();
    String taskDefinitionKey = task.getTaskDefinitionKey();
    // 2.获取当前流程信息
    ProcessInfo processInfo = processInfoService
        .getProcessInfoByProcessDefinitionId(processDefinitionId);
    if (processInfo == null) {
      throw new BaseException("此流程不存在");
    }
    Long formId = processInfo.getFormId();
    HistoricProcessInstance historicProcessInstance = historyService
        .createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
    String businessKey = historicProcessInstance.getBusinessKey();
    if (businessKey == null) {
      throw new BaseException("此流程数据不存在");
    }
    //3.找到此流程开启流程节点对应的流程字段
    List<ProcessNodeField> processNodeFields = processNodeFieldService
        .getByInfoIdAndTaskKey(processInfo.getProcessInfoId(), taskDefinitionKey);
    //4.判断此流程开启流程节点的必填字段是否已填完整
    Map<Long, String> fieldValueAddMap = new HashMap<>();
    for (FieldValueAddParam param : fieldValueAddParams) {
      fieldValueAddMap.put(param.getFieldId(), param.getFieldValue());
    }
    if (processNodeFields.size() > 0) {
      for (ProcessNodeField processNodeField : processNodeFields) {
        Long fieldId = processNodeField.getFieldId();
        Field fieldInfoById = fieldService.getFieldInfoById(fieldId);
        //当此字段不为关联查询且可编辑时要判断是否必填
        if (!FieldTypeEnum.LINK_QUERY.name().equals(fieldInfoById.getFieldType())
            && processNodeField.getEditField() != null && processNodeField.getEditField()) {
          //判断必填
          if (fieldInfoById.getNecessary() && fieldValueAddMap.get(fieldId) == null) {
            throw new BaseException(fieldInfoById.getFieldName() + "字段必填");
          }
        }
      }
    }
    //5.更新流程表单的数据
    fieldValueService.updateFormData(formId, fieldValueAddParams, Long.valueOf(businessKey));
    Map<String, Object> variables = new HashMap<>();
    for (FieldValueAddParam param : fieldValueAddParams) {
      variables.put("field" + param.getFieldId(), param.getFieldValue());
    }
    //完成任务
    try {
      taskService.complete(taskId, variables);
    } catch (ActivitiException e) {
      throw new BaseException("无法流转到下个流程节点");
    }
  }

  @Override
  public void suspendProcess(String processInstanceId) {
    ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
        .processInstanceId(processInstanceId).singleResult();
    if (processInstance == null) {
      throw new BaseException("流程已完成，不能挂起");
    }
    runtimeService.suspendProcessInstanceById(processInstanceId);
  }

  @Override
  public void activeProcess(String processInstanceId) {
    ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
        .processInstanceId(processInstanceId).singleResult();
    if (processInstance == null) {
      throw new BaseException("流程已完成或不存在");
    }
    if (!processInstance.isSuspended()) {
      throw new BaseException("只能激活挂起中的流程");
    }
    runtimeService.activateProcessInstanceById(processInstanceId);
  }

  @Override
  @Transactional
  public void endProcessInstance(String processInstanceId, String processFunction) {
    Execution execution = runtimeService.createExecutionQuery().executionId(processInstanceId)
        .singleResult();
    ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
        .processInstanceId(processInstanceId).singleResult();
    if (processInstance == null) {
      throw new BaseException("流程已完成或不存在");
    }
    Map<String, Object> variables = runtimeService.getVariables(processInstanceId);
    //设置当前流程为失败或者成功
    if (ProcessFunctionEnum.PROCESS_END_ERROR.name().equals(processFunction)) {
      runtimeService.setVariable(execution.getId(), FlowConstant.PROCESS_FAIL, true);
    }
    if (ProcessFunctionEnum.PROCESS_END_SUCCESS.name().equals(processFunction)) {
      runtimeService.setVariable(execution.getId(), FlowConstant.PROCESS_FAIL, false);
    }

    if (execution.getSuperExecutionId() != null) {
      if (ProcessFunctionEnum.PROCESS_END_ERROR.name().equals(processFunction)) {
        runtimeService.setVariable(execution.getSuperExecutionId(), FlowConstant.SUB_PROCESS_FAIL,
            true);
      }
      if (ProcessFunctionEnum.PROCESS_END_SUCCESS.name().equals(processFunction)) {
        runtimeService.setVariable(execution.getSuperExecutionId(), FlowConstant.SUB_PROCESS_FAIL,
            false);
      }
    }
    CommandExecutor commandExecutor = ((TaskServiceImpl) taskService).getCommandExecutor();
    commandExecutor.execute(new MyEndProcessCmd(processInstanceId, "手动结束流程", processFunction));

    //查询按钮操作是否触发流程
    ProcessInfo processInfo = processInfoService
        .getProcessInfoByProcessDefinitionId(processInstance.getProcessDefinitionId());
    List<ProcessTrigger> processTriggers = processTriggerService
        .getByProcessInfoIdAndFunction(processInfo.getProcessInfoId(), processFunction);
    if (processTriggers.size() > 0) {
      for (ProcessTrigger processTrigger : processTriggers) {
        this.startTriggerProcess(variables, processTrigger);
      }
    }
  }

  @Override
  public void rollBackTask(String taskId, String userId) {
    // 根据任务ID查询任务实例
    Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
    if (task == null) {
      throw new BaseException("此任务不存在");
    }
    if (task.isSuspended()) {
      throw new BaseException("当前流程已挂起");
    }
    if (task.getAssignee() == null || !task.getAssignee().equals(userId)) {
      throw new BaseException("您没有权限操作此任务");
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
    //恢复连线
    flowNode.setOutgoingFlows(outgoingFlows);
  }

  @Override
  public void revokeTask(String taskId, String userId) {
    //1.查询当前历史任务
    HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery()
        .finished()
        .taskId(taskId).singleResult();
    if (historicTaskInstance == null) {
      throw new BaseException("当前任务未完成，不能撤销");
    }
    //2.判断当前流程实例是否还在进行中
    ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
        .processInstanceId(historicTaskInstance.getProcessInstanceId()).singleResult();
    if (processInstance == null) {
      throw new BaseException("当前流程已结束，不能撤销");
    }
    if (processInstance.isSuspended()) {
      throw new BaseException("当前流程已挂起，不能撤销");
    }

    //查询该节点的下个任务节点
    BpmnModel bpmnModel = repositoryService
        .getBpmnModel(historicTaskInstance.getProcessDefinitionId());
    FlowNode flowNode = (FlowNode) bpmnModel
        .getFlowElement(historicTaskInstance.getTaskDefinitionKey());
    List<SequenceFlow> outgoingFlows = flowNode.getOutgoingFlows();
    List<HistoricTaskInstance> nextTasks = new ArrayList<>();
    for (SequenceFlow out : outgoingFlows) {
      List<HistoricTaskInstance> finishedTask = historyService.createHistoricTaskInstanceQuery()
          .finished()
          .processInstanceId(historicTaskInstance.getProcessInstanceId())
          .executionId(historicTaskInstance.getExecutionId()).taskDeleteReason(null)
          .taskDefinitionKey(out.getTargetRef()).list();
      if (finishedTask.size() > 0) {
        throw new BaseException("下级节点任务已完成，不能撤销");
      }
      List<HistoricTaskInstance> allTask = historyService.createHistoricTaskInstanceQuery()
          .processInstanceId(historicTaskInstance.getProcessInstanceId())
          .executionId(historicTaskInstance.getExecutionId()).taskDeleteReason(null)
          .taskDefinitionKey(out.getTargetRef()).list();
      nextTasks.addAll(allTask);
    }
    CommandExecutor commandExecutor = ((TaskServiceImpl) taskService).getCommandExecutor();
    for (HistoricTaskInstance taskInstance : nextTasks) {
      TaskEntity task = (TaskEntity) taskService.createTaskQuery().taskId(taskInstance.getId())
          .singleResult();
      ExecutionEntity execution = (ExecutionEntity) runtimeService.createExecutionQuery()
          .executionId(task.getExecutionId())
          .singleResult();
      List<HistoricActivityInstance> hisActList = historyService
          .createHistoricActivityInstanceQuery().processInstanceId(task.getProcessInstanceId())
          .executionId(task.getExecutionId()).activityId(task.getTaskDefinitionKey()).list();
      HistoricActivityInstanceEntity activityInstanceEntity = null;
      for (HistoricActivityInstance hisAct : hisActList) {
        if (hisAct.getTaskId().equals(task.getId())) {
          activityInstanceEntity = (HistoricActivityInstanceEntity) hisAct;
        }

      }
      //删除下级的执行中任务
      commandExecutor
          .execute(new MyDeleteTaskCmd(task, execution, activityInstanceEntity, "上级节点撤销删除", true));
//      commandExecutor.execute(new MyDeleteActCmd(activityInstanceEntity));
    }

    //将当前任务的历史任务和活动的deleteReason写入已撤销
    HistoricTaskInstanceEntity historicTaskInstanceEntity = (HistoricTaskInstanceEntity) historicTaskInstance;
    historicTaskInstanceEntity.setDeleteReason("已撤销");

    List<HistoricActivityInstance> hisActList = historyService
        .createHistoricActivityInstanceQuery()
        .processInstanceId(historicTaskInstance.getProcessInstanceId())
        .executionId(historicTaskInstance.getExecutionId())
        .activityId(historicTaskInstance.getTaskDefinitionKey()).list();
    HistoricActivityInstanceEntity activityInstanceEntity = null;
    for (HistoricActivityInstance hisAct : hisActList) {
      if (hisAct.getTaskId().equals(historicTaskInstance.getId())) {
        activityInstanceEntity = (HistoricActivityInstanceEntity) hisAct;
        activityInstanceEntity.setDeleteReason("已撤销");
      }

    }
    commandExecutor
        .execute(new MyUpdateHistoricTaskCmd(historicTaskInstanceEntity, activityInstanceEntity));

    //创建当前节点新的任务

    ExecutionEntityImpl executionEntity = (ExecutionEntityImpl) runtimeService
        .createExecutionQuery()
        .executionId(historicTaskInstance.getProcessInstanceId()).singleResult();
    ExecutionEntityImpl curTaskExecution = new ExecutionEntityImpl();
    curTaskExecution.setId(historicTaskInstance.getExecutionId());
    curTaskExecution.setRevision(executionEntity.getRevision());
    curTaskExecution.setParentId(executionEntity.getId());
    curTaskExecution.setProcessInstanceId(executionEntity.getProcessInstanceId());
    curTaskExecution.setProcessDefinitionId(executionEntity.getProcessDefinitionId());
    curTaskExecution.setRootProcessInstanceId(executionEntity.getRootProcessInstanceId());
    curTaskExecution.setActive(true);
    curTaskExecution.setConcurrent(false);
    curTaskExecution.setScope(false);
    curTaskExecution.setEventScope(false);
    curTaskExecution.setMultiInstanceRoot(false);
    curTaskExecution.setSuspensionState(1);
    curTaskExecution.setCountEnabled(false);
    curTaskExecution.setStartTime(new Date());

    TaskEntityImpl taskEntity = new TaskEntityImpl();
    taskEntity.setRevision(curTaskExecution.getRevision());
    taskEntity.setExecutionId(curTaskExecution.getId());
    taskEntity.setProcessInstanceId(curTaskExecution.getProcessInstanceId());
    taskEntity.setProcessDefinitionId(curTaskExecution.getProcessDefinitionId());
    taskEntity.setName(historicTaskInstance.getName());
    taskEntity.setTaskDefinitionKey(historicTaskInstance.getTaskDefinitionKey());
    taskEntity.setAssignee(historicTaskInstance.getAssignee());
    taskEntity.setPriority(historicTaskInstance.getPriority());
    taskEntity.setCreateTime(new Date());
    taskEntity.setSuspensionState(1);

    commandExecutor.execute(new MyCreateExecutionCmd(curTaskExecution));
    commandExecutor.execute(new MyCreateTaskCmd(taskEntity));
  }

  @Override
  public FlowChart getFlowChartByProcessId(String processInstanceId) throws IOException {
    FlowChart flowChart = new FlowChart();
    //1.查询流程实例
    HistoricProcessInstance historicProcessInstance = historyService
        .createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
    if (historicProcessInstance != null) {
      //2.根据流程id查询processParam
      ProcessInfo processInfo = processInfoService
          .getProcessInfoByProcessDefinitionId(historicProcessInstance.getProcessDefinitionId());
      if (processInfo != null && processInfo.getProcessParam() != null) {
        ObjectMapper objectMapper = new ObjectMapper();
        ProcessParam processParam = objectMapper
            .readValue(processInfo.getProcessParam(), ProcessParam.class);
        flowChart.setProcessParam(processParam);
      }

      //3.查询进行中的任务节点
      List<HistoricActivityInstance> runtimeActivities = historyService
          .createHistoricActivityInstanceQuery()
          .unfinished().processInstanceId(processInstanceId).list();
      Set<String> runtimeTaskKeySet = new HashSet<>();
      for (HistoricActivityInstance runtimeActivity : runtimeActivities) {
        runtimeTaskKeySet.add(runtimeActivity.getActivityId());
      }

      //4.查询当前流程实例已完成的节点
      List<HistoricActivityInstance> historicActivities = historyService
          .createHistoricActivityInstanceQuery()
          .finished().processInstanceId(processInstanceId).list();
      Set<String> historicTaskKeySet = new HashSet<>();
      for (HistoricActivityInstance historicActivity : historicActivities) {
        if (historicActivity.getDeleteReason() == null) {
          if (historicActivity.getActivityType().equals("parallelGateway")) {
            //当节点为并行网关时，判断是否在等待
            Execution execution = runtimeService.createExecutionQuery()
                .executionId(historicActivity.getExecutionId()).singleResult();
            if (execution == null) {
              historicTaskKeySet.add(historicActivity.getActivityId());
            } else {
              runtimeTaskKeySet.add(historicActivity.getActivityId());
            }
          } else {
            historicTaskKeySet.add(historicActivity.getActivityId());
          }
        }
      }

      flowChart.setHistoricTaskKeyList(new ArrayList<>(historicTaskKeySet));
      flowChart.setRuntimeTaskKeyList(new ArrayList<>(runtimeTaskKeySet));
    }
    return flowChart;
  }

  @Override
  public List<ProcessLogDto> getProcessLog(String processInstanceId) {
    List<ProcessLogDto> dtos = new ArrayList<>();
    //获取当前流程所有的活动
    List<HistoricActivityInstance> activityInstances = historyService
        .createHistoricActivityInstanceQuery()
        .finished()
        .processInstanceId(processInstanceId)
        .orderByHistoricActivityInstanceEndTime().desc()
        .list();
    for (HistoricActivityInstance act : activityInstances) {
      if ("manualTask".equals(act.getActivityType()) && "theStart".equals(act.getActivityId())) {
        ProcessLogDto dto = new ProcessLogDto();
        HistoricProcessInstance historicProcessInstance = historyService
            .createHistoricProcessInstanceQuery().processInstanceId(processInstanceId)
            .singleResult();
        dto.setActName(act.getActivityName());
        dto.setUserId(historicProcessInstance.getStartUserId());
        dto.setStartTime(act.getStartTime());
        dto.setEndTime(act.getEndTime());
        dtos.add(dto);
      }
      if ("userTask".equals(act.getActivityType())) {
        ProcessLogDto dto = new ProcessLogDto();
        dto.setActName(act.getActivityName());
        dto.setUserId(act.getAssignee());
        dto.setStartTime(act.getStartTime());
        dto.setEndTime(act.getEndTime());
        dto.setDuringTime(act.getDurationInMillis() / 1000);
        dtos.add(dto);
      }
    }
    return dtos;
  }
}
