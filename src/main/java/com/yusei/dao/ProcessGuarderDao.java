package com.yusei.dao;

import com.yusei.model.entity.ProcessGuarder;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProcessGuarderDao {

  void insertBatch(List<ProcessGuarder> processGuarders);

  List<ProcessGuarder> getByUserId(String userId);

  void deleteByProcessInfoId(Long processInfoId);
}
