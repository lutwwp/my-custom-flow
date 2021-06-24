package com.yusei.dao;


import com.yusei.common.PageParam;
import com.yusei.model.entity.ProcessInfo;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ProcessInfoDao {

  Integer getLatestVersionByProcessKey(String processDefinitionKey);

  void insert(ProcessInfo processInfo);

  void updateSelective(ProcessInfo processInfo);

  List<ProcessInfo> getProcessInfoByFormId(Long formId);

  ProcessInfo getProcessInfoById(Long processInfoId);

  ProcessInfo getProcessInfoByProcessDefinitionId(String processDefinitionId);

  void deleteProcessInfoById(Long processInfoId);

  void unActiveProcess(String processDefinitionKey);

  void updateProcessStatusById(@Param("processInfoId") Long processInfoId,
      @Param("status") Integer status);

  List<ProcessInfo> getOtherActiveProcessByPage(@Param("pageParam") PageParam pageParam,
      @Param("currentFormId") Long currentFormId);

  List<ProcessInfo> getActiveProcessByPage(@Param("pageParam") PageParam pageParam,
      @Param("processName") String processName);

  Long getActiveProcessCount(String processName);

  ProcessInfo getActiveProcessByFormId(Long formId);

  void setProcessInfoDeleted(Long processInfoId);

  ProcessInfo getActiveProcessByProcessDefinitionKey(String processDefinitionKey);
}
