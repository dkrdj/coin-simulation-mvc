package com.mvc.coinsimulation.service;

import com.mvc.coinsimulation.dto.common.Trade;
import com.mvc.coinsimulation.dto.response.ExecutionResponse;
import com.mvc.coinsimulation.entity.Asset;
import com.mvc.coinsimulation.entity.Execution;
import com.mvc.coinsimulation.entity.Order;
import com.mvc.coinsimulation.entity.User;
import com.mvc.coinsimulation.repository.postgres.ExecutionRepository;
import com.mvc.coinsimulation.repository.postgres.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ExecutionService {
    private final ExecutionRepository executionRepository;
    private final OrderService orderService;
    private final OrderRepository orderRepository;
    private final AssetService assetService;
    private final SseService sseService;
    private final UserService userService;

    public List<ExecutionResponse> getExecutions(Long userId) {
        return executionRepository.findTop10ByUserId(userId).stream()
                .map(Execution::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void executeAsk(Trade trade) {
        //부하테스트 후 비동기 메서드로 변경해서 다시 부하테스트 할 예정
        List<Order> orders = orderRepository.findBidOrders(trade.getAskBid(), trade.getCode(), trade.getTradePrice());
        List<User> users = userService.getUsers(orders);
        Map<Long, User> userMap = users.stream().collect(Collectors.toMap(User::getId, Function.identity()));
        for (Order order : orders) {
            Double executeAmount = orderService.updateOrder(trade, order);
            Execution execution = insert(trade, order, executeAmount);
            User user = userMap.get(order.getUserId());
            userService.updateUserCash(user, executeAmount);
            sseService.sendExecution(execution);
        }
    }

    @Transactional
    public void executeBid(Trade trade) {
        //부하테스트 후 비동기 메서드로 변경해서 다시 부하테스트 할 예정
        List<Order> orders = orderRepository.findAskOrders(trade.getAskBid(), trade.getCode(), trade.getTradePrice());
        List<Asset> assets = assetService.getAssets(orders, trade);
        Map<Long, Asset> assetMap = assets.stream().collect(Collectors.toMap(Asset::getUserId, Function.identity()));
        for (Order order : orders) {
            Double executeAmount = orderService.updateOrder(trade, order);
            Execution execution = insert(trade, order, executeAmount);
            Asset asset = assetMap.get(execution.getUserId());
            assetService.updateAssetForExecution(asset, execution);
            sseService.sendExecution(execution);
        }
    }

    private Execution insert(Trade trade, Order order, Double executeAmount) {
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
        return executionRepository.save(execution);
    }
}
