package com.yanxin.recruit.common.exception;

import com.yanxin.recruit.common.api.ApiResult;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BizException.class)
    public ApiResult<?> biz(BizException e) {
        return ApiResult.fail(e.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ApiResult<?> valid(Exception e) {
        return ApiResult.fail("参数校验失败: " + e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ApiResult<?> ex(Exception e) {
        Throwable root = e;
        while (root.getCause() != null && root.getCause() != root) {
            root = root.getCause();
        }
        String rootMsg = root.getMessage();
        if (rootMsg == null || rootMsg.isBlank()) {
            rootMsg = root.getClass().getSimpleName();
        } else {
            rootMsg = root.getClass().getSimpleName() + ": " + rootMsg;
        }
        return ApiResult.fail("系统异常: " + rootMsg);
    }
}
