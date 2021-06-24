package com.yusei.service.impl;

import com.yusei.dao.ProcessNodeAttributeDao;
import com.yusei.model.dto.process.ProcessNodeAttributeDto;
import com.yusei.model.entity.ProcessNodeAttribute;
import com.yusei.service.IProcessNodeAttributeService;
import com.yusei.utils.BeanCastUtil;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
public class ProcessNodeAttributeService implements IProcessNodeAttributeService {

  @Autowired
  private ProcessNodeAttributeDao processNodeAttributeDao;

  @Override
  public void insertBatch(List<ProcessNodeAttribute> processNodeAttributes) {
    if (!CollectionUtils.isEmpty(processNodeAttributes)) {
      processNodeAttributeDao.insertBatch(processNodeAttributes);
    }
  }

  @Override
  public List<ProcessNodeAttributeDto> getByProcessInfoId(Long processInfoId) {
    List<ProcessNodeAttribute> processNodeAttributes = processNodeAttributeDao
        .getByProcessInfoId(processInfoId);
    List<ProcessNodeAttributeDto> dtoList = BeanCastUtil
        .castList(processNodeAttributes, ProcessNodeAttributeDto.class);
    return dtoList;
  }

  @Override
  public void deleteByProcessInfoId(Long processInfoId) {
    processNodeAttributeDao.deleteByProcessInfoId(processInfoId);
  }

  @Override
  public ProcessNodeAttribute getByProcessInfoIdAndTaskKey(Long processInfoId,
      String taskDefinitionKey) {
    return processNodeAttributeDao.getByProcessInfoIdAndTaskKey(processInfoId, taskDefinitionKey);
  }
}
