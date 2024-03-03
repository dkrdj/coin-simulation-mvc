package com.mvc.coinsimulation.upbit;

import com.mvc.coinsimulation.handler.UpbitOrderBookHandler;
import com.mvc.coinsimulation.handler.UpbitTickerHandler;
import com.mvc.coinsimulation.handler.UpbitTradeHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.client.WebSocketClient;

/**
 * 웹소켓 클라이언트를 초기화하고 업비트 웹소켓에 연결하는 서비스입니다.
 * 애플리케이션 컨텍스트가 초기화된 후에 실행됩니다.
 *
 * @Author 이상현
 * @Version 1.0.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class WebClientInitializer {
    private final WebSocketClient client;
    private final UpbitTickerHandler upbitTickerHandler;
    private final UpbitTradeHandler upbitTradeHandler;
    private final UpbitOrderBookHandler upbitOrderBookHandler;
    @Value("upbit.websocket.uri")
    private String UPBIT_WEBSOCKET_URI;

    /**
     * 웹소켓 클라이언트를 초기화하고 업비트 웹소켓에 연결합니다.
     * 애플리케이션 컨텍스트가 초기화된 후에 실행됩니다.
     */
    @EventListener(ContextRefreshedEvent.class)
    public void upbitConnect() {
        client.execute(upbitTickerHandler, UPBIT_WEBSOCKET_URI);
        client.execute(upbitTradeHandler, UPBIT_WEBSOCKET_URI);
        client.execute(upbitOrderBookHandler, UPBIT_WEBSOCKET_URI);
    }
}
