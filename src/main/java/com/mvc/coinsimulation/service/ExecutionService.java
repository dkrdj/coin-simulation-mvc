package com.mvc.coinsimulation.service;

import com.mvc.coinsimulation.dto.common.Trade;
import com.mvc.coinsimulation.dto.response.ExecutionResponse;
import com.mvc.coinsimulation.entity.Asset;
import com.mvc.coinsimulation.entity.Execution;
import com.mvc.coinsimulation.entity.Order;
import com.mvc.coinsimulation.repository.postgres.ExecutionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ExecutionService {
    private final ExecutionRepository executionRepository;
    private final OrderService orderService;
    private final AssetService assetService;

    public List<ExecutionResponse> getExecutions(Long userId) {
        return executionRepository.findTop10ByUserId(userId).stream()
                .map(Execution::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void executeAsk(Trade trade) {
        List<Order> orders = orderService.getOrders(trade);
        List<Asset> assets = assetService.getAssets(orders, trade);
        for (Order order : orders) {
            Double executeAmount = orderService.updateOrder(trade, order);
            this.insert(trade, order, executeAmount);
        }
        //작성중인 코드

    }

    @Transactional
    public void insert(Trade trade, Order order, Double executeAmount) {
        executionRepository.save(Execution.builder()
                .price(order.getPrice())
                .userId(order.getUserId())
                .gubun(order.getGubun())
                .code(order.getCode())
                .amount(executeAmount)
                .totalPrice(order.getPrice() * executeAmount)
                .dateTime(LocalDateTime.now())
                .sequentialId(trade.getSequentialId())
                .build());
    }

    @Transactional
    public void executeBid(Trade trade) {

    }
}
