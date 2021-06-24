package com.yusei.model.param;

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
public class SubFieldAddParam {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long fieldId;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long formId;

    private String fieldCode;

    private String fieldName;

    private String fieldType;

    private Long linkFormId;

    private Long linkFieldId;

    private String type;

    private String defaultValueType;

    private String defaultValue;

    private Boolean necessary;

    private Boolean allowRepeated;

    private Boolean showField;

    private Boolean editField;

    private List<Long> linkedFieldIds;

    private List<LinkFilterAddParam> linkFilterAddParams;

    private List<ExtendDataParam> extendDataParams;

    private List<ExtendRuleParam> extendRuleParams;

}
