package com.yusei.service;

import com.yusei.model.entity.Field;

import java.util.List;

public interface IFieldService {

    void insertBatch(List<Field> fieldList);

    List<Field> getFieldInfoByFormId(Long formId);

    Field getFieldInfoById(Long fieldId);

    Field getPrimaryKeyByFormId(Long formId);

    List<Field> getFieldByFormIdList(List<Long> fieldIdList);

    int deleteByFieldIdList(List<Long> fieldIdList);

    int deleteByFormId(Long formId);
}
