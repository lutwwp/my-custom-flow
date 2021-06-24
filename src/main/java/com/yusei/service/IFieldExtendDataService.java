package com.yusei.service;

import com.yusei.model.dto.LinkFieldDetailDto;
import com.yusei.model.entity.FieldExtendData;
import com.yusei.model.entity.LinkField;

import java.util.List;

public interface IFieldExtendDataService {

    void insertBatch(List<FieldExtendData> fieldExtendData);


    /**
     * 根据条件查询FieldExtendData list
     */
    List<FieldExtendData> selectByRecord(FieldExtendData record);

    int deleteByFieldIdList(List<Long> fieldIdList);
}
