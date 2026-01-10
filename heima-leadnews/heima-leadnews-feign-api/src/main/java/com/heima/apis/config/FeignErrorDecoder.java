package com.heima.apis.config;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.heima.common.exception.CustomException;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import feign.FeignException;
import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;


@Slf4j
@Component
public class FeignErrorDecoder implements ErrorDecoder {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Exception decode(String methodKey, Response response) {
        try {
            String content = Util.toString(
                    response.body().asReader(StandardCharsets.UTF_8)
            );

            ResponseResult<?> result =
                    JSON.parseObject(content, ResponseResult.class);

            return new CustomException(
                    AppHttpCodeEnum.codeOf(result.getCode()),
                    result.getErrorMessage()
            );
        } catch (Exception e) {
            log.error("Feign decode failed", e);
            return new CustomException(
                    AppHttpCodeEnum.SERVER_ERROR,
                    "remote service error"
            );
        }
    }


}
