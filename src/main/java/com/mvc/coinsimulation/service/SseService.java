package com.mvc.coinsimulation.service;

import com.mvc.coinsimulation.entity.Execution;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class SseService {
    //key = userId, value = emitter
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter add(Long userId) throws IOException {
        if (this.emitters.get(userId) != null) {
            return this.emitters.get(userId);
        }

        SseEmitter emitter = new SseEmitter(30 * 60 * 1000L); // 30분
        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> emitters.remove(userId));
        emitter.onError(throwable -> {
            emitters.remove(userId);
            throwable.printStackTrace();
        });
        emitter.send("SSE Connected");
        this.emitters.put(userId, emitter);
        return emitter;
    }
    @Async
    public void sendExecution(Execution execution) throws IOException {
        SseEmitter sseEmitter = emitters.get(execution.getUserId());
        if (sseEmitter != null) {
            sseEmitter.send(execution.toSseResponse());
        }
    }
}
