package com.yusei.dao;

import com.yusei.model.entity.LinkField;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface LinkFieldDao {

  void insertBatch(List<LinkField> linkFieldList);

  List<LinkField> getByFieldId(Long fieldId);

    int deleteByFieldIdList(@Param("list") List<Long> fieldIdList);
}
