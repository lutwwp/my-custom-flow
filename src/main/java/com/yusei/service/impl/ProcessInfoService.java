package com.yusei.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yusei.common.PageParam;
import com.yusei.dao.ProcessInfoDao;
import com.yusei.enums.ProcessInfoStatusEnum;
import com.yusei.enums.ProcessTriggerTypeEnum;
import com.yusei.exception.BaseException;
import com.yusei.model.dto.process.ProcessDetailInfoDto;
import com.yusei.model.dto.process.ProcessFormInfoDto;
import com.yusei.model.dto.process.ProcessGuarderAbilityDto;
import com.yusei.model.dto.process.ProcessInfoDto;
import com.yusei.model.dto.process.ProcessNodeAttributeDto;
import com.yusei.model.dto.process.ProcessNodeFieldDetailDto;
import com.yusei.model.dto.process.ProcessTriggerDto;
import com.yusei.model.entity.Field;
import com.yusei.model.entity.Form;
import com.yusei.model.entity.ProcessGuarder;
import com.yusei.model.entity.ProcessGuarderAbility;
import com.yusei.model.entity.ProcessInfo;
import com.yusei.model.entity.ProcessNodeAttribute;
import com.yusei.model.entity.ProcessNodeField;
import com.yusei.model.entity.ProcessTrigger;
import com.yusei.model.param.process.ProcessGuarderAddParam;
import com.yusei.model.param.process.ProcessInfoAddParam;
import com.yusei.model.param.process.ProcessInfoUpdateParam;
import com.yusei.model.param.process.ProcessNodeFieldAddParam;
import com.yusei.model.param.process.ProcessTriggerAddParam;
import com.yusei.model.workFlow.EndEvent;
import com.yusei.model.workFlow.ParallelGateway;
import com.yusei.model.workFlow.ProcessParam;
import com.yusei.model.workFlow.SequenceFlow;
import com.yusei.model.workFlow.StartEvent;
import com.yusei.model.workFlow.SubProcessParam;
import com.yusei.model.workFlow.UserTask;
import com.yusei.service.IFieldService;
import com.yusei.service.IFormService;
import com.yusei.service.IProcessGuarderAbilityService;
import com.yusei.service.IProcessGuarderService;
import com.yusei.service.IProcessInfoService;
import com.yusei.service.IProcessNodeAttributeService;
import com.yusei.service.IProcessNodeFieldService;
import com.yusei.service.IProcessTriggerService;
import com.yusei.utils.BeanCastUtil;
import com.yusei.utils.IdWorker;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Service
public class ProcessInfoService implements IProcessInfoService {

  @Autowired
  private ProcessInfoDao processInfoDao;

  @Autowired
  private IFormService formService;

  @Autowired
  private IProcessNodeAttributeService processNodeAttributeService;

  @Autowired
  private IProcessTriggerService processTriggerService;

  @Autowired
  private IProcessGuarderService processGuarderService;

  @Autowired
  private IProcessGuarderAbilityService processGuarderAbilityService;

  @Autowired
  private IProcessNodeFieldService processNodeFieldService;

  @Autowired
  private IFieldService fieldService;


  //??????????????????API??????
  @Autowired
  private RepositoryService repositoryService;

  @Override
  public ProcessInfo getProcessInfoById(Long processInfoId) {
    return processInfoDao.getProcessInfoById(processInfoId);
  }

  @Override
  public ProcessInfo getProcessInfoByProcessDefinitionId(String processDefinitionId) {
    return processInfoDao.getProcessInfoByProcessDefinitionId(processDefinitionId);
  }

  @Override
  @Transactional
  public ProcessInfoDto addProcess(ProcessInfoAddParam addParam) throws JsonProcessingException {
    ProcessInfoDto dto = new ProcessInfoDto();
    //??????????????????????????????
    Long formId = addParam.getFormId();
    Form form = formService.getFormById(formId);
    String processDefinitionKey = "form" + formId;
    //??????????????????????????????xml????????????????????????
    //????????????
    List<ProcessNodeAttribute> processNodeAttributes = new ArrayList<>();
    //???????????????????????????
    List<ProcessTrigger> processTriggers = new ArrayList<>();
    ProcessParam processParam = addParam.getProcessParam();
    String processParamStr = null;
    byte[] processBytearray = null;
    if (processParam != null) {
      processParam.setId(processDefinitionKey);
      processParam.setName(form.getFormName());
      ObjectMapper objectMapper = new ObjectMapper();
      processParamStr = objectMapper.writeValueAsString(processParam);
      //????????????xml?????????
      processBytearray = createProcessDefinitionXML(processParam, processNodeAttributes,
          processTriggers);
    }

    //1.??????????????????????????????
    Integer latestVersion = processInfoDao.getLatestVersionByProcessKey(processDefinitionKey);
    int version = 1;
    if (latestVersion != null) {
      version = latestVersion + 1;
    }
    ProcessInfo processInfo = new ProcessInfo();
    processInfo.setProcessInfoId(IdWorker.getId());
    processInfo.setFormId(formId);
    processInfo.setProcessName(form.getFormName());
    processInfo.setProcessDefinitionKey(processDefinitionKey);
    processInfo.setVersion(version);
    processInfo.setProcessParam(processParamStr);
    processInfo.setProcessBytearray(processBytearray);
    processInfo.setStatus(ProcessInfoStatusEnum.DESIGN.getStatus());
    processInfo.setDeleted(false);
    processInfoDao.insert(processInfo);
    dto.setProcessInfoId(processInfo.getProcessInfoId());
    dto.setVersion(version);
    dto.setStatus(ProcessInfoStatusEnum.DESIGN.getStatus());

    //2.???????????????????????????
    if (processNodeAttributes.size() > 0) {
      processNodeAttributes.forEach(p -> {
        p.setProcessInfoId(processInfo.getProcessInfoId());
      });
      processNodeAttributeService.insertBatch(processNodeAttributes);
    }

    //3.????????????????????????
    List<ProcessTriggerAddParam> processTriggerAddParams = addParam.getProcessTriggerAddParams();
    if (!CollectionUtils.isEmpty(processTriggerAddParams)) {
      for (ProcessTriggerAddParam processTriggerAddParam : processTriggerAddParams) {
        ProcessTrigger processTrigger = new ProcessTrigger();
        processTrigger.setProcessTriggerId(IdWorker.getId());
        processTrigger.setMasterProcessDefinitionKey(processDefinitionKey);
        processTrigger.setTriggerType(ProcessTriggerTypeEnum.FUNCTION_TRIGGER.name());
        processTrigger.setProcessFunction(processTriggerAddParam.getProcessFunction());
        processTrigger.setSlaveFormId(processTriggerAddParam.getSlaveFormId());
        processTrigger
            .setSlaveProcessDefinitionKey(processTriggerAddParam.getSlaveProcessDefinitionKey());
        processTriggers.add(processTrigger);
      }
    }
    if (processTriggers.size() > 0) {
      processTriggers.forEach(p -> {
        p.setProcessInfoId(processInfo.getProcessInfoId());
      });
      processTriggerService.insertBatch(processTriggers);
    }

    //4.?????????????????????????????????
    List<ProcessGuarderAddParam> processGuarderAddParams = addParam
        .getProcessGuarderAddParams();
    if (!CollectionUtils.isEmpty(processGuarderAddParams)) {
      List<ProcessGuarder> processGuarders = new ArrayList<>();
      List<ProcessGuarderAbility> processGuarderAbilities = new ArrayList<>();
      for (ProcessGuarderAddParam param : processGuarderAddParams) {
        //???????????????
        ProcessGuarder processGuarder = new ProcessGuarder();
        processGuarder.setProcessGuarderId(IdWorker.getId());
        processGuarder.setProcessInfoId(processInfo.getProcessInfoId());
        processGuarder.setProcessDefinitionKey(processDefinitionKey);
        processGuarder.setUserId(param.getUserId());
        processGuarders.add(processGuarder);

        //?????????????????????
        ProcessGuarderAbility ability = new ProcessGuarderAbility();
        ability.setProcessGuarderAbilityId(IdWorker.getId());
        ability.setProcessInfoId(processInfo.getProcessInfoId());
        ability.setProcessDefinitionKey(processDefinitionKey);
        ability.setUserId(param.getUserId());
        ability.setProcessFunction(param.getProcessFunction());
        processGuarderAbilities.add(ability);
      }
      if (processGuarders.size() > 0) {
        processGuarders.forEach(p -> {
          p.setProcessInfoId(processInfo.getProcessInfoId());
        });
        processGuarderService.insertBatch(processGuarders);
      }
      if (processGuarderAbilities.size() > 0) {
        processGuarderAbilities.forEach(p -> {
          p.setProcessInfoId(processInfo.getProcessInfoId());
        });
        processGuarderAbilityService.insertBatch(processGuarderAbilities);
      }
    }

    //5.????????????????????????????????????????????????
    List<ProcessNodeFieldAddParam> processNodeFieldAddParams = addParam
        .getProcessNodeFieldAddParams();
    if (!CollectionUtils.isEmpty(processNodeFieldAddParams)) {
      List<ProcessNodeField> processNodeFields = new ArrayList<>();
      for (ProcessNodeFieldAddParam processNodeFieldAddParam : processNodeFieldAddParams) {
        ProcessNodeField processNodeField = new ProcessNodeField();
        processNodeField.setProcessNodeFieldId(IdWorker.getId());
        processNodeField.setProcessInfoId(processInfo.getProcessInfoId());
        processNodeField.setProcessDefinitionKey(processDefinitionKey);
        processNodeField.setTaskDefinitionKey(processNodeFieldAddParam.getTaskDefinitionKey());
        processNodeField.setFormId(formId);
        processNodeField.setFieldId(processNodeFieldAddParam.getFieldId());
        processNodeField.setShowField(processNodeFieldAddParam.getShowField());
        processNodeField.setEditField(processNodeFieldAddParam.getEditField());
        processNodeField.setSummary(processNodeFieldAddParam.getSummary());
        processNodeFields.add(processNodeField);
      }
      if (processNodeFields.size() > 0) {
        processNodeFields.forEach(p -> {
          p.setProcessInfoId(processInfo.getProcessInfoId());
        });
        processNodeFieldService.insertBatch(processNodeFields);
      }
    }
    return dto;
  }


  @Override
  public void updateProcess(ProcessInfoUpdateParam updateParam) throws JsonProcessingException {
    ProcessInfo processInfo = processInfoDao.getProcessInfoById(updateParam.getProcessInfoId());
    if (processInfo == null) {
      throw new BaseException("???????????????????????????");
    }
    Form form = formService.getFormById(processInfo.getFormId());
    //????????????
    List<ProcessNodeAttribute> processNodeAttributes = new ArrayList<>();
    //???????????????????????????
    List<ProcessTrigger> processTriggers = new ArrayList<>();
    ProcessParam processParam = updateParam.getProcessParam();
    String processParamStr = null;
    byte[] processBytearray = null;
    if (processParam != null) {
      processParam.setId(processInfo.getProcessDefinitionKey());
      processParam.setName(form.getFormName());
      ObjectMapper objectMapper = new ObjectMapper();
      processParamStr = objectMapper.writeValueAsString(processParam);
      //????????????xml?????????
      processBytearray = createProcessDefinitionXML(processParam, processNodeAttributes,
          processTriggers);
    }

    //1.??????????????????????????????
    processInfo.setProcessParam(processParamStr);
    processInfo.setProcessBytearray(processBytearray);
    processInfoDao.updateSelective(processInfo);

    //2.????????????????????????
    if (processNodeAttributes.size() > 0) {
      processNodeAttributeService.deleteByProcessInfoId(processInfo.getProcessInfoId());
      processNodeAttributes.forEach(p -> {
        p.setProcessInfoId(processInfo.getProcessInfoId());
      });
      processNodeAttributeService.insertBatch(processNodeAttributes);
    }

    //3.????????????????????????
    List<ProcessTriggerAddParam> processTriggerAddParams = updateParam.getProcessTriggerAddParams();
    if (!CollectionUtils.isEmpty(processTriggerAddParams)) {
      for (ProcessTriggerAddParam processTriggerAddParam : processTriggerAddParams) {
        ProcessTrigger processTrigger = new ProcessTrigger();
        processTrigger.setProcessTriggerId(IdWorker.getId());
        processTrigger.setMasterProcessDefinitionKey(processInfo.getProcessDefinitionKey());
        processTrigger.setTriggerType(ProcessTriggerTypeEnum.FUNCTION_TRIGGER.name());
        processTrigger.setProcessFunction(processTriggerAddParam.getProcessFunction());
        processTrigger.setSlaveFormId(processTriggerAddParam.getSlaveFormId());
        processTrigger
            .setSlaveProcessDefinitionKey(processTriggerAddParam.getSlaveProcessDefinitionKey());
        processTriggers.add(processTrigger);
      }
    }
    if (processTriggers.size() > 0) {
      processTriggerService.deleteByProcessInfoId(processInfo.getProcessInfoId());
      processTriggers.forEach(p -> {
        p.setProcessInfoId(processInfo.getProcessInfoId());
      });
      processTriggerService.insertBatch(processTriggers);
    }

    //4.?????????????????????????????????
    List<ProcessGuarderAddParam> processGuarderAddParams = updateParam
        .getProcessGuarderAddParams();
    if (!CollectionUtils.isEmpty(processGuarderAddParams)) {
      List<ProcessGuarder> processGuarders = new ArrayList<>();
      List<ProcessGuarderAbility> processGuarderAbilities = new ArrayList<>();
      for (ProcessGuarderAddParam param : processGuarderAddParams) {
        //???????????????
        ProcessGuarder processGuarder = new ProcessGuarder();
        processGuarder.setProcessGuarderId(IdWorker.getId());
        processGuarder.setProcessInfoId(processInfo.getProcessInfoId());
        processGuarder.setProcessDefinitionKey(processInfo.getProcessDefinitionKey());
        processGuarder.setUserId(param.getUserId());
        processGuarders.add(processGuarder);

        //?????????????????????
        ProcessGuarderAbility ability = new ProcessGuarderAbility();
        ability.setProcessGuarderAbilityId(IdWorker.getId());
        ability.setProcessInfoId(processInfo.getProcessInfoId());
        ability.setProcessDefinitionKey(processInfo.getProcessDefinitionKey());
        ability.setUserId(param.getUserId());
        ability.setProcessFunction(param.getProcessFunction());
        processGuarderAbilities.add(ability);
      }
      if (processGuarders.size() > 0) {
        processGuarderService.deleteByProcessInfoId(processInfo.getProcessInfoId());
        processGuarders.forEach(p -> {
          p.setProcessInfoId(processInfo.getProcessInfoId());
        });
        processGuarderService.insertBatch(processGuarders);
      }
      if (processGuarderAbilities.size() > 0) {
        processGuarderAbilityService.deleteByProcessInfoId(processInfo.getProcessInfoId());
        processGuarderAbilities.forEach(p -> {
          p.setProcessInfoId(processInfo.getProcessInfoId());
        });
        processGuarderAbilityService.insertBatch(processGuarderAbilities);
      }
    }

    //5.????????????????????????????????????????????????
    List<ProcessNodeFieldAddParam> processNodeFieldAddParams = updateParam
        .getProcessNodeFieldAddParams();
    if (!CollectionUtils.isEmpty(processNodeFieldAddParams)) {
      List<ProcessNodeField> processNodeFields = new ArrayList<>();
      for (ProcessNodeFieldAddParam processNodeFieldAddParam : processNodeFieldAddParams) {
        ProcessNodeField processNodeField = new ProcessNodeField();
        processNodeField.setProcessNodeFieldId(IdWorker.getId());
        processNodeField.setProcessInfoId(processInfo.getProcessInfoId());
        processNodeField.setProcessDefinitionKey(processInfo.getProcessDefinitionKey());
        processNodeField.setTaskDefinitionKey(processNodeFieldAddParam.getTaskDefinitionKey());
        processNodeField.setFormId(processInfo.getFormId());
        processNodeField.setFieldId(processNodeFieldAddParam.getFieldId());
        processNodeField.setShowField(processNodeFieldAddParam.getShowField());
        processNodeField.setEditField(processNodeFieldAddParam.getEditField());
        processNodeField.setSummary(processNodeFieldAddParam.getSummary());
        processNodeFields.add(processNodeField);
      }
      if (processNodeFields.size() > 0) {
        processNodeFieldService.deleteByProcessInfoId(processInfo.getProcessInfoId());
        processNodeFields.forEach(p -> {
          p.setProcessInfoId(processInfo.getProcessInfoId());
        });
        processNodeFieldService.insertBatch(processNodeFields);
      }
    }
  }

  @Override
  public List<ProcessInfo> getProcessInfoByFormId(Long formId) {
    return processInfoDao.getProcessInfoByFormId(formId);
  }

  @Override
  public ProcessDetailInfoDto getProcessDetailInfoById(Long processInfoId) throws IOException {
    ProcessInfo processInfo = processInfoDao.getProcessInfoById(processInfoId);
    if (processInfo == null) {
      throw new BaseException("???????????????????????????");
    }
    ProcessDetailInfoDto dto = BeanCastUtil.castBean(processInfo, ProcessDetailInfoDto.class);
    if (processInfo.getProcessParam() != null) {
      //1.????????????????????????
      ObjectMapper objectMapper = new ObjectMapper();
      ProcessParam processParam = objectMapper
          .readValue(processInfo.getProcessParam(), ProcessParam.class);
      dto.setProcessParam(processParam);
    }

    //2.?????????????????????????????????
    List<ProcessNodeFieldDetailDto> processNodeFields = processNodeFieldService
        .getByProcessInfoId(processInfoId);
    dto.setProcessNodeFields(processNodeFields);

    //3.??????????????????
    List<ProcessNodeAttributeDto> processNodeAttributes = processNodeAttributeService
        .getByProcessInfoId(processInfoId);
    dto.setProcessNodeAttributes(processNodeAttributes);

    //4.?????????????????????
    List<ProcessTriggerDto> processTriggers = processTriggerService
        .getByProcessInfoId(processInfoId);
    dto.setProcessTriggers(processTriggers);

    //5.??????????????????????????????????????????
    List<ProcessGuarderAbilityDto> processGuarderAbilities = processGuarderAbilityService
        .getByProcessInfoId(processInfoId);
    dto.setProcessGuarderAbilities(processGuarderAbilities);
    return dto;
  }

  @Override
  @Transactional
  public void deleteProcessInfoById(Long processInfoId) {
    //??????????????????????????????
    ProcessInfo processInfo = processInfoDao.getProcessInfoById(processInfoId);
    if (processInfo == null) {
      throw new BaseException("???????????????????????????");
    }
    if (!ProcessInfoStatusEnum.DESIGN.getStatus().equals(processInfo.getStatus())) {
      throw new BaseException("???????????????????????????????????????");
    }

    //1.???????????????????????????
    processInfoDao.deleteProcessInfoById(processInfoId);

    //2.????????????????????????
    processNodeFieldService.deleteByProcessInfoId(processInfoId);

    //3.????????????????????????
    processNodeAttributeService.deleteByProcessInfoId(processInfoId);

    //4.?????????????????????
    processTriggerService.deleteByProcessInfoId(processInfoId);

    //5.?????????????????????
    processGuarderService.deleteByProcessInfoId(processInfoId);

    //6.????????????????????????
    processGuarderAbilityService.deleteByProcessInfoId(processInfoId);
  }

  @Override
  @Transactional
  public void deployProcessById(Long processInfoId) {
    //??????????????????????????????
    ProcessInfo processInfo = processInfoDao.getProcessInfoById(processInfoId);
    if (processInfo == null) {
      throw new BaseException("???????????????????????????");
    }
    if (ProcessInfoStatusEnum.ACTIVE.getStatus().equals(processInfo.getStatus())) {
      throw new BaseException("?????????????????????????????????");
    } else if (ProcessInfoStatusEnum.HISTORY.getStatus().equals(processInfo.getStatus())) {
      //??????????????????????????????????????????deploy????????????????????????deleted??????true????????????????????????????????????
      processInfoDao.unActiveProcess(processInfo.getProcessDefinitionKey());
      InputStream in = new ByteArrayInputStream(processInfo.getProcessBytearray());
      Form form = formService.getFormById(processInfo.getFormId());
      String formName = form.getFormName();
      Deployment deployment = repositoryService.createDeployment().name(formName)
          .addInputStream(formName + ".bpmn", in).deploy();
      if (deployment == null) {
        throw new BaseException("????????????");
      }
      processInfoDao.setProcessInfoDeleted(processInfoId);
      ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
          .deploymentId(deployment.getId()).singleResult();
      ProcessInfo newInfo = BeanCastUtil.castBean(processInfo, ProcessInfo.class);
      newInfo.setProcessDefinitionId(processDefinition.getId());
      newInfo.setProcessVersion(processDefinition.getVersion());
      newInfo.setStatus(ProcessInfoStatusEnum.ACTIVE.getStatus());
      processInfoDao.insert(newInfo);
    } else if (ProcessInfoStatusEnum.DESIGN.getStatus().equals(processInfo.getStatus())) {
      //?????????????????????????????????????????????????????????
      processInfoDao.unActiveProcess(processInfo.getProcessDefinitionKey());
      InputStream in = new ByteArrayInputStream(processInfo.getProcessBytearray());
      Form form = formService.getFormById(processInfo.getFormId());
      String formName = form.getFormName();
      Deployment deployment = repositoryService.createDeployment().name(formName)
          .addInputStream(formName + ".bpmn", in).deploy();
      if (deployment == null) {
        throw new BaseException("????????????");
      }
      ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
          .deploymentId(deployment.getId()).singleResult();
      processInfo.setProcessDefinitionId(processDefinition.getId());
      processInfo.setProcessVersion(processDefinition.getVersion());
      processInfo.setStatus(ProcessInfoStatusEnum.ACTIVE.getStatus());
      processInfoDao.updateSelective(processInfo);
    }
  }

  @Override
  public List<ProcessFormInfoDto> getOtherActiveProcessByPage(PageParam pageParam, Long currentFormId) {
    List<ProcessFormInfoDto> dtoList = new ArrayList<>();
    List<ProcessInfo> processInfoList = processInfoDao.getOtherActiveProcessByPage(pageParam, currentFormId);
    for (ProcessInfo processInfo : processInfoList) {
      ProcessFormInfoDto dto = new ProcessFormInfoDto();
      dto.setProcessInfoId(processInfo.getProcessInfoId());
      dto.setProcessDefinitionKey(processInfo.getProcessDefinitionKey());
      dto.setFormId(processInfo.getFormId());
      dto.setProcessName(processInfo.getProcessName());
      dto.setVersion(processInfo.getVersion());
      dtoList.add(dto);
    }
    return dtoList;
  }

  @Override
  public List<ProcessFormInfoDto> getActiveProcessByPage(PageParam pageParam, String processName) {
    List<ProcessFormInfoDto> dtoList = new ArrayList<>();
    List<ProcessInfo> processInfoList = processInfoDao.getActiveProcessByPage(pageParam, processName);
    for (ProcessInfo processInfo : processInfoList) {
      ProcessFormInfoDto dto = new ProcessFormInfoDto();
      dto.setProcessInfoId(processInfo.getProcessInfoId());
      dto.setProcessDefinitionKey(processInfo.getProcessDefinitionKey());
      dto.setFormId(processInfo.getFormId());
      dto.setProcessName(processInfo.getProcessName());
      dto.setVersion(processInfo.getVersion());
      dtoList.add(dto);
    }
    return dtoList;
  }

  @Override
  public Long getActiveProcessCount(String processName) {
    return processInfoDao.getActiveProcessCount(processName);
  }

  @Override
  public ProcessInfo getActiveProcessByFormId(Long formId) {
    return processInfoDao.getActiveProcessByFormId(formId);
  }

  @Override
  @Deprecated
  public void insert(ProcessInfo processInfo) {
    processInfoDao.insert(processInfo);
  }

  @Override
  public ProcessInfo getActiveProcessByProcessDefinitionKey(String processDefinitionKey) {
    return processInfoDao.getActiveProcessByProcessDefinitionKey(processDefinitionKey);
  }

  @Override
  public List<ProcessNodeFieldDetailDto> getStartProcessFieldByFormId(Long formId) {
    List<ProcessNodeFieldDetailDto> dtoList = new ArrayList<>();
    ProcessInfo processInfo = processInfoDao.getActiveProcessByFormId(formId);
    if (processInfo == null) {
      throw new BaseException("?????????????????????????????????");
    }
    List<ProcessNodeField> processNodeFields = processNodeFieldService
        .getByInfoIdAndTaskKey(processInfo.getProcessInfoId(), "theStart");
    for (ProcessNodeField processNodeField : processNodeFields) {
      ProcessNodeFieldDetailDto dto = new ProcessNodeFieldDetailDto();
      dto.setTaskDefinitionKey(processNodeField.getTaskDefinitionKey());
      Long fieldId = processNodeField.getFieldId();
      dto.setFieldId(fieldId);
      Field field = fieldService.getFieldInfoById(fieldId);
      dto.setFieldName(field.getFieldName());
      dto.setFieldType(field.getFieldType());
      dto.setShowField(processNodeField.getShowField());
      dto.setEditField(processNodeField.getEditField());
      dtoList.add(dto);
    }
    return dtoList;
  }

  /**
   * ????????????xml??????.
   *
   * @param processParam ????????????
   */
  private byte[] createProcessDefinitionXML(ProcessParam processParam,
      List<ProcessNodeAttribute> processNodeAttributes, List<ProcessTrigger> processTriggers) {
    StartEvent startEventParam = processParam.getStartEvent();
    List<EndEvent> endEventList = processParam.getEndEventList();
    List<UserTask> userTaskList = processParam.getUserTaskList();
    List<SequenceFlow> sequenceFlowList = processParam.getSequenceFlowList();
    List<SubProcessParam> subProcessList = processParam.getSubProcessList();
    List<ParallelGateway> parallelGatewayList = processParam.getParallelGatewayList();

    Document document = DocumentHelper.createDocument();
    Element definitions = document
        .addElement("definitions", "http://www.omg.org/spec/BPMN/20100524/MODEL");
    definitions.addNamespace("", "http://www.omg.org/spec/BPMN/20100524/MODEL");
    definitions.addAttribute("targetNamespace", "http://www.activiti.org/test");
    definitions.addAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
    definitions.addAttribute("xmlns:activiti", "http://activiti.org/bpmn");
    definitions.addAttribute("xmlns:xsd", "http://www.w3.org/2001/XMLSchema");
    definitions.addAttribute("xmlns:bpmndi", "http://www.omg.org/spec/BPMN/20100524/DI");
    definitions.addAttribute("xmlns:omgdc", "http://www.omg.org/spec/DD/20100524/DC");
    definitions.addAttribute("xmlns:omgdi", "http://www.omg.org/spec/DD/20100524/DI");
    definitions.addAttribute("typeLanguage", "http://www.w3.org/2001/XMLSchema");
    definitions.addAttribute("expressionLanguage", "http://www.w3.org/1999/XPath");

    definitions.addNamespace("bpmndi", "http://www.omg.org/spec/BPMN/20100524/DI");
    definitions.addNamespace("omgdc", "http://www.omg.org/spec/DD/20100524/DC");
    definitions.addNamespace("omgdi", "http://www.omg.org/spec/DD/20100524/DI");
    definitions.addNamespace("activiti", "http://activiti.org/bpmn");

    Element process = definitions.addElement("process");
    process.addAttribute("id", processParam.getId());
    process.addAttribute("name", processParam.getName());
    process.addAttribute("isExecutable", "true");

    //????????????
    Element startEvent = process.addElement("startEvent");
    startEvent.addAttribute("id", "startEvent");

    // ??????????????????????????????????????????????????????,????????????????????????????????????????????????????????????
    Element startFlow = process.addElement("sequenceFlow");
    startFlow.addAttribute("id", "startFlow");
    startFlow.addAttribute("sourceRef", "startEvent");
    startFlow.addAttribute("targetRef", startEventParam.getId());
    Element startFlowExtensionElements = startFlow.addElement("extensionElements");
    Element startFlowListener = DocumentHelper.createElement("activiti:executionListener");
    startFlowListener.addNamespace("activiti", "http://activiti.org/bpmn");
    startFlowListener.addAttribute("class", "com.yusei.listener.StartFlowListener");
    startFlowExtensionElements.add(startFlowListener);

    Element manualTask = process.addElement("manualTask");
    manualTask.addAttribute("id", startEventParam.getId());
    manualTask.addAttribute("name", startEventParam.getName());
    if (StringUtils.isNotEmpty(startEventParam.getDefaultFlow())) {
      manualTask.addAttribute("default", startEventParam.getDefaultFlow());
    }

    //????????????
    for (EndEvent endEventParam : endEventList) {
      Element endEvent = process.addElement("endEvent");
      endEvent.addAttribute("id", endEventParam.getId());
      endEvent.addAttribute("name", endEventParam.getName());
    }

    //????????????
    for (UserTask userTaskParam : userTaskList) {
      Element userTask = process.addElement("userTask");
      userTask.addAttribute("id", userTaskParam.getId());
      userTask.addAttribute("name", userTaskParam.getName());
      StringBuilder sb = new StringBuilder("");
      if (userTaskParam.getTaskCandidateUsers() != null) {
        for (String user : userTaskParam.getTaskCandidateUsers()) {
          if (sb.length() == 0) {
            sb.append(user);
          } else {
            sb.append(",").append(user);
          }
        }
      }
      userTask.addAttribute("activiti:candidateUsers", sb.toString());
      if (userTaskParam.getDefaultFlow() != null) {
        userTask.addAttribute("default", userTaskParam.getDefaultFlow());
      }
      ProcessNodeAttribute processNodeAttribute = new ProcessNodeAttribute();
      processNodeAttribute.setProcessNodeAttributeId(IdWorker.getId());
      processNodeAttribute.setProcessDefinitionKey(processParam.getId());
      processNodeAttribute.setTaskDefinitionKey(userTaskParam.getId());
      processNodeAttribute
          .setRollBack(userTaskParam.getRollBack() == null ? false : userTaskParam.getRollBack());
      processNodeAttribute
          .setRevoke(userTaskParam.getRevoke() == null ? false : userTaskParam.getRevoke());
      processNodeAttributes.add(processNodeAttribute);
    }

    //??????
    for (SequenceFlow sequenceFlowParam : sequenceFlowList) {
      Element sequenceFlow = process.addElement("sequenceFlow");
      sequenceFlow.addAttribute("id", sequenceFlowParam.getId());
      String sequenceFlowName = sequenceFlowParam.getName();
      if (StringUtils.isNotEmpty(sequenceFlowName)) {
        sequenceFlow.addAttribute("name", sequenceFlowParam.getName());
      }
      sequenceFlow.addAttribute("sourceRef", sequenceFlowParam.getSourceRef());
      sequenceFlow.addAttribute("targetRef", sequenceFlowParam.getTargetRef());

      //?????????????????????????????????
      Element extensionElements = sequenceFlow.addElement("extensionElements");
      Element flowListener = DocumentHelper.createElement("activiti:executionListener");
      flowListener.addNamespace("activiti", "http://activiti.org/bpmn");
      flowListener.addAttribute("class", "com.yusei.listener.FlowTriggerListener");
      extensionElements.add(flowListener);

      //?????????????????????????????????????????????
      if ("endError".equals(sequenceFlowParam.getTargetRef())) {
        Element flowListener2 = DocumentHelper.createElement("activiti:executionListener");
        flowListener2.addNamespace("activiti", "http://activiti.org/bpmn");
        flowListener2.addAttribute("class", "com.yusei.listener.EndErrorFlowListener");
        extensionElements.add(flowListener2);
      }
      //???????????????????????????????????????
      List<ProcessTriggerAddParam> processTriggerAddParams = sequenceFlowParam
          .getProcessTriggerAddParams();
      if (!CollectionUtils.isEmpty(processTriggerAddParams)) {
        for (ProcessTriggerAddParam processTriggerAddParam : processTriggerAddParams) {
          ProcessTrigger processTrigger = new ProcessTrigger();
          processTrigger.setProcessTriggerId(IdWorker.getId());
          processTrigger.setMasterProcessDefinitionKey(processParam.getId());
          processTrigger.setTriggerType(ProcessTriggerTypeEnum.FLOW_TRIGGER.name());
          processTrigger.setFlowDefinitionKey(sequenceFlowParam.getId());
          processTrigger.setSlaveFormId(processTriggerAddParam.getSlaveFormId());
          processTrigger
              .setSlaveProcessDefinitionKey(processTriggerAddParam.getSlaveProcessDefinitionKey());
          processTriggers.add(processTrigger);
        }
      }

      //???????????????????????????
      String expression = sequenceFlowParam.getExpression();
      if (StringUtils.isNotEmpty(expression)) {
        Element conditionExpression = sequenceFlow.addElement("conditionExpression");
        conditionExpression.addAttribute("xsi:type", "tFormalExpression");
        conditionExpression.addCDATA(expression);
      }
    }

    //?????????
    if (null != subProcessList && subProcessList.size() > 0) {
      for (int i = 0; i < subProcessList.size(); i++) {
        Element callActivity = process.addElement("callActivity");
        callActivity.addAttribute("activiti:exclusive", "true");
        callActivity.addAttribute("calledElement", subProcessList.get(i).getProcessDefinitionKey());
        callActivity.addAttribute("id", subProcessList.get(i).getId());
        callActivity.addAttribute("name", subProcessList.get(i).getName());
        Element extensionElements = callActivity.addElement("extensionElements");
        Namespace nameSpace = new Namespace("activiti", "http://activiti.org/bpmn");

        Element inParam = DocumentHelper.createElement("activiti:in");
        inParam.addNamespace("activiti", "http://activiti.org/bpmn");
        inParam.addAttribute("source", "processFail");
        inParam.addAttribute("target", "processFail");
        extensionElements.add(inParam);

        Element outParam = DocumentHelper.createElement("activiti:out");
        outParam.addNamespace("activiti", "http://activiti.org/bpmn");
        outParam.addAttribute("source", "processFail");
        outParam.addAttribute("target", "subProcessFail");
        extensionElements.add(outParam);
      }
    }

    //????????????
    if (null != parallelGatewayList && parallelGatewayList.size() > 0) {
      for (int i = 0; i < parallelGatewayList.size(); i++) {
        Element parallelGateway = process.addElement("parallelGateway");
        parallelGateway.addAttribute("id", parallelGatewayList.get(i).getId());
        parallelGateway.addAttribute("name", parallelGatewayList.get(i).getName());
      }
    }

    String xml = document.asXML();
    xml = xml.replaceAll("xmlns=\"\"", "");
    return xml.getBytes();
  }
}
