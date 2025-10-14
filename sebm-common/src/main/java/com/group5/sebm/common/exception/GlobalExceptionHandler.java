package com.group5.sebm.common.exception;


import com.group5.sebm.common.common.BaseResponse;
import com.group5.sebm.common.common.ResultUtils;
import io.swagger.v3.oas.annotations.Hidden;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Hidden
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public BaseResponse<?> businessExceptionHandler(BusinessException e) {
        log.error("BusinessException", e);
        // 对于普通请求，返回标准 JSON 响应
        return ResultUtils.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理 @Valid 或 @Validated 校验失败异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public BaseResponse<?> handleValidationException(MethodArgumentNotValidException ex) {
        // 收集所有字段错误信息
        String errorMsg = ex.getBindingResult().getFieldErrors()
            .stream()
            .map(err -> err.getField() + ": " + err.getDefaultMessage())
            .collect(Collectors.joining("; "));

        return ResultUtils.error(ErrorCode.PARAMS_ERROR, errorMsg);
    }

}