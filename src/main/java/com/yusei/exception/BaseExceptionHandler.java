package com.yusei.exception;

import com.yusei.utils.ResponseUtils;
import com.yusei.common.ReturnResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class BaseExceptionHandler {

  @ExceptionHandler(value = BaseException.class)
  public ReturnResult baseExceptionHandler(BaseException e) {
    log.error("{}", e);
    return ResponseUtils.error(e.getMessage());
  }

  @ExceptionHandler(value = IllegalArgumentException.class)
  public ReturnResult paramCheckExceptionHandler(IllegalArgumentException e) {
    log.error("参数校验异常： {}", e);
    return ResponseUtils.error(e.getMessage());
  }

  @ExceptionHandler(value = Exception.class)
  public ReturnResult exceptionHandler(Exception e) {
    log.error("{}", e);
    return ResponseUtils.error("系统错误");
  }
}
