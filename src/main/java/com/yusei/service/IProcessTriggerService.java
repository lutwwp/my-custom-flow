package com.yusei.service;

import com.yusei.model.dto.process.ProcessTriggerDto;
import com.yusei.model.entity.ProcessTrigger;
import java.util.List;

public interface IProcessTriggerService {

  void insertBatch(List<ProcessTrigger> processTriggers);

  List<ProcessTriggerDto> getByProcessInfoId(Long processInfoId);

  void deleteByProcessInfoId(Long processInfoId);

  List<ProcessTrigger> getByProcessInfoIdAndFunction(Long processInfoId, String processFunction);

  List<ProcessTrigger> getByProcessInfoIdAndFlowKey(Long processInfoId, String flowDefinitionKey);

}
