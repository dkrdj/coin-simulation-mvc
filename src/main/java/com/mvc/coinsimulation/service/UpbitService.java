package com.mvc.coinsimulation.service;

import com.mvc.coinsimulation.handler.UpbitOrderBookHandler;
import com.mvc.coinsimulation.handler.UpbitTickerHandler;
import com.mvc.coinsimulation.handler.UpbitTradeHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
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

@Slf4j
@Service
public class UpbitService {
    private final UpbitTickerHandler upbitTickerHandler;
    private final UpbitTradeHandler upbitTradeHandler;
    private final UpbitOrderBookHandler upbitOrderBookHandler;
    private final WebSocketClient webSocketClient;
    private final String UPBIT_WEBSOCKET_URI;

    public UpbitService(UpbitTickerHandler upbitTickerHandler,
                        UpbitTradeHandler upbitTradeHandler,
                        UpbitOrderBookHandler upbitOrderBookHandler,
                        WebSocketClient webSocketClient,
                        @Value("${upbit.websocket.uri}") String upbitWebsocketUri) {
        this.upbitTickerHandler = upbitTickerHandler;
        this.upbitTradeHandler = upbitTradeHandler;
        this.upbitOrderBookHandler = upbitOrderBookHandler;
        this.webSocketClient = webSocketClient;
        UPBIT_WEBSOCKET_URI = upbitWebsocketUri;
    }

    /**
     * 웹소켓 클라이언트를 초기화하고 업비트 웹소켓에 연결합니다.
     * 애플리케이션이 준비되면 실행합니다.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void upbitConnect() {
        webSocketClient.execute(upbitOrderBookHandler, UPBIT_WEBSOCKET_URI);
        webSocketClient.execute(upbitTickerHandler, UPBIT_WEBSOCKET_URI);
        webSocketClient.execute(upbitTradeHandler, UPBIT_WEBSOCKET_URI);
    }
}

