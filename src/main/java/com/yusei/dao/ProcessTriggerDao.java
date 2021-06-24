package com.yusei.dao;

import com.yusei.model.entity.ProcessTrigger;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ProcessTriggerDao {

  void insertBatch(List<ProcessTrigger> processTriggers);

  List<ProcessTrigger> getByProcessInfoId(Long processInfoId);

  void deleteByProcessInfoId(Long processInfoId);

  List<ProcessTrigger> getByProcessInfoIdAndFunction(@Param("processInfoId") Long processInfoId,
      @Param("processFunction") String processFunction);

  List<ProcessTrigger> getByProcessInfoIdAndFlowKey(@Param("processInfoId") Long processInfoId,
      @Param("flowDefinitionKey") String flowDefinitionKey);

}
