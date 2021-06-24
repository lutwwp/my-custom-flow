package com.yusei.dao;

import com.yusei.model.entity.ProcessNodeAttribute;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ProcessNodeAttributeDao {

  void insertBatch(List<ProcessNodeAttribute> processNodeAttributes);

  List<ProcessNodeAttribute> getByProcessInfoId(Long processInfoId);

  void deleteByProcessInfoId(Long processInfoId);

  ProcessNodeAttribute getByProcessInfoIdAndTaskKey(@Param("processInfoId") Long processInfoId,
      @Param("taskDefinitionKey") String taskDefinitionKey);
}
