package com.apimaster.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        // 禁用序列化空bean的异常
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        // 注册JavaTimeModule处理Java 8日期时间类型
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }
}
