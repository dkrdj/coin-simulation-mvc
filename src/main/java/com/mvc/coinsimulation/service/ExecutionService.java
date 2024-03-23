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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ExecutionService {
    private final ExecutionRepository executionRepository;
    private final OrderService orderService;
    private final AssetService assetService;
    private final SseService sseService;

    public List<ExecutionResponse> getExecutions(Long userId) {
        return executionRepository.findTop10ByUserId(userId).stream()
                .map(Execution::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void executeAsk(Trade trade) {
        List<Order> orders = orderService.getOrders(trade);
        List<Asset> assets = assetService.getAssets(orders, trade);
        Map<Long, Asset> assetMap = new HashMap<>();
        for (Asset asset : assets) {
            assetMap.put(asset.getUserId(), asset);
        }
        for (Order order : orders) {
            Double executeAmount = orderService.updateOrder(trade, order);
            Execution execution = this.insert(trade, order, executeAmount);
            sseService.sendExecution(execution);
        }
        //작성중인 코드 asset 어카냐...

    }

    @Transactional
    public Execution insert(Trade trade, Order order, Double executeAmount) {
        Execution execution = Execution.builder()
                .price(order.getPrice())
                .userId(order.getUserId())
                .gubun(order.getGubun())
                .code(order.getCode())
                .amount(executeAmount)
                .totalPrice(order.getPrice() * executeAmount)
                .dateTime(LocalDateTime.now())
                .sequentialId(trade.getSequentialId())
                .build();
        executionRepository.save(execution);
        return execution;
    }

    @Transactional
    public void executeBid(Trade trade) {

    }
}
