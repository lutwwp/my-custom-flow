package com.yusei.service.impl;

import com.yusei.dao.LinkFieldDao;
import com.yusei.model.dto.LinkFieldDetailDto;
import com.yusei.model.entity.Field;
import com.yusei.model.entity.LinkField;
import com.yusei.service.IFieldService;
import com.yusei.service.ILinkFieldService;
import com.yusei.utils.BeanCastUtil;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
public class LinkFieldService implements ILinkFieldService {

    @Autowired
    private LinkFieldDao linkFieldDao;

    @Autowired
    private IFieldService fieldService;

    @Override
    public void insertBatch(List<LinkField> linkFieldList) {
        if (!CollectionUtils.isEmpty(linkFieldList)) {
            linkFieldDao.insertBatch(linkFieldList);
        }
    }

    @Override
    public List<LinkFieldDetailDto> getByFieldId(Long fieldId) {
        List<LinkField> linkFields = linkFieldDao.getByFieldId(fieldId);
        List<LinkFieldDetailDto> dtos = new ArrayList<>();
        for (LinkField linkField : linkFields) {
            LinkFieldDetailDto dto = BeanCastUtil.castBean(linkField, LinkFieldDetailDto.class);
            Field field = fieldService.getFieldInfoById(linkField.getSlaveFieldId());
            if (field != null) {
                dto.setSlaveFieldName(field.getFieldName());
                dtos.add(dto);
            }
        }
        return dtos;
    }

    @Override
    public int deleteByFieldIdList(List<Long> fieldIdList) {
        if (CollectionUtils.isEmpty(fieldIdList)) {
            return 0;
        }
        return linkFieldDao.deleteByFieldIdList(fieldIdList);
    }
}
