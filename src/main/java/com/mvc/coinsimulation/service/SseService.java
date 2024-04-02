package com.mvc.coinsimulation.service;

import com.mvc.coinsimulation.entity.Execution;
import com.mvc.coinsimulation.exception.SseIOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;

@Service
@Slf4j
public class SseService {
    //key = userId, value = emitter
    private final Map<Long, SseEmitter> emitters;

    public SseService(Map<Long, SseEmitter> emitters) {
        this.emitters = emitters;
    }

    public SseEmitter add(Long userId) throws IOException {
        if (emitters.get(userId) != null) {
            return emitters.get(userId);
        }

        SseEmitter emitter = new SseEmitter(30 * 60 * 1000L); // 30분
        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> emitters.remove(userId));
        emitter.onError(throwable -> {
            emitters.remove(userId);
            throwable.printStackTrace();
        });
        emitter.send("SSE Connected");
        emitters.put(userId, emitter);
        return emitter;
    }

    @Async
    public void sendExecution(Execution execution) {
        SseEmitter sseEmitter = emitters.get(execution.getUserId());
        if (sseEmitter == null) {
            return;
        }
        try {
            sseEmitter.send(execution.toSseResponse());
        } catch (IOException e) {
            throw new SseIOException();
        }
    }
}
