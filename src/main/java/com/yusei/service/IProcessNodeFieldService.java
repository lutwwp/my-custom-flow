package com.yusei.service;

import com.yusei.model.dto.process.ProcessNodeFieldDetailDto;
import com.yusei.model.entity.ProcessNodeField;
import java.util.List;

public interface IProcessNodeFieldService {

  void insertBatch(List<ProcessNodeField> processNodeFields);

  List<ProcessNodeFieldDetailDto> getByProcessInfoId(Long processInfoId);

  void deleteByProcessInfoId(Long processInfoId);

  List<ProcessNodeField> getByInfoIdAndTaskKey(Long processInfoId, String taskDefinitionKey);
}
