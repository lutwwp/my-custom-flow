package com.yusei.dao;

import com.yusei.common.PageParam;
import com.yusei.model.entity.Form;
import com.yusei.model.param.FormQueryParam;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface FormDao {


  void insertEntity(Form form);

  List<Form> getFormByPage(@Param("pageParam") PageParam pageParam,
      @Param("queryParam") FormQueryParam queryParam);

  Form getById(Long formId);

  void updateEntity(Form form);
}
