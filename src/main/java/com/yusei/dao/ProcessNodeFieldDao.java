package com.yusei.dao;

import com.yusei.model.entity.ProcessNodeField;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ProcessNodeFieldDao {

  void insertBatch(List<ProcessNodeField> processNodeFields);

  List<ProcessNodeField> getByProcessInfoId(Long processInfoId);

  void deleteByProcessInfoId(Long processInfoId);

  List<ProcessNodeField> getByInfoIdAndTaskKey(@Param("processInfoId") Long processInfoId,
      @Param("taskDefinitionKey") String taskDefinitionKey);

}
