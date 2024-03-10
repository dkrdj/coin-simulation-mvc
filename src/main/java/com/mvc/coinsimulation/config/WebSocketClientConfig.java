package com.mvc.coinsimulation.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

/**
 * WebSocket 클라이언트 및 ObjectMapper를 구성하는 클래스
 *
 * @Author 이상현
 * @Version 1.0.0
 * @See None
 */
@Configuration
public class WebSocketClientConfig {

    /**
     * 기본적인 WebSocket 클라이언트를 생성하여 반환하는 메서드
     *
     * @return WebSocketClient
     */
    @Bean
    public WebSocketClient webClient() {
        return new StandardWebSocketClient();
    }

    /**
     * Snake Case를 사용하는 ObjectMapper를 생성하여 반환하는 메서드
     *
     * @return ObjectMapper
     */
    @Bean
    public ObjectMapper snakeObjectMapper() {
        return new ObjectMapper()
                .registerModule(new JavaTimeModule()) // Java 8 시간 모듈 등록
                .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE); // Snake Case 전략 설정
    }
}