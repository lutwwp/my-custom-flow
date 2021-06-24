package com.yusei.dao;

import com.yusei.model.entity.ProcessGuarderAbility;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ProcessGuarderAbilityDao {

  void insertBatch(List<ProcessGuarderAbility> processGuarderAbilities);

  void deleteByProcessInfoId(Long processInfoId);

  List<ProcessGuarderAbility> getByProcessInfoId(Long processInfoId);

  List<ProcessGuarderAbility> getByProcessInfoIdAndUserId(@Param("processInfoId") Long processInfoId,
      @Param("userId") String userId);
}
