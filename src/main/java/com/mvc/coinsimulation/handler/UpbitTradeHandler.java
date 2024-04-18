package com.mvc.coinsimulation.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mvc.coinsimulation.dto.common.Trade;
import com.mvc.coinsimulation.enums.UpbitRequestType;
import com.mvc.coinsimulation.service.ExecutionService;
import com.mvc.coinsimulation.util.UpbitUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 업비트 웹소켓 연결을 관리하는 핸들러입니다.
 * 실시간 시세 정보를 요청하고 받아옵니다.
 *
 * @Author 이상현
 * @Version 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class UpbitTradeHandler extends BinaryWebSocketHandler {
    private final SimpMessageSendingOperations simpMessageSendingOperations;
    private final ObjectMapper snakeObjectMapper;
    private final ExecutionService executionService;


    /**
     * 웹소켓 연결이 설정된 직후 실행되는 메서드입니다.
     * 실시간 시세 정보를 요청합니다.
     *
     * @param session 웹소켓 세션
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {
        session.sendMessage(new TextMessage(UpbitUtils.makeBody(UpbitRequestType.TRADE)));
    }

    /**
     * 웹소켓으로부터 메시지를 받았을 때 처리하는 메서드입니다.
     *
     * @param session 웹소켓 세션
     * @param message 받은 메시지
     */

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
        String convertedMessage = new String(message.getPayload().array(), StandardCharsets.UTF_8);
        publish(convertedMessage);
        processTrade(convertedMessage);
    }


    /**
     * 받은 메시지를 토픽에 발행하는 비동기 메서드입니다.
     *
     * @param convertedMessage 처리된 메시지
     */
    @Async
    protected void publish(String convertedMessage) {
        simpMessageSendingOperations.convertAndSend("/sub/orderbook", convertedMessage);
    }

    @Async
    protected void processTrade(String convertedMessage) throws JsonProcessingException {
        Trade trade = snakeObjectMapper.readValue(convertedMessage, Trade.class);
        switch (trade.getAskBid()) {
            case ASK -> executionService.executeAsk(trade);
            case BID -> executionService.executeBid(trade);
        }
    }


}
