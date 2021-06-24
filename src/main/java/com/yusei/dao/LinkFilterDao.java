package com.yusei.dao;

import com.yusei.model.entity.LinkFilter;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface LinkFilterDao {

  void insertBatch(List<LinkFilter> linkFilterList);

  List<LinkFilter> getByFieldId(Long fieldId);

  List<LinkFilter> getByCurrentFormFieldId(Long currentFormFieldId);

    int deleteByFieldIdList(@Param("list") List<Long> fieldIdList);
}
