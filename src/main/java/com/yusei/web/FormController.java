package com.yusei.web;

import com.yusei.common.PageParam;
import com.yusei.model.dto.FormDetailDto;
import com.yusei.model.dto.FormInfoDto;
import com.yusei.model.entity.Form;
import com.yusei.model.param.FormAddParam;
import com.yusei.model.param.FormQueryParam;
import com.yusei.service.IFormService;
import com.yusei.utils.BeanCastUtil;
import com.yusei.utils.ResponseUtils;
import com.yusei.common.ReturnResult;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/form")
public class FormController {

  @Autowired
  private IFormService formService;

  @PostMapping("/addForm")
  public ReturnResult addForm(@RequestBody FormAddParam addParam) {
    String formId = formService.addForm(addParam);
    return ResponseUtils.ok(formId);
  }

  @GetMapping("/getFormByPage")
  public ReturnResult getFormByPage(PageParam pageParam, FormQueryParam queryParam) {
    List<Form> formList = formService.getFormByPage(pageParam, queryParam);
    List<FormInfoDto> formInfoDtos = BeanCastUtil.castList(formList, FormInfoDto.class);
    return ResponseUtils.ok(formInfoDtos);
  }

  @GetMapping("/getFormDetailById")
  public ReturnResult getFormDetailById(Long formId) {
    FormDetailDto detailDto = formService.getFormDetailById(formId);
    return ResponseUtils.ok(detailDto);
  }
}
