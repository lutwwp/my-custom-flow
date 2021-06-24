package com.yusei.service.impl;

import com.yusei.dao.ProcessNodeFieldDao;
import com.yusei.model.dto.process.ProcessNodeFieldDetailDto;
import com.yusei.model.entity.Field;
import com.yusei.model.entity.ProcessNodeField;
import com.yusei.service.IFieldService;
import com.yusei.service.IProcessNodeFieldService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProcessNodeFieldService implements IProcessNodeFieldService {

  @Autowired
  private ProcessNodeFieldDao processNodeFieldDao;

  @Autowired
  private IFieldService fieldService;

  @Override
  public void insertBatch(List<ProcessNodeField> processNodeFields) {
    processNodeFieldDao.insertBatch(processNodeFields);
  }

  @Override
  public List<ProcessNodeFieldDetailDto> getByProcessInfoId(Long processInfoId) {
    List<ProcessNodeFieldDetailDto> dtoList = new ArrayList<>();
    List<ProcessNodeField> processNodeFields = processNodeFieldDao.getByProcessInfoId(processInfoId);
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
      dto.setSummary(processNodeField.getSummary());
      dtoList.add(dto);
    }
    return dtoList;
  }

  @Override
  public void deleteByProcessInfoId(Long processInfoId) {
    processNodeFieldDao.deleteByProcessInfoId(processInfoId);
  }

  @Override
  public List<ProcessNodeField> getByInfoIdAndTaskKey(Long processInfoId,
      String taskDefinitionKey) {
    return processNodeFieldDao.getByInfoIdAndTaskKey(processInfoId, taskDefinitionKey);
  }

}
