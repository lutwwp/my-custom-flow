package com.yusei.service;

import com.yusei.model.entity.ProcessGuarder;
import java.util.List;

public interface IProcessGuarderService {

  void insertBatch(List<ProcessGuarder> processGuarders);

  List<ProcessGuarder> getByUserId(String userId);

  void deleteByProcessInfoId(Long processInfoId);
}
