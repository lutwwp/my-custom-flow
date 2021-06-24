package com.yusei.web;

import com.yusei.common.ReturnResult;
import com.yusei.enums.FieldTypeEnum;
import com.yusei.model.dto.FieldBriefDto;
import com.yusei.model.entity.Field;
import com.yusei.model.param.SubFieldAddParam;
import com.yusei.service.IFieldService;
import com.yusei.utils.BeanCastUtil;
import com.yusei.utils.ResponseUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/field")
public class FieldController {

  @Autowired
  private IFieldService fieldService;

  @GetMapping("/getFieldInfoByFormId")
  public ReturnResult getFieldInfoByFormId(Long formId) {
    List<Field> fieldList = fieldService.getFieldInfoByFormId(formId);

    List<Long> fieldIdList = fieldList.stream().filter(e -> FieldTypeEnum.SUB_TABLE.name().equals(e.getFieldType())).map(Field::getFieldId).collect(Collectors.toList());
    List<FieldBriefDto> fieldBriefDtos = BeanCastUtil.castList(fieldList, FieldBriefDto.class);

    if (!CollectionUtils.isEmpty(fieldIdList)){
      List<Field> fieldByFormIdList = fieldService.getFieldByFormIdList(fieldIdList);

      Map<Long, List<Field>> map = new HashMap<>();
      fieldByFormIdList.forEach(e -> {
        List<Field> fields = map.get(e.getFormId());
        if (CollectionUtils.isEmpty(fields)){
          fields = new ArrayList<>();
          map.put(e.getFormId(), fields);
        }

        fields.add(e);
      });

      fieldBriefDtos.forEach(e -> {
        List<Field> fields = map.get(e.getFieldId());
        if ( !CollectionUtils.isEmpty(fields)){
          e.setSubFieldAddParam(BeanCastUtil.castList(fields, SubFieldAddParam.class));
        }
      });

    }
    return ResponseUtils.ok(fieldBriefDtos);
  }
}
