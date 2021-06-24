package com.yusei.web;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/activitiBase")
@Deprecated
public class ActivitiBaseController {

//
//  @Autowired
//  private IActivitiService activitiService;
//
//  @Autowired
//  private RepositoryService repositoryService;
//
//  @Autowired
//  private RuntimeService runtimeService;
//
//  @Autowired
//  private HistoryService historyService;
//
//  @Autowired
//  private TaskService taskService;
//
//  @Autowired
//  private IdentityService identityService;
//
//  @Autowired
//  private IProcessDefinitionFormService processDefinitionFormService;
//
//  @Autowired
//  private IProcessNodeFieldService processNodeFieldService;
//
//  @Autowired
//  private IFieldService fieldService;
//
//  @Autowired
//  private IFieldValueService fieldValueService;
//
//  @Autowired
//  private ILinkFilterService linkFilterService;
//
//  @Autowired
//  private IProcessInfoService processInfoService;
//
//  @Autowired
//  private IProcessNodeAttributeService processNodeAttributeService;
//
//  @Autowired
//  private IProcessGuarderService processGuarderService;
//
//  @Autowired
//  private IProcessTriggerService processTriggerService;
//
//  /**
//   * 发布
//   */
//  @Deprecated
//  @PostMapping("/deploy")
//  public ReturnResult deployProcess(String url) {
//
//    Deployment deployment = repositoryService.createDeployment()
//        .name("发布测试")
//        .addClasspathResource(url)
//        .deploy();
//    return ResponseUtils.ok(deployment.getId());
//  }
//
//  /**
//   * @param processDefinitionKey 流程定义key值
//   * @param sponsorId 发起人id
//   * @param fieldValueAddParams 发起流程需填写的字段值
//   */
//  @Deprecated
//  @Transactional
//  @PostMapping("/startProcess")
//  public ReturnResult startProcess(String processDefinitionKey, String sponsorId,
//      @RequestBody List<FieldValueAddParam> fieldValueAddParams) {
//    //1.找到此流程定义对应的表单
//    ProcessDefinitionForm processDefinitionForm = processDefinitionFormService
//        .getByProcessDefinitionKey(processDefinitionKey);
//    Long primaryKeyValue = null;
//    if (processDefinitionForm != null) {
//      Long formId = processDefinitionForm.getFormId();
//      //2.找到此流程开启流程节点对应的流程字段
//      List<ProcessNodeField> processNodeFields = processNodeFieldService
//          .getByProcessKey(processDefinitionKey, "theStart");
//      //3.判断此流程开启流程节点的必填字段是否已填完整，是否违反唯一性判断
//      Map<Long, String> fieldValueAddMap = new HashMap<>();
//      for (FieldValueAddParam param : fieldValueAddParams) {
//        fieldValueAddMap.put(param.getFieldId(), param.getFieldValue());
//      }
//      if (processNodeFields.size() > 0) {
//        for (ProcessNodeField processNodeField : processNodeFields) {
//          Long fieldId = processNodeField.getFieldId();
//          Field fieldInfoById = fieldService.getFieldInfoById(fieldId);
//          //当此字段不为关联查询且可编辑时要判断是否必填
//          if (!FieldTypeEnum.LINK_QUERY.name().equals(fieldInfoById.getFieldType())
//              && processNodeField.getEditField() != null && processNodeField.getEditField()) {
//            //判断必填
//            if (fieldInfoById.getNecessary() && fieldValueAddMap.get(fieldId) == null) {
//              throw new BaseException(fieldInfoById.getFieldName() + "字段必填");
//            }
//            //判断重复
//          }
//        }
//      }
//      primaryKeyValue = fieldValueService.addFormData(formId, fieldValueAddParams);
//      if (primaryKeyValue == null) {
//        throw new BaseException("创建流程记录失败");
//      }
//    }
//    identityService.setAuthenticatedUserId(sponsorId);
//    Map<String, Object> variables = new HashMap<>();
//    variables.put(FlowConstant.PROCESS_FAIL, false);
//    for (FieldValueAddParam param : fieldValueAddParams) {
//      variables.put("field" + param.getFieldId(), param.getFieldValue());
//    }
//    ProcessInstance processInstance;
//    try {
//      if (primaryKeyValue != null) {
//        processInstance = runtimeService
//            .startProcessInstanceByKey(processDefinitionKey, primaryKeyValue.toString(), variables);
//      } else {
//        processInstance = runtimeService
//            .startProcessInstanceByKey(processDefinitionKey, variables);
//      }
//    } catch (ActivitiException e) {
//      throw new BaseException("无法流转到下个流程节点");
//    }
//    return ResponseUtils.ok(processInstance.getProcessDefinitionId());
//  }
//
//  /**
//   * 分页查询我发起的流程.
//   */
//  @Deprecated
//  @GetMapping("/getMyStartedProcess")
//  public ReturnResult getMyStartedProcess(String userId, PageParam pageParam) {
//    List<HistoricProcessInstance> historicProcessInstanceList = historyService
//        .createHistoricProcessInstanceQuery().startedBy(userId).orderByProcessInstanceStartTime()
//        .desc()
//        .listPage(pageParam.getOffset(), pageParam.getEndIndex());
//    List<ProcessInstanceDto> processInstanceDtos = new ArrayList<>();
//    for (HistoricProcessInstance historicProcessInstance : historicProcessInstanceList) {
//      ProcessInstanceDto processInstanceDto = new ProcessInstanceDto();
//      processInstanceDto.setProcessInstanceId(historicProcessInstance.getId());
//      processInstanceDto
//          .setProcessName(historicProcessInstance.getProcessDefinitionName());
//      processInstanceDto.setStartTime(historicProcessInstance.getStartTime());
//      processInstanceDto.setEndTime(historicProcessInstance.getEndTime());
//      ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
//          .processInstanceId(historicProcessInstance.getId()).singleResult();
//      if (processInstance == null) {
//        processInstanceDto.setProcessStatus(ProcessStatusEnum.OVER.getProcessStatus());
//      } else {
//        //流程进行中，要查出当前流程进行中的节点名称
//        processInstanceDto.setProcessStatus(ProcessStatusEnum.PENDING.getProcessStatus());
//        List<HistoricActivityInstance> historicActivities = historyService
//            .createHistoricActivityInstanceQuery()
//            .unfinished().processInstanceId(historicProcessInstance.getId()).list();
//        List<ActTaskDto> taskDtoList = new ArrayList<>();
//        for (HistoricActivityInstance historicActivity : historicActivities) {
//          ActTaskDto taskDto = new ActTaskDto();
//          taskDto.setId(historicActivity.getTaskId());
//          taskDto.setName(historicActivity.getActivityName());
//          taskDto.setTaskDefinitionKey(historicActivity.getActivityId());
//          taskDtoList.add(taskDto);
//        }
//        processInstanceDto.setTaskDtoList(taskDtoList);
//      }
//      processInstanceDtos.add(processInstanceDto);
//    }
//    return ResponseUtils.ok(processInstanceDtos);
//  }
//
//  @Deprecated
//  private List<ActTaskDto> getPendingProcessNodeByInstanceId(String processInstanceId,
//      String userId) {
//    List<Task> taskList;
//    if (userId != null) {
//      taskList = taskService.createTaskQuery().processInstanceId(processInstanceId)
//          .taskAssignee(userId).list();
//    } else {
//      taskList = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
//    }
//    List<ActTaskDto> taskDtos = new ArrayList<>();
//    for (Task task : taskList) {
//      ActTaskDto taskDto = new ActTaskDto();
//      taskDto.setId(task.getId());
//      taskDto.setName(task.getName());
//      taskDto.setTaskDefinitionKey(task.getTaskDefinitionKey());
//      taskDtos.add(taskDto);
//    }
//    return taskDtos;
//  }
//
//  /**
//   * 分页查询我的待办流程.
//   */
//  @Deprecated
//  @GetMapping("/getPreHandleProcess")
//  public ReturnResult getPreHandleProcess(String userId, PageParam pageParam) {
//    //1.获取我的待办任务
//    List<Task> tasks = taskService.createTaskQuery().taskCandidateOrAssigned(userId).list();
//    List<ProcessInstanceDto> processInstanceDtos = new ArrayList<>();
//    //2.获取流程实例id的set
//    Set<String> processInstanceIdSet = new HashSet<>();
//    for (Task task : tasks) {
//      processInstanceIdSet.add(task.getProcessInstanceId());
//    }
//    if (processInstanceIdSet.size() > 0) {
//      List<HistoricProcessInstance> historicProcessInstanceList = historyService
//          .createHistoricProcessInstanceQuery().unfinished()
//          .processInstanceIds(processInstanceIdSet)
//          .orderByProcessInstanceStartTime().desc()
//          .listPage(pageParam.getOffset(), pageParam.getEndIndex());
//      for (HistoricProcessInstance historicProcessInstance : historicProcessInstanceList) {
//        ProcessInstanceDto processInstanceDto = new ProcessInstanceDto();
//        processInstanceDto.setProcessInstanceId(historicProcessInstance.getId());
//        processInstanceDto
//            .setProcessName(historicProcessInstance.getProcessDefinitionName());
//        processInstanceDto.setStartTime(historicProcessInstance.getStartTime());
//        processInstanceDto.setEndTime(historicProcessInstance.getEndTime());
//        //流程进行中，要查出当前流程进行中的节点名称
//        processInstanceDto.setProcessStatus(ProcessStatusEnum.PENDING.getProcessStatus());
//        List<ActTaskDto> taskDtoList = getPendingProcessNodeByInstanceId(
//            historicProcessInstance.getId(), userId);
//        processInstanceDto.setTaskDtoList(taskDtoList);
//        processInstanceDtos.add(processInstanceDto);
//      }
//    }
//    return ResponseUtils.ok(processInstanceDtos);
//  }
//
//  @Deprecated
//  @PostMapping("/claimTask")
//  public ReturnResult claimTask(String userId, String taskId) {
//    taskService.claim(taskId, userId);
//    return ResponseUtils.ok();
//  }
//
//  @Deprecated
//  @GetMapping("/getHandleProcess")
//  public ReturnResult getHandleProcess(String userId, PageParam pageParam) {
//    List<HistoricTaskInstance> historicTasks = historyService.createHistoricTaskInstanceQuery()
//        .finished()
//        .taskAssignee(userId).list();
//    List<ProcessInstanceDto> processInstanceDtos = new ArrayList<>();
//    //获取流程实例id的set
//    Set<String> processInstanceIdSet = new HashSet<>();
//    for (HistoricTaskInstance HistoricTask : historicTasks) {
//      processInstanceIdSet.add(HistoricTask.getProcessInstanceId());
//    }
//    if (processInstanceIdSet.size() > 0) {
//      List<HistoricProcessInstance> historicProcessInstanceList = historyService
//          .createHistoricProcessInstanceQuery()
//          .processInstanceIds(processInstanceIdSet)
//          .orderByProcessInstanceStartTime().desc()
//          .listPage(pageParam.getOffset(), pageParam.getEndIndex());
//      for (HistoricProcessInstance historicProcessInstance : historicProcessInstanceList) {
//        ProcessInstanceDto processInstanceDto = new ProcessInstanceDto();
//        processInstanceDto.setProcessInstanceId(historicProcessInstance.getId());
//        processInstanceDto
//            .setProcessName(historicProcessInstance.getProcessDefinitionName());
//        processInstanceDto.setStartTime(historicProcessInstance.getStartTime());
//        processInstanceDto.setEndTime(historicProcessInstance.getEndTime());
//        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
//            .processInstanceId(historicProcessInstance.getId()).singleResult();
//        if (processInstance == null) {
//          processInstanceDto.setProcessStatus(ProcessStatusEnum.OVER.getProcessStatus());
//        } else {
//          //流程进行中，要查出当前流程进行中的节点名称
//          processInstanceDto.setProcessStatus(ProcessStatusEnum.PENDING.getProcessStatus());
//        }
//        List<ActTaskDto> taskDtoList = new ArrayList<>();
//        List<HistoricTaskInstance> myHandledTasks = historyService
//            .createHistoricTaskInstanceQuery().finished()
//            .processInstanceId(historicProcessInstance.getId())
//            .taskAssignee(userId).list();
//        for (HistoricTaskInstance myHandledTask : myHandledTasks) {
//          ActTaskDto taskDto = new ActTaskDto();
//          taskDto.setId(myHandledTask.getId());
//          taskDto.setName(myHandledTask.getName());
//          taskDto.setTaskDefinitionKey(myHandledTask.getTaskDefinitionKey());
//          taskDtoList.add(taskDto);
//        }
//        processInstanceDto.setTaskDtoList(taskDtoList);
//        processInstanceDtos.add(processInstanceDto);
//      }
//    }
//    return ResponseUtils.ok(processInstanceDtos);
//  }
//
//  @Deprecated
//  @GetMapping("/getProcessDetail")
//  public ReturnResult getProcessDetail(@RequestParam String processInstanceId,
//      @RequestParam String taskDefinitionKey) {
//    //1.查询流程实例，获取流程定义key值和businessKey值
//    HistoricProcessInstance historicProcessInstance = historyService
//        .createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
//    String processDefinitionKey = historicProcessInstance.getProcessDefinitionKey();
//    String businessKey = historicProcessInstance.getBusinessKey();
//    //2.找到此流程开启流程节点对应的流程字段
//    ProcessDefinitionForm processDefinitionForm = processDefinitionFormService
//        .getByProcessDefinitionKey(processDefinitionKey);
//    Long formId = processDefinitionForm.getFormId();
//    List<ProcessNodeField> processNodeFields = processNodeFieldService
//        .getByProcessKey(processDefinitionKey, taskDefinitionKey);
//    //3.根据表单id和表单记录的主键值，查询此表单这条记录的所有字段数据
//    if (businessKey == null) {
//      //businessKey值为空，代表刚刚进入子流程，子流程数据记录未创建，新建子流程数据
//      String rootProcessInstanceId = historicProcessInstance.getSuperProcessInstanceId();
//      //获取主流程的表单数据
//      ProcessInstance rootProcessInstance = runtimeService.createProcessInstanceQuery()
//          .processInstanceId(rootProcessInstanceId).singleResult();
//      ProcessDefinitionForm rootProcessDefinitionForm = processDefinitionFormService
//          .getByProcessDefinitionKey(rootProcessInstance.getProcessDefinitionKey());
//      Long rootFormId = rootProcessDefinitionForm.getFormId();
//      //查询出关联流程的数据
//      Map<Long, String> fieldValueMap = fieldValueService
//          .getByFormIdAndPrimaryKeyValue(rootFormId,
//              Long.valueOf(rootProcessInstance.getBusinessKey()));
//
//      //创建子流程流程实例对应的流程表单记录
//      List<FieldValueAddParam> fieldValueAddParams = new ArrayList<>();
//      List<ProcessNodeField> startNodeProcessNodeFields = processNodeFieldService
//          .getByProcessKey(processDefinitionKey, "theStart");
//      //查询出发起流程所需字段值，映射到主流程的字段
//      for (ProcessNodeField startNodeField : startNodeProcessNodeFields) {
//        Long fieldId = startNodeField.getFieldId();
//        Field fieldInfoById = fieldService.getFieldInfoById(fieldId);
//        FieldValueAddParam fieldValueAddParam = new FieldValueAddParam();
//        fieldValueAddParam.setFieldId(startNodeField.getFieldId());
//        //查询此字段的关联值
//        if (FieldTypeEnum.SELECT.name().equals(fieldInfoById.getFieldType())) {
//          if (DefaultValueTypeEnum.LINK_FORM.name().equals(fieldInfoById.getDefaultValueType())) {
//            //下拉框的关联表单
//            Long linkFormId = fieldInfoById.getLinkFormId();
//            Long linkFieldId = fieldInfoById.getLinkFieldId();
//            if (rootFormId.equals(linkFormId)) {
//              //如果关联的表单是主流程表单，从主流程表单里面直接获取数据
//              String fieldValue = fieldValueMap.get(linkFieldId);
//              fieldValueAddParam.setFieldValue(fieldValue);
//              fieldValueAddParams.add(fieldValueAddParam);
//            }
//          }
//        }
//      }
//      Long primaryKeyValue = fieldValueService.addFormData(formId, fieldValueAddParams);
//      businessKey = primaryKeyValue.toString();
//      //已启动流程修改businessKey
//      runtimeService.updateBusinessKey(processInstanceId, businessKey);
//    }
//
//    ProcessNodeAllAttributeDto processNodeAllAttributeDto = new ProcessNodeAllAttributeDto();
//    List<FieldValueDetailDto> formDataDetail = fieldValueService
//        .getFormDataDetail(formId, Long.valueOf(businessKey));
//    Map<Long, List<FieldValueDetailDto>> groupByFieldId = formDataDetail.stream()
//        .collect(Collectors.groupingBy(FieldValueDetailDto::getFieldId));
//    List<FieldValueDetailDto> fieldValueDetailDtos = new ArrayList<>();
//    for (ProcessNodeField processNodeField : processNodeFields) {
//      if (processNodeField.getShowField() != null && processNodeField.getShowField()) {
//        List<FieldValueDetailDto> dtos = groupByFieldId.get(processNodeField.getFieldId());
//        if (dtos != null && dtos.size() > 0) {
//          FieldValueDetailDto dto = dtos.get(0);
//          dto.setShowField(processNodeField.getShowField());
//          dto.setEditField(processNodeField.getEditField());
//          fieldValueDetailDtos.add(dto);
//        }
//      }
//    }
//    processNodeAllAttributeDto.setFieldValueDetailDtos(fieldValueDetailDtos);
////    List<ProcessNodeAttribute> processNodeAttributes = processNodeAttributeService
////        .getByProcessInfoIdAndTaskKey(null, taskDefinitionKey);
////    if (processNodeAttributes.size() > 0) {
////      ProcessNodeAttribute processNodeAttribute = processNodeAttributes.get(0);
////      processNodeAllAttributeDto.setTaskDefinitionKey(processNodeAttribute.getTaskDefinitionKey());
////      processNodeAllAttributeDto.setRollBack(processNodeAttribute.getRollBack());
////      processNodeAllAttributeDto.setRevoke(processNodeAttribute.getRevoke());
////    }
//    return ResponseUtils.ok(processNodeAllAttributeDto);
//  }
//
//  @Deprecated
//  @GetMapping("/getMyMonitoredProcess")
//  public ReturnResult getMyMonitoredProcess(String userId, PageParam pageParam) {
//    List<ProcessInstanceDto> processInstanceDtos = new ArrayList<>();
//    List<ProcessGuarder> processGuarders = processGuarderService.getByUserId(userId);
//    if (processGuarders.size() > 0) {
//      List<String> processDefinitionKeys = new ArrayList<>();
//      for (ProcessGuarder processGuarder : processGuarders) {
//        processDefinitionKeys.add(processGuarder.getProcessDefinitionKey());
//      }
//
//      List<HistoricProcessInstance> historicProcessInstances = historyService
//          .createHistoricProcessInstanceQuery().processDefinitionKeyIn(processDefinitionKeys)
//          .orderByProcessInstanceStartTime()
//          .desc()
//          .listPage(pageParam.getOffset(), pageParam.getEndIndex());
//      for (HistoricProcessInstance historicProcessInstance : historicProcessInstances) {
//        ProcessInstanceDto processInstanceDto = new ProcessInstanceDto();
//        processInstanceDto.setProcessInstanceId(historicProcessInstance.getId());
//        processInstanceDto
//            .setProcessName(historicProcessInstance.getProcessDefinitionName());
//        processInstanceDto.setStartTime(historicProcessInstance.getStartTime());
//        processInstanceDto.setEndTime(historicProcessInstance.getEndTime());
//        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
//            .processInstanceId(historicProcessInstance.getId()).singleResult();
//        if (processInstance == null) {
//          processInstanceDto.setProcessStatus(ProcessStatusEnum.OVER.getProcessStatus());
//        } else {
//          processInstanceDto.setProcessStatus(ProcessStatusEnum.PENDING.getProcessStatus());
//          List<HistoricActivityInstance> historicActivities = historyService
//              .createHistoricActivityInstanceQuery()
//              .unfinished().processInstanceId(historicProcessInstance.getId()).list();
//          List<ActTaskDto> taskDtoList = new ArrayList<>();
//          for (HistoricActivityInstance historicActivity : historicActivities) {
//            ActTaskDto taskDto = new ActTaskDto();
//            taskDto.setId(historicActivity.getTaskId());
//            taskDto.setName(historicActivity.getActivityName());
//            taskDto.setTaskDefinitionKey(historicActivity.getActivityId());
//            taskDtoList.add(taskDto);
//          }
//          processInstanceDto.setTaskDtoList(taskDtoList);
//        }
//        processInstanceDtos.add(processInstanceDto);
//      }
//    }
//    return ResponseUtils.ok(processInstanceDtos);
//  }
//
//  @Deprecated
//  @PostMapping("/completeTask")
//  public ReturnResult completeTask(String taskId, String userId,
//      @RequestBody List<FieldValueAddParam> fieldValueAddParams) {
//    // 1,根据任务ID查询任务实例
//    Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
//    if (task == null) {
//      return ResponseUtils.error("任务不存在");
//    }
//    // 2.获取当前任务的key值和当前流程定义的key值
//    ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().
//        processDefinitionId(task.getProcessDefinitionId()).singleResult();
//    String processDefinitionKey = processDefinition.getKey();
//    String taskDefinitionKey = task.getTaskDefinitionKey();
//    //3.找到此流程开启流程节点对应的流程字段
//    ProcessDefinitionForm processDefinitionForm = processDefinitionFormService
//        .getByProcessDefinitionKey(processDefinitionKey);
//    if (processDefinitionForm != null) {
//      Long formId = processDefinitionForm.getFormId();
//      List<ProcessNodeField> processNodeFields = processNodeFieldService
//          .getByProcessKey(processDefinitionKey, taskDefinitionKey);
//      //4.判断此流程开启流程节点的必填字段是否已填完整，是否违反唯一性判断
//      Map<Long, String> fieldValueAddMap = new HashMap<>();
//      for (FieldValueAddParam param : fieldValueAddParams) {
//        fieldValueAddMap.put(param.getFieldId(), param.getFieldValue());
//      }
//      if (processNodeFields.size() > 0) {
//        for (ProcessNodeField processNodeField : processNodeFields) {
//          Long fieldId = processNodeField.getFieldId();
//          Field fieldInfoById = fieldService.getFieldInfoById(fieldId);
//          //当此字段不为关联查询且可编辑时要判断是否必填
//          if (!FieldTypeEnum.LINK_QUERY.name().equals(fieldInfoById.getFieldType())
//              && processNodeField.getEditField() != null && processNodeField.getEditField()) {
//            //判断必填
//            if (fieldInfoById.getNecessary() && fieldValueAddMap.get(fieldId) == null) {
//              throw new BaseException(fieldInfoById.getFieldName() + "字段必填");
//            }
//            //判断重复
//          }
//        }
//      }
//      //5.判断当前任务是否在子流程下，并且流程实例的businessKey是否为空，
//      // 如果是表明是子流程，并且流程表单记录未创建
//      ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
//          .processInstanceId(task.getProcessInstanceId()).singleResult();
//      String businessKey = processInstance.getBusinessKey();
//      String rootProcessInstanceId = processInstance.getRootProcessInstanceId();
//      if (businessKey == null && !rootProcessInstanceId.equals(task.getProcessInstanceId())) {
//        //获取主流程的表单数据
//        ProcessInstance rootProcessInstance = runtimeService.createProcessInstanceQuery()
//            .processInstanceId(rootProcessInstanceId).singleResult();
//        ProcessDefinitionForm rootProcessDefinitionForm = processDefinitionFormService
//            .getByProcessDefinitionKey(rootProcessInstance.getProcessDefinitionKey());
//        Long rootFormId = rootProcessDefinitionForm.getFormId();
//        //查询出关联流程的数据
//        Map<Long, String> fieldValueMap = fieldValueService
//            .getByFormIdAndPrimaryKeyValue(rootFormId,
//                Long.valueOf(rootProcessInstance.getBusinessKey()));
//
//        //创建子流程流程实例对应的流程表单记录
//        List<ProcessNodeField> startNodeProcessNodeFields = processNodeFieldService
//            .getByProcessKey(processDefinitionKey, "theStart");
//        //查询出发起流程所需字段值，映射到主流程的字段
//        for (ProcessNodeField startNodeField : startNodeProcessNodeFields) {
//          Long fieldId = startNodeField.getFieldId();
//          Field fieldInfoById = fieldService.getFieldInfoById(fieldId);
//          FieldValueAddParam fieldValueAddParam = new FieldValueAddParam();
//          fieldValueAddParam.setFieldId(startNodeField.getFieldId());
//          //查询此字段的关联值
//          if (FieldTypeEnum.SELECT.name().equals(fieldInfoById.getFieldType())) {
//            if (DefaultValueTypeEnum.LINK_FORM.name().equals(fieldInfoById.getDefaultValueType())) {
//              //下拉框的关联表单
//              Long linkFormId = fieldInfoById.getLinkFormId();
//              Long linkFieldId = fieldInfoById.getLinkFieldId();
//              if (rootFormId.equals(linkFormId)) {
//                //如果关联的表单是主流程表单，从主流程表单里面直接获取数据
//                String fieldValue = fieldValueMap.get(linkFieldId);
//                fieldValueAddParam.setFieldValue(fieldValue);
//                fieldValueAddParams.add(fieldValueAddParam);
//              }
//            }
//          }
//        }
//        Long primaryKeyValue = fieldValueService.addFormData(formId, fieldValueAddParams);
//        //已启动流程修改businessKey
//        runtimeService.updateBusinessKey(task.getProcessInstanceId(), primaryKeyValue.toString());
//
//      } else {
//        //修改流程表单数据
//        fieldValueService.updateFormData(formId, fieldValueAddParams, Long.valueOf(businessKey));
//      }
//    }
//    //设置任务的指向人
//    taskService.setAssignee(taskId, userId);
//    Map<String, Object> variables = new HashMap<>();
//    for (FieldValueAddParam param : fieldValueAddParams) {
//      variables.put("field" + param.getFieldId(), param.getFieldValue());
//    }
//    //完成任务
//    try {
//      taskService.complete(taskId, variables);
//    } catch (ActivitiException e) {
//      throw new BaseException("无法流转到下个流程节点");
//    }
//    return ResponseUtils.ok();
//  }
//
//  /**
//   * 回退（默认上一级）
//   */
//  @PostMapping("/rollBackTask")
//  public ReturnResult rollBackTask(String taskId) {
//// 1,根据任务ID查询任务实例
//    Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
//    if (task == null) {
//      throw new BaseException("当前任务不存在");
//    }
//    Execution execution = runtimeService.createExecutionQuery().executionId(task.getExecutionId())
//        .singleResult();
//    String currentActId = execution.getActivityId();
//
//    BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
//    FlowNode flowNode = (FlowNode) bpmnModel.getFlowElement(currentActId);
//    List<SequenceFlow> incomingFlows = flowNode.getIncomingFlows();
//    List<SequenceFlow> outgoingFlows = flowNode.getOutgoingFlows();
//    List<SequenceFlow> newOutFlows = new ArrayList<>();
//    int i = 0;
//    for (SequenceFlow sequenceFlow : incomingFlows) {
//      i++;
//      SequenceFlow newOutFlow = new SequenceFlow();
//      FlowElement source = sequenceFlow.getSourceFlowElement();
//      newOutFlow.setSourceRef(flowNode.getId());
//      newOutFlow.setSourceFlowElement(flowNode);
//      newOutFlow.setTargetRef(source.getId());
//      newOutFlow.setTargetFlowElement(source);
//      newOutFlow.setId("autoFlow" + i);
//      newOutFlow.setName("回退临时连线" + i);
//      newOutFlows.add(newOutFlow);
//    }
//    flowNode.setOutgoingFlows(newOutFlows);
//    taskService.complete(taskId);
//    //恢复连线
//    flowNode.setOutgoingFlows(outgoingFlows);
//    return ResponseUtils.ok();
//  }
//
//  @PostMapping("/revokeTask")
//  @Transactional
//  public ReturnResult revokeTask(String taskId) {
//    //1.查询当前历史任务
//    HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery()
//        .finished()
//        .taskId(taskId).singleResult();
//    if (historicTaskInstance == null) {
//      throw new BaseException("当前任务未完成，不能撤销");
//    }
//    //2.判断当前流程实例是否还在进行中
//    ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
//        .processInstanceId(historicTaskInstance.getProcessInstanceId()).singleResult();
//    if (processInstance == null) {
//      throw new BaseException("当前流程已结束，不能撤销");
//    }
//    //查询该节点的下个任务节点
//    BpmnModel bpmnModel = repositoryService
//        .getBpmnModel(historicTaskInstance.getProcessDefinitionId());
//    FlowNode flowNode = (FlowNode) bpmnModel
//        .getFlowElement(historicTaskInstance.getTaskDefinitionKey());
//    List<SequenceFlow> outgoingFlows = flowNode.getOutgoingFlows();
//    List<HistoricTaskInstance> nextTasks = new ArrayList<>();
//    for (SequenceFlow out : outgoingFlows) {
//      List<HistoricTaskInstance> finishedTask = historyService.createHistoricTaskInstanceQuery()
//          .finished()
//          .processInstanceId(historicTaskInstance.getProcessInstanceId())
//          .executionId(historicTaskInstance.getExecutionId()).taskDeleteReason(null)
//          .taskDefinitionKey(out.getTargetRef()).list();
//      if (finishedTask.size() > 0) {
//        throw new BaseException("下级节点任务已完成，不能撤销");
//      }
//      List<HistoricTaskInstance> allTask = historyService.createHistoricTaskInstanceQuery()
//          .processInstanceId(historicTaskInstance.getProcessInstanceId())
//          .executionId(historicTaskInstance.getExecutionId()).taskDeleteReason(null)
//          .taskDefinitionKey(out.getTargetRef()).list();
//      nextTasks.addAll(allTask);
//    }
//    CommandExecutor commandExecutor = ((TaskServiceImpl) taskService).getCommandExecutor();
//    for (HistoricTaskInstance taskInstance : nextTasks) {
//      TaskEntity task = (TaskEntity) taskService.createTaskQuery().taskId(taskInstance.getId())
//          .singleResult();
//      ExecutionEntity execution = (ExecutionEntity) runtimeService.createExecutionQuery()
//          .executionId(task.getExecutionId())
//          .singleResult();
//      List<HistoricActivityInstance> hisActList = historyService
//          .createHistoricActivityInstanceQuery().processInstanceId(task.getProcessInstanceId())
//          .executionId(task.getExecutionId()).activityId(task.getTaskDefinitionKey()).list();
//      HistoricActivityInstanceEntity activityInstanceEntity = null;
//      for (HistoricActivityInstance hisAct : hisActList) {
//        if (hisAct.getTaskId().equals(task.getId())) {
//          activityInstanceEntity = (HistoricActivityInstanceEntity) hisAct;
//        }
//
//      }
//      //删除下级的执行中任务
//      commandExecutor
//          .execute(new MyDeleteTaskCmd(task, execution, activityInstanceEntity, "上级节点撤销删除", true));
////      commandExecutor.execute(new MyDeleteActCmd(activityInstanceEntity));
//    }
//
//    //将当前任务的历史任务和活动的deleteReason写入已撤销
//    HistoricTaskInstanceEntity historicTaskInstanceEntity = (HistoricTaskInstanceEntity) historicTaskInstance;
//    historicTaskInstanceEntity.setDeleteReason("已撤销");
//
//    List<HistoricActivityInstance> hisActList = historyService
//        .createHistoricActivityInstanceQuery()
//        .processInstanceId(historicTaskInstance.getProcessInstanceId())
//        .executionId(historicTaskInstance.getExecutionId())
//        .activityId(historicTaskInstance.getTaskDefinitionKey()).list();
//    HistoricActivityInstanceEntity activityInstanceEntity = null;
//    for (HistoricActivityInstance hisAct : hisActList) {
//      if (hisAct.getTaskId().equals(historicTaskInstance.getId())) {
//        activityInstanceEntity = (HistoricActivityInstanceEntity) hisAct;
//        activityInstanceEntity.setDeleteReason("已撤销");
//      }
//
//    }
//    commandExecutor
//        .execute(new MyUpdateHistoricTaskCmd(historicTaskInstanceEntity, activityInstanceEntity));
//
//    //创建当前节点新的任务
//
//    ExecutionEntityImpl executionEntity = (ExecutionEntityImpl) runtimeService
//        .createExecutionQuery()
//        .executionId(historicTaskInstance.getProcessInstanceId()).singleResult();
//    ExecutionEntityImpl curTaskExecution = new ExecutionEntityImpl();
//    curTaskExecution.setId(historicTaskInstance.getExecutionId());
//    curTaskExecution.setRevision(executionEntity.getRevision());
//    curTaskExecution.setParentId(executionEntity.getId());
//    curTaskExecution.setProcessInstanceId(executionEntity.getProcessInstanceId());
//    curTaskExecution.setProcessDefinitionId(executionEntity.getProcessDefinitionId());
//    curTaskExecution.setRootProcessInstanceId(executionEntity.getRootProcessInstanceId());
//    curTaskExecution.setActive(true);
//    curTaskExecution.setConcurrent(false);
//    curTaskExecution.setScope(false);
//    curTaskExecution.setEventScope(false);
//    curTaskExecution.setMultiInstanceRoot(false);
//    curTaskExecution.setSuspensionState(1);
//    curTaskExecution.setCountEnabled(false);
//    curTaskExecution.setStartTime(new Date());
//
//    TaskEntityImpl taskEntity = new TaskEntityImpl();
//    taskEntity.setRevision(curTaskExecution.getRevision());
//    taskEntity.setExecutionId(curTaskExecution.getId());
//    taskEntity.setProcessInstanceId(curTaskExecution.getProcessInstanceId());
//    taskEntity.setProcessDefinitionId(curTaskExecution.getProcessDefinitionId());
//    taskEntity.setName(historicTaskInstance.getName());
//    taskEntity.setTaskDefinitionKey(historicTaskInstance.getTaskDefinitionKey());
//    taskEntity.setAssignee(historicTaskInstance.getAssignee());
//    taskEntity.setPriority(historicTaskInstance.getPriority());
//    taskEntity.setCreateTime(new Date());
//    taskEntity.setSuspensionState(1);
//
//    commandExecutor.execute(new MyCreateExecutionCmd(curTaskExecution));
//    commandExecutor.execute(new MyCreateTaskCmd(taskEntity));
//
//    return null;
//  }
//
//  @Deprecated
//  @PostMapping("/setProcessFailure")
//  public ReturnResult setProcessFailure(String taskId) {
//    Map<String, Object> variables = new HashMap<>();
//    variables.put(FlowConstant.PROCESS_FAIL, true);
//    taskService.complete(taskId, variables);
//    return ResponseUtils.ok();
//  }
//
//
//  @PostMapping("/deleteProcessInstance")
//  @Deprecated
//  public ReturnResult deleteProcessInstance(String processInstanceId, String processFunction) {
//    Execution execution = runtimeService.createExecutionQuery().executionId(processInstanceId)
//        .singleResult();
//    ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
//        .processInstanceId(processInstanceId).singleResult();
//    Map<String, Object> variables = runtimeService.getVariables(processInstanceId);
//    //设置当前流程为失败或者成功
//    if (ProcessFunctionEnum.PROCESS_END_ERROR.name().equals(processFunction)) {
//      runtimeService.setVariable(execution.getId(), FlowConstant.PROCESS_FAIL, true);
//    }
//    if (ProcessFunctionEnum.PROCESS_END_SUCCESS.name().equals(processFunction)) {
//      runtimeService.setVariable(execution.getId(), FlowConstant.PROCESS_FAIL, false);
//    }
//
//    if (execution.getSuperExecutionId() != null) {
//      if (ProcessFunctionEnum.PROCESS_END_ERROR.name().equals(processFunction)) {
//        runtimeService.setVariable(execution.getSuperExecutionId(), FlowConstant.SUB_PROCESS_FAIL,
//            true);
//      }
//      if (ProcessFunctionEnum.PROCESS_END_SUCCESS.name().equals(processFunction)) {
//        runtimeService.setVariable(execution.getSuperExecutionId(), FlowConstant.SUB_PROCESS_FAIL,
//            false);
//      }
//    }
//    CommandExecutor commandExecutor = ((TaskServiceImpl) taskService).getCommandExecutor();
//    commandExecutor.execute(new MyEndProcessCmd(processInstanceId, "手动结束流程", processFunction));
//
//    //查询按钮操作是否触发流程
//    ProcessTriggerQueryParam queryParam = new ProcessTriggerQueryParam();
//    queryParam.setMasterProcessDefinitionKey(processInstance.getProcessDefinitionKey());
//    queryParam.setTriggerType(ProcessTriggerTypeEnum.FUNCTION_TRIGGER.name());
//    queryParam.setProcessFunction(processFunction);
//    List<ProcessTrigger> processTriggers = processTriggerService.getByCondition(queryParam);
//    if (processTriggers.size() > 0) {
//      for (ProcessTrigger processTrigger : processTriggers) {
//        activitiService.startTriggerProcess(variables, processTrigger);
//      }
//    }
//    return ResponseUtils.ok();
//  }
//
//  @PostMapping("/suspendProcess")
//  @Deprecated
//  public ReturnResult suspendProcess(String processInstanceId) {
//    runtimeService.suspendProcessInstanceById(processInstanceId);
//    return ResponseUtils.ok();
//  }
//
//  @PostMapping("/continueExecution")
//  @Deprecated
//  public ReturnResult continueExecution(String executionId, String processInstanceKey) {
//    ProcessDefinition pro = repositoryService.createProcessDefinitionQuery()
//        .processDefinitionKey("form7721508919282176").singleResult();
//    CommandExecutor commandExecutor = ((TaskServiceImpl) taskService).getCommandExecutor();
//    ExecutionEntity execution = (ExecutionEntity) runtimeService.createExecutionQuery()
//        .executionId(executionId)
//        .singleResult();
//    HistoricActivityInstance historicActivityInstance = historyService
//        .createHistoricActivityInstanceQuery().activityId(execution.getActivityId())
//        .processInstanceId(execution.getProcessInstanceId()).list().get(0);
//    BpmnModel bpmnModel = repositoryService
//        .getBpmnModel(historicActivityInstance.getProcessDefinitionId());
//    CallActivity flowElement1 = (CallActivity) bpmnModel.getFlowElement(execution.getActivityId());
//    List<SequenceFlow> outgoingFlows = flowElement1.getOutgoingFlows();
//
//    FlowElement flowElement = outgoingFlows.get(0).getTargetFlowElement();
//    execution.setCurrentFlowElement(flowElement);
//    commandExecutor.execute(new MyCreateExecutionCmd(execution));
//    return null;
//  }
//
//
//  @GetMapping("/getProcessRate")
//  @Deprecated
//  public ReturnResult getProcessRate(String taskId) {
//    Map<String, Object> coordinate = new HashMap<>();
//    // 1,根据任务ID查询任务实例
//    Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
//    BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
//    //获取当前活动对象
//    FlowElement flowElement = bpmnModel.getFlowElement(task.getTaskDefinitionKey());
//
//    GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowElement.getId());
//
//    coordinate.put("x", graphicInfo.getX());
//    coordinate.put("y", graphicInfo.getY());
//    coordinate.put("width", graphicInfo.getWidth());
//    coordinate.put("height", graphicInfo.getHeight());
//    return ResponseUtils.ok(coordinate);
//  }
//
//
//  @GetMapping("/getTask")
//  @Deprecated
//  public ReturnResult getTask(String user) {
//    //1.获取我的待办任务
//    List<Task> tasks = taskService.createTaskQuery()
//        .taskCandidateOrAssigned(user)
//        .orderByTaskCreateTime().desc().list();
//    List<ActTaskDto> taskDtos = new ArrayList<>();
//    for (Task task : tasks) {
//      ActTaskDto taskDto = new ActTaskDto();
//      BeanUtils.copyProperties(task, taskDto);
//      taskDto.setTaskLocalVariables(taskService.getVariablesLocal(task.getId()));
//      taskDto.setProcessVariables(taskService.getVariables(task.getId()));
//      //查询当前任务关联的所有字段，并查出对应数据
//      taskDtos.add(taskDto);
//    }
//    return ResponseUtils.ok(taskDtos);
//  }
}
