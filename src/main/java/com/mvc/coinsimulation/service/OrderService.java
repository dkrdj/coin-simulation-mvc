package com.mvc.coinsimulation.service;

import com.mvc.coinsimulation.dto.common.Trade;
import com.mvc.coinsimulation.dto.request.OrderRequest;
import com.mvc.coinsimulation.dto.response.OrderResponse;
import com.mvc.coinsimulation.entity.Asset;
import com.mvc.coinsimulation.entity.Order;
import com.mvc.coinsimulation.entity.User;
import com.mvc.coinsimulation.enums.Gubun;
import com.mvc.coinsimulation.exception.NoOrderException;
import com.mvc.coinsimulation.exception.NoUserException;
import com.mvc.coinsimulation.repository.postgres.OrderRepository;
import com.mvc.coinsimulation.repository.postgres.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final UserService userService;
    private final AssetService assetService;

    public List<OrderResponse> getOrders(Long userId) {
        return orderRepository.findByUserId(userId).stream().map(Order::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public OrderResponse buyOrder(Long userId, OrderRequest orderRequest) {
        userService.updateUserCash(userId, orderRequest);
        return insertOrder(userId, orderRequest, Gubun.ASK).toResponse();
    }

    @Transactional
    public OrderResponse sellOrder(Long userId, OrderRequest orderRequest) {
        Asset asset = assetService.updateAssetForBidOrder(userId, orderRequest);
        return insertOrder(userId, orderRequest, Gubun.BID, asset.getAveragePrice()).toResponse();
    }

    private Order insertOrder(Long userId, OrderRequest orderRequest, Gubun gubun, BigDecimal prePrice) {
        User user = userRepository.findById(userId).orElseThrow(NoUserException::new);
        return orderRepository.save(Order.builder()
                .amount(orderRequest.getAmount())
                .code(orderRequest.getCode())
                .dateTime(LocalDateTime.now())
                .gubun(gubun)
                .price(orderRequest.getPrice())
                .user(user)
                .prePrice(prePrice)
                .build());
    }

    private Order insertOrder(Long userId, OrderRequest orderRequest, Gubun gubun) {
        User user = userRepository.findById(userId).orElseThrow(NoUserException::new);
        return orderRepository.save(Order.builder()
                .amount(orderRequest.getAmount())
                .code(orderRequest.getCode())
                .dateTime(LocalDateTime.now())
                .gubun(gubun)
                .price(orderRequest.getPrice())
                .user(user)
                .build());
    }

    @Transactional
    public void cancelOrder(Long userId, Long orderId) {
        Order order = orderRepository.findByIdAndUserIdForUpdate(orderId, userId).orElseThrow(NoOrderException::new);
        switch (order.getGubun()) {
            case ASK -> userService.updateUserCash(order);
            case BID -> assetService.updateAssetForBidOrderCancel(order);
            default -> throw new NoOrderException();
        }
        orderRepository.deleteById(orderId);
    }

    @Transactional
    public BigDecimal updateOrder(Trade trade, Order order) {
        BigDecimal restAmount = order.getAmount();
        BigDecimal executeAmount = trade.getTradeVolume().min(restAmount);
        if (executeAmount.equals(restAmount)) {
            orderRepository.deleteById(order.getId());
        } else {
            order.setAmount(restAmount.subtract(executeAmount));
        }
        return executeAmount;
    }
}
