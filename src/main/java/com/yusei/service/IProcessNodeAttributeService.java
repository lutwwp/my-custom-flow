package com.yusei.service;

import com.yusei.model.dto.process.ProcessNodeAttributeDto;
import com.yusei.model.entity.ProcessNodeAttribute;
import java.util.List;

public interface IProcessNodeAttributeService {

  void insertBatch(List<ProcessNodeAttribute> processNodeAttributes);

  List<ProcessNodeAttributeDto> getByProcessInfoId(Long processInfoId);

  void deleteByProcessInfoId(Long processInfoId);

  ProcessNodeAttribute getByProcessInfoIdAndTaskKey(Long processInfoId, String taskDefinitionKey);
}
