package com.yusei.service.impl;

import com.yusei.dao.LinkFilterDao;
import com.yusei.model.dto.LinkFilterDetailDto;
import com.yusei.model.entity.Field;
import com.yusei.model.entity.LinkFilter;
import com.yusei.service.IFieldService;
import com.yusei.service.ILinkFilterService;
import com.yusei.utils.BeanCastUtil;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
public class LinkFilterService implements ILinkFilterService {

  @Autowired
  private LinkFilterDao linkFilterDao;

  @Autowired
  private IFieldService fieldService;

  @Override
  public void insertBatch(List<LinkFilter> linkFilterList) {
    if (!CollectionUtils.isEmpty(linkFilterList)) {
      linkFilterDao.insertBatch(linkFilterList);
    }
  }

  @Override
  public List<LinkFilterDetailDto> getByFieldId(Long fieldId) {
    List<LinkFilter> linkFilters = linkFilterDao.getByFieldId(fieldId);
    List<LinkFilterDetailDto> dtos = new ArrayList<>();
    for (LinkFilter linkFilter : linkFilters) {
      LinkFilterDetailDto dto = BeanCastUtil.castBean(linkFilter, LinkFilterDetailDto.class);
      Field currentFormField = fieldService.getFieldInfoById(linkFilter.getCurrentFormFieldId());
      Field linkFormField = fieldService.getFieldInfoById(linkFilter.getLinkFormFieldId());
      if (currentFormField != null && linkFormField != null) {
        dto.setCurrentFormFieldName(currentFormField.getFieldName());
        dto.setLinkFormFieldName(linkFormField.getFieldName());
        dtos.add(dto);
      }
    }
    return dtos;
  }

  @Override
  public List<LinkFilter> getByCurrentFormFieldId(Long currentFormFieldId) {
    return linkFilterDao.getByCurrentFormFieldId(currentFormFieldId);
  }

    @Override
    public int deleteByFieldIdList(List<Long> fieldIdList) {
      if (CollectionUtils.isEmpty(fieldIdList)) {
        return 0;
      }
      return linkFilterDao.deleteByFieldIdList(fieldIdList);
    }
}
