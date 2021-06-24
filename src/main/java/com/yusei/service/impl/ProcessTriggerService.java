package com.yusei.service.impl;

import com.yusei.dao.ProcessTriggerDao;
import com.yusei.model.dto.process.ProcessTriggerDto;
import com.yusei.model.entity.Form;
import com.yusei.model.entity.ProcessTrigger;
import com.yusei.service.IFormService;
import com.yusei.service.IProcessTriggerService;
import com.yusei.utils.BeanCastUtil;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProcessTriggerService implements IProcessTriggerService {

  @Autowired
  private ProcessTriggerDao processTriggerDao;

  @Autowired
  private IFormService formService;

  @Override
  public void insertBatch(List<ProcessTrigger> processTriggers) {
    processTriggerDao.insertBatch(processTriggers);
  }

  @Override
  public List<ProcessTriggerDto> getByProcessInfoId(Long processInfoId) {
    List<ProcessTriggerDto> dtoList = new ArrayList<>();
    List<ProcessTrigger> processTriggers = processTriggerDao.getByProcessInfoId(processInfoId);
    for (ProcessTrigger processTrigger : processTriggers) {
      ProcessTriggerDto dto = BeanCastUtil.castBean(processTrigger, ProcessTriggerDto.class);
      Form form = formService.getFormById(dto.getSlaveFormId());
      dto.setSlaveFormName(form.getFormName());
      dtoList.add(dto);
    }
    return dtoList;
  }

  @Override
  public void deleteByProcessInfoId(Long processInfoId) {
    processTriggerDao.deleteByProcessInfoId(processInfoId);
  }

  @Override
  public List<ProcessTrigger> getByProcessInfoIdAndFunction(Long processInfoId,
      String processFunction) {
    return processTriggerDao.getByProcessInfoIdAndFunction(processInfoId, processFunction);
  }

  @Override
  public List<ProcessTrigger> getByProcessInfoIdAndFlowKey(Long processInfoId,
      String flowDefinitionKey) {
    return processTriggerDao.getByProcessInfoIdAndFlowKey(processInfoId, flowDefinitionKey);
  }
}
