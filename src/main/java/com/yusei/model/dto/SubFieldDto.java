package com.yusei.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yusei.model.param.ExtendDataParam;
import com.yusei.model.param.ExtendRuleParam;
import com.yusei.model.param.LinkFilterAddParam;
import lombok.Data;

import java.util.List;

/**
 * @author liuqiang
 * @TOTD
 * @date 2020/9/2 18:03
 */
@Data
public class SubFieldDto {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long fieldId;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long formId;

    private String fieldCode;

    private String fieldName;

    private String fieldType;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long linkFormId;

    private String linkFormName;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long linkFieldId;

    private String linkFieldName;

    private String defaultValueType;

    private String defaultValue;

    private Boolean necessary;

    private Boolean allowRepeated;

    private Boolean showField;

    private Boolean editField;

    private List<LinkFieldDetailDto> linkFields;

    private List<LinkFilterDetailDto> linkFilters;

    private List<ExtendDataDto> extendDatas;

    private List<ExtendRuleDto> extendRules;


}
