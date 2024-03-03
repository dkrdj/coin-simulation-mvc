package com.mvc.coinsimulation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 현재 코인들의 시세를 유저에게 전달함.
 * upbit로부터 데이터를 받아 bypass로 유저에게 전달하는 서비스
 * 코인 별 현재 가격을 저장하는 Map 자료구조 존재
 *
 * @Author 이상현
 * @Version 1.0.0
 * @See None
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TicketService {
    private final Map<String, Double> currentPrices = new ConcurrentHashMap<>();

    public Double getCurrentPrice(String code) {
        return currentPrices.get(code);
    }
}
