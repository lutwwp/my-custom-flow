package com.yusei.service;

import com.yusei.model.dto.process.ProcessGuarderAbilityDto;
import com.yusei.model.entity.ProcessGuarderAbility;
import com.yusei.model.entity.ProcessNodeField;
import java.util.List;

public interface IProcessGuarderAbilityService {

  void insertBatch(List<ProcessGuarderAbility> processGuarderAbilities);

  void deleteByProcessInfoId(Long processInfoId);

  List<ProcessGuarderAbilityDto> getByProcessInfoId(Long processInfoId);

  List<ProcessGuarderAbility> getByProcessInfoIdAndUserId(Long processInfoId, String userId);
}
