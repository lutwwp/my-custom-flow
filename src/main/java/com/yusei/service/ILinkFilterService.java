package com.yusei.service;

import com.yusei.model.dto.LinkFilterDetailDto;
import com.yusei.model.entity.LinkFilter;

import java.util.List;

public interface ILinkFilterService {

    void insertBatch(List<LinkFilter> linkFilterList);

    List<LinkFilterDetailDto> getByFieldId(Long fieldId);

    List<LinkFilter> getByCurrentFormFieldId(Long currentFormFieldId);

    int deleteByFieldIdList(List<Long> fieldIdList);
}
