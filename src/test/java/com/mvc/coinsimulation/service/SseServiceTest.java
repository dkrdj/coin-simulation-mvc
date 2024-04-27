package com.mvc.coinsimulation.service;

import com.mvc.coinsimulation.entity.Execution;
import com.mvc.coinsimulation.enums.Gubun;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class SseServiceTest {

    private SseService sseService;
    private ConcurrentHashMap<Long, SseEmitter> emitters;

    @BeforeEach
    void setUp() {
        emitters = new ConcurrentHashMap<>();
        sseService = new SseService(emitters);
    }

    @Test
    @DisplayName("SSE 추가 테스트")
    void add() throws IOException {
        Long userId = 1L;

        SseEmitter emitter = sseService.add(userId);

        assertThat(emitter).isNotNull();
        assertThat(emitter).isEqualTo(emitters.get(userId));
    }

    @Test
    @DisplayName("체결 내역 SSE 전송 테스트")
    void sendExecution() throws IOException {
        Long userId = 1L;
        Execution execution = Execution.builder()
                .userId(userId)
                .gubun(Gubun.ASK)
                .code("BTC-KRW")
                .price(BigDecimal.valueOf(20000000))
                .amount(BigDecimal.valueOf(1.2342))
                .dateTime(LocalDateTime.now())
                .build();

        SseEmitter mockEmitter = mock(SseEmitter.class);
        SseEmitter mockEmitter2 = mock(SseEmitter.class);
        emitters.put(userId, mockEmitter);
        emitters.put(2L, mockEmitter2);
        sseService.sendExecution(execution);

        verify(mockEmitter, times(1)).send(execution.toSseResponse());
        verify(mockEmitter2, never()).send(execution.toSseResponse());

    }

}
