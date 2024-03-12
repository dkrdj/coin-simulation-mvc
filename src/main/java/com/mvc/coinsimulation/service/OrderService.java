package com.mvc.coinsimulation.service;

import com.mvc.coinsimulation.dto.common.Trade;
import com.mvc.coinsimulation.entity.Order;
import com.mvc.coinsimulation.repository.postgres.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final ExecutionService executionService;

    public List<Order> getOrders(Trade trade) {
        return orderRepository.findOrdersForAsk(trade.getAskBid(), trade.getCode(), trade.getTradePrice());
    }

    public Double updateOrder(Trade trade, Order order) {
        Double restAmount = order.getAmount();
        Double executeAmount = Math.min(trade.getTradeVolume(), restAmount);

        if (executeAmount.equals(restAmount)) {
            orderRepository.deleteById(order.getId());
        }
        return executeAmount;
    }
}
