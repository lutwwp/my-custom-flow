package com.yusei.service;

import com.yusei.model.dto.LinkFieldDetailDto;
import com.yusei.model.entity.LinkField;

import java.util.List;

public interface ILinkFieldService {

    void insertBatch(List<LinkField> linkFieldList);

    List<LinkFieldDetailDto> getByFieldId(Long fieldId);

    int deleteByFieldIdList(List<Long> fieldIdList);
}
