package com.yusei.service.impl;

import com.yusei.common.PageParam;
import com.yusei.dao.FieldValueDao;
import com.yusei.enums.DefaultValueTypeEnum;
import com.yusei.enums.FieldTypeEnum;
import com.yusei.enums.LinkMethodEnum;
import com.yusei.model.dto.*;
import com.yusei.model.entity.Field;
import com.yusei.model.entity.FieldValue;
import com.yusei.model.entity.LinkFilter;
import com.yusei.model.param.FieldValueAddParam;
import com.yusei.model.param.SubFieldValueAddParam;
import com.yusei.service.IFieldService;
import com.yusei.service.IFieldValueService;
import com.yusei.service.ILinkFieldService;
import com.yusei.service.ILinkFilterService;
import com.yusei.utils.IdWorker;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Service
public class FieldValueService implements IFieldValueService {

    @Autowired
    private FieldValueDao fieldValueDao;

    @Autowired
    private IFieldService fieldService;

    @Autowired
    private ILinkFieldService linkFieldService;

    @Autowired
    private ILinkFilterService linkFilterService;


    //todo 校验字段的数据是否正确
    private void checkFieldValue(Field fieldInfo, String value) {

    }

    @Override
    @Transactional
    public Long addFormData(Long formId, List<FieldValueAddParam> fieldValueAddParams) {
        checkAddFormParam(formId, fieldValueAddParams);
        List<FieldValue> fieldValues = new ArrayList<>();
        //设置表单主键的数据
        Field primaryKey = fieldService.getPrimaryKeyByFormId(formId);
        FieldValue primaryKeyValueEntity = new FieldValue();
        primaryKeyValueEntity.setFieldValueId(IdWorker.getId());
        primaryKeyValueEntity.setFormId(formId);
        primaryKeyValueEntity.setFieldId(primaryKey.getFieldId());
        primaryKeyValueEntity.setFieldType(FieldTypeEnum.PRIMARY_KEY.name());
        Long primaryKeyValue = IdWorker.getId();
        primaryKeyValueEntity.setFieldValue(String.valueOf(primaryKeyValue));
        primaryKeyValueEntity.setPrimaryKeyValue(primaryKeyValue);
        fieldValues.add(primaryKeyValueEntity);

        List<Field> fieldList = fieldService.getFieldInfoByFormId(formId);
        Map<Long, Field> fieldMap = new HashMap<>();
        fieldList.forEach(e -> {
            fieldMap.put(e.getFieldId(), e);

            if (e.getFieldType().equals(FieldTypeEnum.SERIAL_NUMBER.name())) {
                FieldValueAddParam param = new FieldValueAddParam();
                param.setFieldId(e.getFieldId());
                param.setFieldCode(e.getFieldCode());

                fieldValueAddParams.add(param);
            }
        });

        for (FieldValueAddParam addParam : fieldValueAddParams) {
            Long subPimaryKeyValue = null;
            if (!CollectionUtils.isEmpty(addParam.getSubFieldValueAddParams())) {
                subPimaryKeyValue = addFormData(addParam.getFieldId(), addParam.getSubFieldValueAddParams());
                addParam.setGroupId(subPimaryKeyValue);
            }

            //先查询出字段的详情
            Field fieldInfo = fieldMap.get(addParam.getFieldId());
            FieldValue fieldValue = new FieldValue();


            if (fieldInfo.getFieldType().equals(FieldTypeEnum.SERIAL_NUMBER.name())) {
                fieldValue.setFieldValue(IdWorker.getId() + "");
            } else {
                checkFieldValue(fieldInfo, addParam.getFieldValue());
                fieldValue.setFieldValue(addParam.getFieldValue());
            }

            if (fieldValue.getFieldValue() != null) {
                fieldValue.setFieldValueId(IdWorker.getId());
                fieldValue.setFormId(formId);
                fieldValue.setFieldId(addParam.getFieldId());
                fieldValue.setFieldType(fieldInfo.getFieldType());
                fieldValue.setPrimaryKeyValue(primaryKeyValue);
                fieldValue.setGroupId(addParam.getGroupId());
                fieldValues.add(fieldValue);
            }


        }

        //批量新增数据
        fieldValueDao.insertBatch(fieldValues);
        return primaryKeyValue;
    }

    /**
     * <p>description: 新增数据校验 </p>
     *
     * @author: liuqiang
     * @date: 2020/8/31  14:44
     */
    private void checkAddFormParam(Long formId, List<FieldValueAddParam> fieldValueAddParams) {
        Assert.notEmpty(fieldValueAddParams, "新增数据为空");
        Assert.notNull(formId, "表单为空");
        List<Field> fieldInfoList = fieldService.getFieldInfoByFormId(formId);
        Map<Long, Field> fieldMap = fieldInfoList.stream().collect(Collectors.toMap(Field::getFieldId, Function.identity()));

        fieldValueAddParams.forEach(e -> {
            Field field = fieldMap.get(e.getFieldId());
            if (Objects.nonNull(field.getNecessary()) && StringUtils.isEmpty(e.getFieldValue())) {
                throw new IllegalArgumentException("字段不能为空：" + field.getFieldName());
            }

            if (Objects.nonNull(field.getAllowRepeated()) && !field.getAllowRepeated()) {

                Long linkFormPrimaryKeyValue = fieldValueDao.getLinkFormPrimaryKeyValue(formId, e.getFieldId(), "field_value = '" + e.getFieldValue() + "'");
                if (linkFormPrimaryKeyValue != null) {
                    throw new IllegalArgumentException("数据不允许重复：" + e.getFieldValue());
                }
            }

        });

    }

    @Override
    public Map<Long, String> getByFormIdAndPrimaryKeyValue(Long formId, Long primaryKeyValue) {
        Map<Long, String> fieldValueMap = new HashMap<>();
        List<FieldValue> fieldValues = fieldValueDao.getByFormIdAndPrimaryKeyValue(formId, primaryKeyValue);
        for (FieldValue fieldValue : fieldValues) {
            if (FieldTypeEnum.PRIMARY_KEY.name().equals(fieldValue.getFieldType())){
                continue;
            }
            if (FieldTypeEnum.SUB_TABLE.name().equals(fieldValue.getFieldType())){
                List<FieldValue> subFieldValueList = fieldValueDao.getByFormIdAndPrimaryKeyValue(fieldValue.getFieldId(), fieldValue.getGroupId());
                if ( !CollectionUtils.isEmpty(subFieldValueList)){
                    subFieldValueList.forEach(e -> {
                        fieldValueMap.put(e.getFieldId(), e.getFieldValue());
                    });
                }
            }

            fieldValueMap.put(fieldValue.getFieldId(), fieldValue.getFieldValue());
        }
        return fieldValueMap;
    }

    @Override
    public void updateFormData(Long formId, List<FieldValueAddParam> fieldValueAddParams,
                               Long primaryKeyValue) {
        List<FieldValue> addList = new ArrayList<>();
        List<FieldValue> updateList = new ArrayList<>();
        Map<Long, FieldValue> fieldValueMap = new HashMap<>();
        List<FieldValue> fieldValues = fieldValueDao.getByFormIdAndPrimaryKeyValue(formId, primaryKeyValue);
        for (FieldValue fieldValue : fieldValues) {
            fieldValueMap.put(fieldValue.getFieldId(), fieldValue);
        }
        for (FieldValueAddParam addParam : fieldValueAddParams) {
            FieldValue fieldValue = fieldValueMap.get(addParam.getFieldId());
            if (fieldValue == null) {
                fieldValue = new FieldValue();
                fieldValue.setFieldValueId(IdWorker.getId());
                fieldValue.setFormId(formId);
                fieldValue.setFieldId(addParam.getFieldId());
                fieldValue.setFieldValue(addParam.getFieldValue());
                fieldValue.setPrimaryKeyValue(primaryKeyValue);
                addList.add(fieldValue);
            } else {
                fieldValue.setFieldValue(addParam.getFieldValue());
                updateList.add(fieldValue);
            }
        }

        if (addList.size() > 0) {
            fieldValueDao.insertBatch(addList);
        }
        if (updateList.size() > 0) {
            fieldValueDao.updateBatch(updateList);
        }
    }

    @Override
    public List<FieldValueRecordDto> getFormDataByPage(PageParam pageParam, Long formId) {
        List<FieldValueRecordDto> dtos = new ArrayList<>();
        List<Long> primaryKeyValues = fieldValueDao.getPrimaryKeyValueByPage(pageParam, formId);

        for (Long primaryKeyValue : primaryKeyValues) {
            FieldValueRecordDto dto = new FieldValueRecordDto();
            dto.setId(primaryKeyValue);
            List<FieldValueInfoDto> fieldValueInfoDtos = new ArrayList<>();
            List<FieldValue> fieldValues = fieldValueDao.getByFormIdAndPrimaryKeyValue(formId, primaryKeyValue);
            for (FieldValue fieldValue : fieldValues) {
                if (FieldTypeEnum.PRIMARY_KEY.name().equals(fieldValue.getFieldType())
                        || FieldTypeEnum.LINK_QUERY.name().equals(fieldValue.getFieldType())) {
                    continue;
                }
                FieldValueInfoDto fieldValueInfoDto = new FieldValueInfoDto();
                fieldValueInfoDto.setFieldId(fieldValue.getFieldId());
                fieldValueInfoDto.setFieldValue(fieldValue.getFieldValue());

                if (FieldTypeEnum.SUB_TABLE.name().equals(fieldValue.getFieldType())){
                    List<FieldValue> subFieldValues = fieldValueDao.getByFormIdAndPrimaryKeyValue(fieldValue.getFieldId(), fieldValue.getGroupId());

                    List<FieldValueInfoDto> subFieldValueInfoDtos = new ArrayList<>();
                    subFieldValues.forEach(subValue -> {
                        if (FieldTypeEnum.PRIMARY_KEY.name().equals(subValue.getFieldType())
                                || FieldTypeEnum.LINK_QUERY.name().equals(subValue.getFieldType())) {
                            return;
                        }

                        FieldValueInfoDto subFieldValueInfoDto = new FieldValueInfoDto();
                        subFieldValueInfoDto.setFieldId(subValue.getFieldId());
                        subFieldValueInfoDto.setFieldValue(subValue.getFieldValue());
                        subFieldValueInfoDto.setGroupId(subValue.getGroupId());
                        subFieldValueInfoDtos.add(subFieldValueInfoDto);
                    });

                    fieldValueInfoDto.setSubFieldValueInfoDtos(subFieldValueInfoDtos);
                }


                fieldValueInfoDtos.add(fieldValueInfoDto);
            }
            dto.setFieldValueInfoDtos(fieldValueInfoDtos);
            dtos.add(dto);
        }
        return dtos;
    }

    @Override
    public Long countFormData(Long formId) {
        return fieldValueDao.countFormData(formId);
    }

    @Override
    public List<FieldValueDetailDto> getFormDataDetail(Long formId, Long primaryKeyValue) {
        List<FieldValueDetailDto> dtos = new ArrayList<>();
        Map<Long, String> fieldValueMap = getByFormIdAndPrimaryKeyValue(formId, primaryKeyValue);
        List<Field> fieldList = fieldService.getFieldInfoByFormId(formId);
        for (Field field : fieldList) {
            FieldValueDetailDto dto = new FieldValueDetailDto();
            dto.setFieldId(field.getFieldId());
            dto.setFieldName(field.getFieldName());
            dto.setFieldType(field.getFieldType());
            dto.setShowField(field.getShowField());
            dto.setEditField(field.getEditField());
            if (FieldTypeEnum.LINK_QUERY.name().equals(field.getFieldType())) {
                //关联查询
                Long linkFormId = field.getLinkFormId();
                List<LinkFilterDetailDto> linkFilters = linkFilterService.getByFieldId(field.getFieldId());
                LinkFilterDetailDto linkFilter = linkFilters.get(0);
                dto.setLinkFilters(linkFilters);
                //查询关联表要展示的字段
                List<LinkFieldDetailDto> linkFields = linkFieldService.getByFieldId(field.getFieldId());
                dto.setLinkFields(linkFields);
                String currentFormFieldValue = fieldValueMap.get(linkFilter.getCurrentFormFieldId());
                if (!StringUtils.isEmpty(currentFormFieldValue)) {
                    String sqlCommandFilter = createSqlCommandFilter(currentFormFieldValue,
                            linkFilter.getMethod());
                    Long LinkFormPrimaryKeyValue = fieldValueDao
                            .getLinkFormPrimaryKeyValue(linkFormId, linkFilter.getLinkFormFieldId(),
                                    sqlCommandFilter);
                    if (LinkFormPrimaryKeyValue != null) {
                        List<FieldValueDetailDto> linkFieldDetailDtos = new ArrayList<>();
                        //查询关联表的数据
                        Map<Long, String> linkFormFieldValueMap = getByFormIdAndPrimaryKeyValue(linkFormId, LinkFormPrimaryKeyValue);

                        for (LinkFieldDetailDto linkField : linkFields) {
                            FieldValueDetailDto linkFieldDetailDto = new FieldValueDetailDto();
                            linkFieldDetailDto.setFieldId(linkField.getSlaveFieldId());
                            linkFieldDetailDto.setFieldName(linkField.getSlaveFieldName());
                            linkFieldDetailDto.setFieldValue(linkFormFieldValueMap.get(linkField.getSlaveFieldId()));
                            linkFieldDetailDtos.add(linkFieldDetailDto);
                        }
                        dto.setLinkFieldDetailDtos(linkFieldDetailDtos);
                    }
                }
            } else {
                dto.setFieldValue(fieldValueMap.get(field.getFieldId()));
            }

            dtos.add(dto);
        }
        return dtos;
    }

    private String createSqlCommandFilter(String currentFormFieldValue, String method) {
        StringBuilder sqlCommandFilter = new StringBuilder();
        if (LinkMethodEnum.EQUALS.name().equals(method)) {
            sqlCommandFilter.append("field_value = '").append(currentFormFieldValue).append("'");
        }
        return sqlCommandFilter.toString();
    }

    @Override
    public List<String> getSelectFieldDataSource(Long fieldId) {
        List<String> valueList = new ArrayList<>();
        Field field = fieldService.getFieldInfoById(fieldId);
        if (FieldTypeEnum.SELECT.name().equals(field.getFieldType())) {
            if (DefaultValueTypeEnum.LINK_FORM.name().equals(field.getDefaultValueType())) {
                //下拉框的关联表单
                Long linkFormId = field.getLinkFormId();
                Long linkFieldId = field.getLinkFieldId();
                List<FieldValue> fieldValues = fieldValueDao.getByFormIdAndFieldId(linkFormId, linkFieldId);
                for (FieldValue fieldValue : fieldValues) {
                    valueList.add(fieldValue.getFieldValue());
                }
            }
        }
        return valueList;
    }

    @Override
    public List<LinkQueryFieldValueDto> getLinkQueryDataSource(Long fieldId, String fieldValue) {
        List<LinkQueryFieldValueDto> dtoList = new ArrayList<>();
        //1.查询出所有以此字段为条件的关联查询
        List<LinkFilter> linkFilters = linkFilterService.getByCurrentFormFieldId(fieldId);
        //2.循环处理所有关联查询
        for (LinkFilter linkFilter : linkFilters) {
            LinkQueryFieldValueDto dto = new LinkQueryFieldValueDto();
            dto.setFieldId(linkFilter.getMasterFieldId());
            //3.根据查询筛选条件获取关联表单的一条记录
            Field masterField = fieldService.getFieldInfoById(linkFilter.getMasterFieldId());
            String sqlCommandFilter = createSqlCommandFilter(fieldValue,
                    linkFilter.getMethod());

            // 判断是否为子表单
            Field subField = fieldService.getFieldInfoById(masterField.getFormId());
            if (subField != null && FieldTypeEnum.SUB_TABLE.name().equals( subField.getFieldType() )){
                masterField.setLinkFormId(subField.getLinkFormId());
            }

            Long LinkFormPrimaryKeyValue = fieldValueDao
                    .getLinkFormPrimaryKeyValue(masterField.getLinkFormId(), linkFilter.getLinkFormFieldId(),
                            sqlCommandFilter);
            if (LinkFormPrimaryKeyValue != null) {
                List<FieldLinkInfoDto> fieldValueInfoDtos = new ArrayList<>();
                //查询关联表的数据
                Map<Long, String> linkFormFieldValueMap = getByFormIdAndPrimaryKeyValue(masterField.getLinkFormId(), LinkFormPrimaryKeyValue);
                //查询关联表要展示的字段
                List<LinkFieldDetailDto> linkFields = linkFieldService.getByFieldId(linkFilter.getMasterFieldId());
                if (FieldTypeEnum.SUB_TABLE.name().equals(masterField.getFieldType()) ||
                    (FieldTypeEnum.TEXT.name().equals(masterField.getFieldType()) || FieldTypeEnum.LINK_DATA.name().equals(masterField.getFieldType()))
                    && DefaultValueTypeEnum.JOINT_DATA.name().equals(masterField.getDefaultValueType())) {
                    dto.setFieldValue(linkFormFieldValueMap.get(linkFields.get(0).getSlaveFieldId()));
                }
                for (LinkFieldDetailDto linkField : linkFields) {
                    FieldLinkInfoDto linkFieldInfolDto = new FieldLinkInfoDto();
                    linkFieldInfolDto.setFieldId(linkField.getSlaveFieldId());
                    linkFieldInfolDto.setFieldValue(linkFormFieldValueMap.get(linkField.getSlaveFieldId()));

                    fieldValueInfoDtos.add(linkFieldInfolDto);
                }

                dto.setFieldValueInfoDtos(fieldValueInfoDtos);
                dtoList.add(dto);
            }
        }
        return dtoList;
    }

    @Override
    public List<FieldValue> getByFormIdAndFieldId(Long formId, Long fieldId) {
        return fieldValueDao.getByFormIdAndFieldId(formId, fieldId);
    }
}
