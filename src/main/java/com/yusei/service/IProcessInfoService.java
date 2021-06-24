package com.yusei.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.yusei.common.PageParam;
import com.yusei.model.dto.process.ProcessDetailInfoDto;
import com.yusei.model.dto.process.ProcessFormInfoDto;
import com.yusei.model.dto.process.ProcessInfoDto;
import com.yusei.model.dto.process.ProcessNodeFieldDetailDto;
import com.yusei.model.entity.ProcessInfo;
import com.yusei.model.param.process.ProcessInfoAddParam;
import com.yusei.model.param.process.ProcessInfoUpdateParam;
import com.yusei.model.workFlow.ProcessParam;
import java.io.IOException;
import java.util.List;

public interface IProcessInfoService {

  ProcessInfo getProcessInfoById(Long processInfoId);

  ProcessInfo getProcessInfoByProcessDefinitionId(String processDefinitionId);

  ProcessInfoDto addProcess(ProcessInfoAddParam addParam) throws JsonProcessingException;

  void updateProcess(ProcessInfoUpdateParam updateParam) throws JsonProcessingException;

  List<ProcessInfo> getProcessInfoByFormId(Long formId);

  ProcessDetailInfoDto getProcessDetailInfoById(Long processInfoId) throws IOException;

  void deleteProcessInfoById(Long processInfoId);

  void deployProcessById(Long processInfoId);

  List<ProcessFormInfoDto> getOtherActiveProcessByPage(PageParam pageParam, Long currentFormId);

  List<ProcessFormInfoDto> getActiveProcessByPage(PageParam pageParam, String processName);

  Long getActiveProcessCount(String processName);

  ProcessInfo getActiveProcessByFormId(Long formId);

  void insert(ProcessInfo processInfo);

  ProcessInfo getActiveProcessByProcessDefinitionKey(String processDefinitionKey);

  List<ProcessNodeFieldDetailDto> getStartProcessFieldByFormId(Long formId);
}
