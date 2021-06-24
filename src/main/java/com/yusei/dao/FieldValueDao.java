package com.yusei.dao;

import com.yusei.common.PageParam;
import com.yusei.model.entity.FieldValue;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface FieldValueDao {

  void insertBatch(List<FieldValue> fieldValues);

  List<FieldValue> getByFormIdAndPrimaryKeyValue(@Param("formId") Long formId,
      @Param("primaryKeyValue") Long primaryKeyValue);

  void updateBatch(List<FieldValue> updateList);

  List<Long> getPrimaryKeyValueByPage(@Param("pageParam") PageParam pageParam, @Param("formId") Long formId);

  Long countFormData(Long formId);

  Long getLinkFormPrimaryKeyValue(@Param("formId") Long formId,
      @Param("fieldId")Long fieldId, @Param("sqlCommandFilter")String sqlCommandFilter);

  List<FieldValue> getByFormIdAndFieldId(@Param("formId") Long formId,
      @Param("fieldId") Long fieldId);
}
