package com.mvc.coinsimulation.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class SseConfig {

    @Bean
    public ConcurrentHashMap<Long, SseEmitter> emitters() {
        return new ConcurrentHashMap<>();
    }
}
