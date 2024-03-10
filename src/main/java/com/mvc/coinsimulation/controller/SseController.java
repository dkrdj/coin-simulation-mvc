package com.mvc.coinsimulation.controller;

import com.mvc.coinsimulation.sse.SseEmitters;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

/**
 * SSE (Server-Sent Events) 연결을 처리하는 컨트롤러 클래스
 *
 * @Author 이상현
 * @Version 1.0.0
 * @See None
 */
@RestController
@Slf4j
@RequiredArgsConstructor
public class SseController {
    private final SseEmitters sseEmitters;

    /**
     * 클라이언트와 SSE 연결을 설정하고 SseEmitter를 반환하는 메서드
     *
     * @param userId 세션에 저장된 사용자 ID
     * @return ResponseEntity<SseEmitter>
     */
    @GetMapping(value = "/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> connect(@SessionAttribute("user") Long userId) {
        SseEmitter emitter = new SseEmitter(30 * 60 * 1000L); // 30분
        try {
            sseEmitters.add(userId, emitter);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok(emitter);
    }
}
