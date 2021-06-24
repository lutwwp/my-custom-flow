package com.yusei.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.yusei.common.PageParam;
import com.yusei.common.PageResult;
import com.yusei.common.ReturnResult;
import com.yusei.model.dto.process.ProcessDetailInfoDto;
import com.yusei.model.dto.process.ProcessFormInfoDto;
import com.yusei.model.dto.process.ProcessInfoDto;
import com.yusei.model.dto.process.ProcessNodeFieldDetailDto;
import com.yusei.model.entity.ProcessInfo;
import com.yusei.model.param.process.ProcessInfoAddParam;
import com.yusei.model.param.process.ProcessInfoUpdateParam;
import com.yusei.service.IProcessInfoService;
import com.yusei.utils.BeanCastUtil;
import com.yusei.utils.ResponseUtils;
import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/processInfo")
public class ProcessInfoController {

  @Autowired
  private IProcessInfoService processInfoService;

  @PostMapping("/addProcess")
  public ReturnResult addProcess(@RequestBody ProcessInfoAddParam addParam)
      throws JsonProcessingException {
    ProcessInfoDto dto = processInfoService.addProcess(addParam);
    return ResponseUtils.ok(dto);
  }

  @PostMapping("/updateProcess")
  public ReturnResult updateProcess(@RequestBody ProcessInfoUpdateParam updateParam)
      throws JsonProcessingException {
    processInfoService.updateProcess(updateParam);
    return ResponseUtils.ok();
  }

  /**
   * 流程表单对应的流程版本信息的下拉查询.
   *
   * @param formId 流程表单id
   * @return 流程版本信息
   */
  @GetMapping("/getProcessInfoByFormId")
  public ReturnResult getProcessInfoByFormId(Long formId) {
    List<ProcessInfo> processInfos = processInfoService.getProcessInfoByFormId(formId);
    List<ProcessInfoDto> dtoList = BeanCastUtil.castList(processInfos, ProcessInfoDto.class);
    return ResponseUtils.ok(dtoList);
  }

  /**
   * 流程版本对应的详细流程信息.
   *
   * @param processInfoId 流程信息表id
   * @return 流程详细信息
   */
  @GetMapping("/getProcessDetailInfoById")
  public ReturnResult getProcessDetailInfoById(Long processInfoId) throws IOException {
    ProcessDetailInfoDto processDetailInfoDto = processInfoService
        .getProcessDetailInfoById(processInfoId);
    return ResponseUtils.ok(processDetailInfoDto);
  }

  @PostMapping("/deleteProcessInfoById")
  public ReturnResult deleteProcessInfoById(Long processInfoId) {
    processInfoService.deleteProcessInfoById(processInfoId);
    return ResponseUtils.ok();
  }

  @PostMapping("/deployProcessById")
  public ReturnResult deployProcessById(Long processInfoId) {
    processInfoService.deployProcessById(processInfoId);
    return ResponseUtils.ok();
  }

  @GetMapping("/getOtherActiveProcessByPage")
  public ReturnResult getOtherActiveProcessByPage(PageParam pageParam, Long currentFormId) {
    List<ProcessFormInfoDto> dtos = processInfoService
        .getOtherActiveProcessByPage(pageParam, currentFormId);
    return ResponseUtils.ok(dtos);
  }

  @GetMapping("/getActiveProcessByPage")
  public ReturnResult getActiveProcessByPage(PageParam pageParam, String processName) {
    List<ProcessFormInfoDto> dtos = processInfoService
        .getActiveProcessByPage(pageParam, processName);
    Long total = processInfoService.getActiveProcessCount(processName);
    PageResult<ProcessFormInfoDto> result = new PageResult<>(dtos, total);
    return ResponseUtils.ok(result);
  }

  @GetMapping("/getStartProcessFieldByFormId")
  public ReturnResult getStartProcessFieldByFormId(Long formId) {
    List<ProcessNodeFieldDetailDto> dtos = processInfoService.getStartProcessFieldByFormId(formId);
    return ResponseUtils.ok(dtos);
  }
}
