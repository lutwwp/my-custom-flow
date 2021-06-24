package com.yusei.dao;

import com.yusei.model.entity.Field;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface FieldDao {

    void insertBatch(List<Field> fieldList);

    List<Field> getFieldInfoByFormId(Long formId);

    Field getFieldInfoById(Long fieldId);

    Field getPrimaryKeyByFormId(Long formId);

    List<Field> getFieldByFormIdList(@Param("list") List<Long> fieldIdList);

    int deleteByFieldIdList(@Param("list") List<Long> fieldIdList);

    int deleteByFormId(@Param("formId") Long formId);
}
