package com.yusei.service.impl;

import com.yusei.dao.FieldDao;
import com.yusei.model.entity.Field;
import com.yusei.service.IFieldService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
public class FieldService implements IFieldService {

    @Autowired
    private FieldDao fieldDao;

    @Override
    public void insertBatch(List<Field> fieldList) {
        if (!CollectionUtils.isEmpty(fieldList)) {
            fieldDao.insertBatch(fieldList);
        }
    }

    @Override
    public List<Field> getFieldInfoByFormId(Long formId) {
        return fieldDao.getFieldInfoByFormId(formId);
    }

    @Override
    public Field getFieldInfoById(Long fieldId) {
        return fieldDao.getFieldInfoById(fieldId);
    }

    @Override
    public Field getPrimaryKeyByFormId(Long formId) {
        return fieldDao.getPrimaryKeyByFormId(formId);
    }

    @Override
    public List<Field> getFieldByFormIdList(List<Long> fieldIdList) {
        return fieldDao.getFieldByFormIdList(fieldIdList);
    }

    @Override
    public int deleteByFieldIdList(List<Long> fieldIdList) {
      if (CollectionUtils.isEmpty(fieldIdList)){
        return 0;
      }

      return fieldDao.deleteByFieldIdList(fieldIdList);
    }

    @Override
    public int deleteByFormId(Long formId) {
        return fieldDao.deleteByFormId(formId);
    }
}
