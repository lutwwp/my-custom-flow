package com.yusei.service.impl;

import com.yusei.dao.ProcessGuarderDao;
import com.yusei.model.entity.ProcessGuarder;
import com.yusei.service.IProcessGuarderService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProcessGuarderService implements IProcessGuarderService {

  @Autowired
  private ProcessGuarderDao processGuarderDao;

  @Override
  public void insertBatch(List<ProcessGuarder> processGuarders) {
    processGuarderDao.insertBatch(processGuarders);
  }

  @Override
  public List<ProcessGuarder> getByUserId(String userId) {
    return processGuarderDao.getByUserId(userId);
  }

  @Override
  public void deleteByProcessInfoId(Long processInfoId) {
    processGuarderDao.deleteByProcessInfoId(processInfoId);
  }
}
