package com.yusei.service.impl;

import com.yusei.dao.ProcessGuarderAbilityDao;
import com.yusei.model.dto.process.ProcessGuarderAbilityDto;
import com.yusei.model.entity.ProcessGuarderAbility;
import com.yusei.service.IProcessGuarderAbilityService;
import com.yusei.utils.BeanCastUtil;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProcessGuarderAbilityService implements IProcessGuarderAbilityService {

  @Autowired
  private ProcessGuarderAbilityDao processGuarderAbilityDao;

  @Override
  public void insertBatch(List<ProcessGuarderAbility> processGuarderAbilities) {
    processGuarderAbilityDao.insertBatch(processGuarderAbilities);
  }

  @Override
  public void deleteByProcessInfoId(Long processInfoId) {
    processGuarderAbilityDao.deleteByProcessInfoId(processInfoId);
  }

  @Override
  public List<ProcessGuarderAbilityDto> getByProcessInfoId(Long processInfoId) {
    List<ProcessGuarderAbility> processGuarderAbilities = processGuarderAbilityDao
        .getByProcessInfoId(processInfoId);
    return BeanCastUtil.castList(processGuarderAbilities, ProcessGuarderAbilityDto.class);
  }

  @Override
  public List<ProcessGuarderAbility> getByProcessInfoIdAndUserId(Long processInfoId,
      String userId) {
    return processGuarderAbilityDao.getByProcessInfoIdAndUserId(processInfoId, userId);
  }
}
