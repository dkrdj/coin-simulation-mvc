package com.mvc.coinsimulation.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.mvc.coinsimulation.repository.mongo.BitcoinRepository;
import com.mvc.coinsimulation.repository.mongo.EthereumRepository;
import com.mvc.coinsimulation.service.TicketService;
import com.mvc.coinsimulation.util.UpbitRequestUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;

/**
 * 업비트 웹소켓 연결을 관리하는 핸들러입니다.
 * 실시간 시세 정보를 요청하고 받아옵니다.
 *
 * @Author 이상현
 * @Version 1.0.0
 */
@Slf4j
@Component
public class UpbitTradeHandler extends TextWebSocketHandler {
    private final BitcoinRepository bitcoinRepository;
    private final EthereumRepository ethereumRepository;
    private final TicketService ticketService;
    private final ObjectMapper snakeOM;
    private final String ERROR_DUPLICATION = "ID is duplicated";

    public UpbitTradeHandler(BitcoinRepository bitcoinRepository, EthereumRepository ethereumRepository, TicketService ticketService) {
        this.snakeOM = new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        this.bitcoinRepository = bitcoinRepository;
        this.ethereumRepository = ethereumRepository;
        this.ticketService = ticketService;
    }

    /**
     * 웹소켓 연결이 설정된 직후 실행되는 메서드입니다.
     * 실시간 시세 정보를 요청합니다.
     *
     * @param session 웹소켓 세션
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {
        session.sendMessage(new TextMessage(UpbitRequestUtil.makeBody("trade")));
    }

    /**
     * 웹소켓으로부터 메시지를 받았을 때 처리하는 메서드입니다.
     *
     * @param session 웹소켓 세션
     * @param message 받은 메시지
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {

    }

    /**
     * 웹소켓으로 전송할 요청 메시지를 생성합니다.
     * 티켓 데이터, 티커 데이터, 포맷 데이터를 생성하여 리스트에 추가하고,
     * 해당 리스트를 JSON 형식으로 변환하여 반환합니다.
     *
     * @return 웹소켓으로 전송할 요청 메시지(JSON 형식)
     */


}
