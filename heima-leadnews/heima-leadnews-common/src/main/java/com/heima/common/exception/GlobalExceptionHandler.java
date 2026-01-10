package com.heima.common.exception;


import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestControllerAdvice  //控制器增强类
public class GlobalExceptionHandler {


    @ExceptionHandler(ResponseStatusException.class)
    public void handleResponseStatusException(ResponseStatusException e) {
        throw e; // 关键：直接往外抛
    }

    /**
     * 处理不可控异常
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseResult> handleException(Exception e){
        log.error("catch exception:{}",e.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseResult.errorResult(
                        AppHttpCodeEnum.SERVER_ERROR,
                        e.getMessage()
                ));
    }

    /**
     * 处理可控异常  自定义异常
     * @param e
     * @return
     */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ResponseResult> handleCustomException(CustomException e){
        log.error("catch exception:{}",e);
        ResponseResult result;
        if (StringUtils.isNotBlank(e.getErrorMsg())) {
            result = ResponseResult.errorResult(e.getAppHttpCodeEnum(), e.getErrorMsg());
        } else {
            result = ResponseResult.errorResult(e.getAppHttpCodeEnum());
        }

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(result);
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ResponseResult> handleThrowable(Throwable t) {
        log.error("fatal error", t);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseResult.errorResult(
                        AppHttpCodeEnum.SERVER_ERROR,
                        "system error"
                ));
    }


}
