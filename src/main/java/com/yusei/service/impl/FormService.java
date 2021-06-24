package com.yusei.service.impl;

import com.yusei.common.PageParam;
import com.yusei.dao.FormDao;
import com.yusei.enums.*;
import com.yusei.model.dto.*;
import com.yusei.model.entity.*;
import com.yusei.model.param.*;
import com.yusei.service.*;
import com.yusei.utils.BeanCastUtil;
import com.yusei.utils.IdWorker;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

@Service
public class FormService implements IFormService {

  @Autowired
  private FormDao formDao;

  @Autowired
  private IFieldService fieldService;

  @Autowired
  private ILinkFieldService linkFieldService;

  @Autowired
  private ILinkFilterService linkFilterService;

  @Autowired
  private IFieldExtendDataService fieldExtendDataService;

  @Autowired
  private IFieldExtendRuleService fieldExtendRuleService;

  @Autowired
  private IFieldExtendRuleDetailService fieldExtendRuleDetailService;

  public static List<String> DATE_LINK = Arrays.asList(
          FieldTypeEnum.TEXT.name(), FieldTypeEnum.LINK_DATA.name(), FieldTypeEnum.SUB_TABLE.name()
  );

  @Override
  public Field initFormPrimaryKey(Long formId) {
    Field field = new Field();
    field.setFieldId(IdWorker.getId());
    field.setFormId(formId);
    field.setFieldCode("primary_key");
    field.setOrderNumber(-1);
    field.setFieldName("ID");
    field.setFieldType(FieldTypeEnum.PRIMARY_KEY.name());
    field.setDefaultValueType(DefaultValueTypeEnum.NULL.name());
    field.setNecessary(true);
    field.setAllowRepeated(false);
    field.setShowField(false);
    field.setEditField(false);
    return field;
  }

  @Override
  @Transactional
  public String addForm(FormAddParam addParam) {
    // 1.表单非空校验
    checkAddParam(addParam);

    // 2.初始化容器
    List<Field> fieldList = new ArrayList<>();
    List<LinkField> linkFieldList = new ArrayList<>();
    List<LinkFilter> linkFilterList = new ArrayList<>();
    List<FieldExtendData> fieldExtendDataList = new ArrayList<>();
    List<FieldExtendRule> fieldExtendRuleList = new ArrayList<>();
    List<FieldExtendRuleDetail> fieldExtendRuleDetailList = new ArrayList<>();


    // 3.新增表单/更新表单
    Form form = new Form();
    form.setFormName(addParam.getFormName());
    form.setFormType(addParam.getFormType());
    if ( addParam.getFormId() != null ){
      // 更新表单， 获取原表单所有 field，
      form.setFormId(addParam.getFormId());
      formDao.updateEntity(form);

      // 删除所有更新数据的 field、link field、 link filter、extend 信息,
      deleteAllFieldInfo(addParam.getFormId());
    }else{
      form.setFormId(IdWorker.getId());
      formDao.insertEntity(form);

      // 初始化表单主键字段
      Field primaryKeyField = initFormPrimaryKey(form.getFormId());
      fieldList.add(primaryKeyField);
    }



    // 5.将表单字段数据根据fieldCode分组
    Map<String, Long> fieldIdMap = buildFieldCodeMap(addParam.getFieldAddParams(), form.getFormId());

    // 6.新增表单的字段和字段的一些属性
    for (FieldAddParam fieldAddParam : addParam.getFieldAddParams()) {
      buildTableDataInfo(addParam.getFieldAddParams(), fieldList, linkFieldList, linkFilterList,
              fieldExtendDataList, fieldExtendRuleList,
              fieldExtendRuleDetailList, fieldIdMap,
              fieldAddParam);


      // 表单为子表单
      boolean subTable = FieldTypeEnum.SUB_TABLE.name().equals(fieldAddParam.getFieldType());
      if (subTable){
        List<FieldAddParam> fieldAddParams = fieldAddParam.getSubFieldAddParams();

        // 删除所有更新数据的 field、link field、 link filter、extend 信息,
        deleteAllFieldInfo(fieldAddParam.getFieldId());

        Map<String, Long> subFieldIdMap = buildFieldCodeMap(fieldAddParams, fieldAddParam.getFieldId());
        fieldIdMap.entrySet().forEach(entry -> subFieldIdMap.put(entry.getKey(), entry.getValue()));

        // 初始化表单主键字段
        Field subPrimaryKeyField = initFormPrimaryKey(fieldAddParam.getFieldId());
        fieldList.add(subPrimaryKeyField);
        if ( !CollectionUtils.isEmpty(fieldAddParams)){
          for (FieldAddParam param : fieldAddParams) {
            buildTableDataInfo(fieldAddParams, fieldList, linkFieldList, linkFilterList,
                    fieldExtendDataList, fieldExtendRuleList,
                    fieldExtendRuleDetailList, subFieldIdMap,
                    param);
          }
        }
      }

    }

    fieldService.insertBatch(fieldList);
    linkFieldService.insertBatch(linkFieldList);
    linkFilterService.insertBatch(linkFilterList);
    fieldExtendDataService.insertBatch(fieldExtendDataList);
    fieldExtendRuleService.insertBatch(fieldExtendRuleList);
    fieldExtendRuleDetailService.insertBatch(fieldExtendRuleDetailList);
    return form.getFormId().toString();
  }

  private void deleteAllFieldInfo(Long formId) {
    List<Field> fieldInfoByForm = fieldService.getFieldInfoByFormId(formId);

    if ( !CollectionUtils.isEmpty(fieldInfoByForm)){
      List<Long> fieldIdList = fieldInfoByForm.stream().map(Field::getFieldId).collect(Collectors.toList());
      fieldService.deleteByFieldIdList(fieldIdList);
      linkFieldService.deleteByFieldIdList(fieldIdList);
      linkFilterService.deleteByFieldIdList(fieldIdList);

      fieldExtendDataService.deleteByFieldIdList(fieldIdList);

      List<FieldExtendRule> fieldExtendRuleList = fieldExtendRuleService.selectByFieldIdList(fieldIdList);
      if ( !CollectionUtils.isEmpty(fieldExtendRuleList)){
        List<Long> ruleIdList = fieldExtendRuleList.stream().map(FieldExtendRule::getFieldExtendRuleId).collect(Collectors.toList());
        fieldExtendRuleService.deleteByExtendRuleIdList(ruleIdList);
        fieldExtendRuleDetailService.deleteByExtendRuleIdList(ruleIdList);
      }

    }
  }

  private Map<String, Long> buildFieldCodeMap(List<FieldAddParam> fieldAddParams, Long formId) {
    Map<String, Long> fieldIdMap = new HashMap<>();
    if ( !CollectionUtils.isEmpty(fieldAddParams)){
      for (FieldAddParam fieldAddParam : fieldAddParams) {
        if (fieldAddParam.getFieldId() == null ){
          // 更新字段
          fieldAddParam.setFormId(formId);
          fieldAddParam.setFieldId(IdWorker.getId());
        }

        fieldIdMap.put(fieldAddParam.getFieldCode(), fieldAddParam.getFieldId());
      }
    }

    return fieldIdMap;
  }

  private void buildTableDataInfo(List<FieldAddParam> fieldAddParams, List<Field> fieldList, List<LinkField> linkFieldList, List<LinkFilter> linkFilterList, List<FieldExtendData> fieldExtendDataList, List<FieldExtendRule> fieldExtendRuleList, List<FieldExtendRuleDetail> fieldExtendRuleDetailList, Map<String, Long> fieldIdMap, FieldAddParam fieldAddParam) {
    Field field = new Field();
    BeanUtils.copyProperties(fieldAddParam, field);
    field.setOrderNumber(fieldAddParams.indexOf(fieldAddParam));

    if (DATE_LINK.contains(fieldAddParam.getFieldType())) {
      //当前字段为正常文本下，判断默认值
      if (DefaultValueTypeEnum.JOINT_DATA.name().equals(fieldAddParam.getDefaultValueType())) {
        //如果是数据联动
        if ( !CollectionUtils.isEmpty(fieldAddParam.getLinkedFieldIds())){
          for (Long slaveFieldId : fieldAddParam.getLinkedFieldIds()) {
            LinkField linkField = new LinkField();
            linkField.setLinkFieldId(IdWorker.getId());
            linkField.setLinkType(LinkTypeEnum.JOINT_DATA.name());
            linkField.setMasterFieldId(field.getFieldId());
            linkField.setSlaveFieldId(slaveFieldId);
            linkFieldList.add(linkField);
          }
        }

        if ( !CollectionUtils.isEmpty(fieldAddParam.getLinkFilterAddParams())){
          for (LinkFilterAddParam linkFilterAddParam : fieldAddParam.getLinkFilterAddParams()) {
            LinkFilter linkFilter = new LinkFilter();
            linkFilter.setLinkFilterId(IdWorker.getId());
            linkFilter.setLinkType(LinkTypeEnum.JOINT_DATA.name());
            linkFilter.setMasterFieldId(field.getFieldId());
            Long currentFieldId = fieldIdMap.get(linkFilterAddParam.getCurrentFormFieldCode());
            linkFilter.setCurrentFormFieldId(currentFieldId);
            linkFilter.setLinkFormFieldId(linkFilterAddParam.getLinkFormFieldId());
            linkFilter.setMethod(linkFilterAddParam.getMethod());
            linkFilterList.add(linkFilter);
          }
        }

      }
    } else if (FieldTypeEnum.LINK_QUERY.name().equals(fieldAddParam.getFieldType())) {
      List<Long> linkedFieldIds = fieldAddParam.getLinkedFieldIds();

      if ( !CollectionUtils.isEmpty(linkedFieldIds)){
        //当前字段为关联查询字段
        for (Long slaveFieldId : linkedFieldIds) {
          LinkField linkField = new LinkField();
          linkField.setLinkFieldId(IdWorker.getId());
          linkField.setLinkType(LinkTypeEnum.LINK_QUERY.name());
          linkField.setMasterFieldId(field.getFieldId());
          linkField.setSlaveFieldId(slaveFieldId);
          linkFieldList.add(linkField);
        }
      }

      List<LinkFilterAddParam> linkFilterAddParams = fieldAddParam.getLinkFilterAddParams();
      if ( !CollectionUtils.isEmpty(linkFilterAddParams)){
        for (LinkFilterAddParam linkFilterAddParam : linkFilterAddParams) {
          LinkFilter linkFilter = new LinkFilter();
          linkFilter.setLinkFilterId(IdWorker.getId());
          linkFilter.setLinkType(LinkTypeEnum.LINK_QUERY.name());
          linkFilter.setMasterFieldId(field.getFieldId());
          Long currentFieldId = fieldIdMap.get(linkFilterAddParam.getCurrentFormFieldCode());
          linkFilter.setCurrentFormFieldId(currentFieldId);
          linkFilter.setLinkFormFieldId(linkFilterAddParam.getLinkFormFieldId());
          linkFilter.setMethod(linkFilterAddParam.getMethod());
          linkFilterList.add(linkFilter);
        }
      }

    } else if (FieldTypeEnum.SELECT.name().equals(fieldAddParam.getFieldType())) {
      //当前字段为下拉选择框,判断下拉选项默认值的类型
      if (DefaultValueTypeEnum.LINK_FORM.name().equals(fieldAddParam.getDefaultValueType())) {

      }
    }
    fieldList.add(field);

    // 设置扩展数据
    if (!CollectionUtils.isEmpty(fieldAddParam.getExtendDataParams())){
       // 扩展数据
      List<ExtendDataParam> extendDataParams = fieldAddParam.getExtendDataParams();
      Integer otherNum = 0;
      for (ExtendDataParam e : extendDataParams) {
        FieldExtendData extendData = new FieldExtendData();
        extendData.setFieldExtendDataId(IdWorker.getId());
        extendData.setFormId(field.getFormId());
        extendData.setFieldId(field.getFieldId());
        extendData.setType(e.getType());
        extendData.setSelected(e.getSelected());
        extendData.setName(e.getName());
        extendData.setValue(e.getValue());
        extendData.setDataType(e.getDataType());

        if (e.getType().equals(ExtendTypeEnum.OTHER)){
          ++otherNum;
          if ( otherNum > 1){
            throw new IllegalArgumentException("当前选项中存在多个 ‘其他’ 类型");
          }
        }

        fieldExtendDataList.add(extendData);
      }
    }

    // 设置扩展规则
    if ( !CollectionUtils.isEmpty(fieldAddParam.getExtendRuleParams())){
      List<ExtendRuleParam> extendRuleParams = fieldAddParam.getExtendRuleParams();
      for (ExtendRuleParam e : extendRuleParams) {
        FieldExtendRule rule = new FieldExtendRule();
        rule.setFormId(field.getFormId());
        rule.setFieldId(field.getFieldId());
        rule.setFieldExtendRuleId(IdWorker.getId());
        rule.setName(e.getName());
        rule.setType(e.getType());

        fieldExtendRuleList.add(rule);

        if ( !CollectionUtils.isEmpty(e.getDetailParams())){
          List<ExtendRuleDetailParam> detailParams = e.getDetailParams();
          detailParams.forEach(detail -> {
            FieldExtendRuleDetail detailBean = new FieldExtendRuleDetail();
            detailBean.setFieldExtendRuleDetailId(IdWorker.getId());
            detailBean.setFieldExtendRuleId(rule.getFieldExtendRuleId());
            detailBean.setRule(detail.getRule());
            detailBean.setValue(detail.getValue());

            fieldExtendRuleDetailList.add(detailBean);
          });
        }
      }
    }
  }


  private void checkAddParam(FormAddParam addParam) {
    Assert.isTrue(StringUtils.isNotEmpty(addParam.getFormName()), "表单名为空");
    Assert.notNull(StringUtils.isNotEmpty(addParam.getFormType()), "表单类型为空");
    Assert.notEmpty(addParam.getFieldAddParams(), "表单内容为空");
  }

  @Override
  public List<Form> getFormByPage(PageParam pageParam, FormQueryParam queryParam) {
    return formDao.getFormByPage(pageParam, queryParam);
  }

  @Override
  public FormDetailDto getFormDetailById(Long formId) {
    FormDetailDto formDetailDto = new FormDetailDto();
    Form form = formDao.getById(formId);
    BeanUtils.copyProperties(form, formDetailDto);
    List<FieldDetailDto> fieldDetailDtos = getFieldDetailDtos(formId);

    formDetailDto.setFieldDetailDtos(fieldDetailDtos);
    return formDetailDto;
  }

  private List<FieldDetailDto> getFieldDetailDtos(Long formId) {
    //查询表单所有字段
    List<FieldDetailDto> fieldDetailDtos = new ArrayList<>();
    List<Field> fieldList = fieldService.getFieldInfoByFormId(formId);
    for (Field field : fieldList) {
      FieldDetailDto fieldDetailDto = new FieldDetailDto();
      BeanUtils.copyProperties(field, fieldDetailDto);
      if (field.getLinkFormId() != null) {
        Form linkForm = formDao.getById(field.getLinkFormId());
        if (linkForm != null) {
          fieldDetailDto.setLinkFormName(linkForm.getFormName());
        }
      }
      if (field.getLinkFieldId() != null) {
        Field linkField = fieldService.getFieldInfoById(field.getLinkFieldId());
        if (linkField != null) {
          fieldDetailDto.setLinkFieldName(linkField.getFieldName());
        }
      }

      List<ExtendRuleDto> extendRules = new ArrayList<>();
      List<FieldExtendRule> fieldExtendRules = fieldExtendRuleService.selectByRecord(new FieldExtendRule().setFieldId(field.getFieldId()));
      if ( !CollectionUtils.isEmpty(fieldExtendRules)){

        List<Long> extendRuleIdList = fieldExtendRules.stream().map(FieldExtendRule::getFieldExtendRuleId).collect(Collectors.toList());
        Map<Long, List<FieldExtendRuleDetail>> ruleDetailMap = fieldExtendRuleDetailService.selectMapByRuleIdList(extendRuleIdList);

        for (FieldExtendRule fieldExtendRule : fieldExtendRules) {
          ExtendRuleDto extendRuleDto = BeanCastUtil.castBean(fieldExtendRule, ExtendRuleDto.class);
          List<FieldExtendRuleDetail> fieldExtendRuleDetails = ruleDetailMap.get(fieldExtendRule.getFieldExtendRuleId());
          if ( !CollectionUtils.isEmpty(fieldExtendRuleDetails)){
            extendRuleDto.setDetailDtos(BeanCastUtil.castList(fieldExtendRuleDetails, ExtendRuleDetailDto.class));
          }

          extendRules.add(extendRuleDto);
        }
      }

      if ( FieldTypeEnum.SUB_TABLE.name().equals(field.getFieldType()) ){
        // 子表单，查询子表单字段信息
        List<FieldDetailDto> subFieldDetilList = getFieldDetailDtos(field.getFieldId());
        fieldDetailDto.setSubFieldDtos(BeanCastUtil.castList(subFieldDetilList, SubFieldDto.class));
      }

      List<FieldExtendData> fieldExtendDataList = fieldExtendDataService.selectByRecord(new FieldExtendData().setFieldId(field.getFieldId()));

      List<LinkFieldDetailDto> linkFields = linkFieldService.getByFieldId(field.getFieldId());
      List<LinkFilterDetailDto> linkFilters = linkFilterService.getByFieldId(field.getFieldId());
      fieldDetailDto.setLinkFields(linkFields);
      fieldDetailDto.setLinkFilters(linkFilters);
      fieldDetailDto.setExtendDatas(BeanCastUtil.castList(fieldExtendDataList, ExtendDataDto.class));
      fieldDetailDto.setExtendRules(extendRules);
      fieldDetailDtos.add(fieldDetailDto);
    }
    return fieldDetailDtos;
  }

  @Override
  public Form getFormById(Long formId) {
    return formDao.getById(formId);
  }
}
