package com.mvc.coinsimulation.sse;

import com.mvc.coinsimulation.entity.Execution;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class SseEmitters {
    //key = userId, value = emitter
    Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter add(Long userId, SseEmitter emitter) throws IOException {
        this.emitters.put(userId, emitter);
        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> emitters.remove(userId));
        emitter.onError(throwable -> {
            emitters.remove(userId);
            throwable.printStackTrace();
        });
        emitter.send("SSE Connected");
        return emitter;
    }

    public void sendExecution(Execution execution) throws IOException {
        SseEmitter sseEmitter = emitters.get(execution.getUserId());
        if (sseEmitter != null) {
            sseEmitter.send(execution);
        }
    }
}
