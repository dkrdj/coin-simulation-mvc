package com.mvc.coinsimulation.controller;

import com.mvc.coinsimulation.dto.request.OrderRequest;
import com.mvc.coinsimulation.dto.response.OrderResponse;
import com.mvc.coinsimulation.enums.CoinConstant;
import com.mvc.coinsimulation.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {
    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getOrder(@SessionAttribute("user") Long userId) {
        return ResponseEntity.ok(orderService.getOrders(userId));
    }

    @PostMapping("buy")
    public ResponseEntity<?> buy(@SessionAttribute("user") Long userId, @RequestBody OrderRequest orderRequest) {
        if (orderRequest.getPrice() % CoinConstant.COIN_MIN_VALUE.getValue() != 0) {
            return ResponseEntity.badRequest().body("최소 금액 (" + CoinConstant.COIN_MIN_VALUE.getValue() + ") 단위로 주문 신청을 해야 합니다.");
        }
        return ResponseEntity.ok(orderService.buyOrder(userId, orderRequest));
    }

    @PostMapping("sell")
    public ResponseEntity<?> sell(@SessionAttribute("user") Long userId, @RequestBody OrderRequest orderRequest) {
        if (orderRequest.getPrice() % CoinConstant.COIN_MIN_VALUE.getValue() != 0) {
            return ResponseEntity.badRequest().body("최소 금액 (" + CoinConstant.COIN_MIN_VALUE.getValue() + ") 단위로 주문 신청을 해야 합니다.");
        }
        return ResponseEntity.ok(orderService.sellOrder(userId, orderRequest));
    }

    @DeleteMapping("{orderId}")
    public ResponseEntity<Void> cancel(@SessionAttribute("user") Long userId, @PathVariable Long orderId) {
        orderService.cancelOrder(userId, orderId);
        return ResponseEntity.ok().build();
    }
}

