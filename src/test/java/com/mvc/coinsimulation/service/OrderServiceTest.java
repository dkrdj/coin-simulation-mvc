package com.mvc.coinsimulation.service;

import com.mvc.coinsimulation.dto.common.Trade;
import com.mvc.coinsimulation.dto.request.OrderRequest;
import com.mvc.coinsimulation.dto.response.OrderResponse;
import com.mvc.coinsimulation.entity.Order;
import com.mvc.coinsimulation.enums.Gubun;
import com.mvc.coinsimulation.exception.NoOrderException;
import com.mvc.coinsimulation.repository.postgres.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private UserService userService;
    @Mock
    private AssetService assetService;
    @InjectMocks
    private OrderService orderService;

    @Test
    @DisplayName("주문 정보 조회 테스트")
    void getOrders() {
        //given
        List<Order> orderList = new ArrayList<>();
        LocalDateTime dateTime = LocalDateTime.now();
        String code = "code";
        for (long i = 0; i < 10; i++) {
            Order order = Order.builder()
                    .amount(i / 10.0)
                    .code(code)
                    .dateTime(dateTime)
                    .gubun(i % 2 == 0 ? Gubun.ASK : Gubun.BID)
                    .price(2000000d)
                    .id(i)
                    .build();
            orderList.add(order);
        }
        when(orderRepository.findByUserId(1L)).thenReturn(orderList);

        //when
        List<OrderResponse> orderResponseList = orderService.getOrders(1L);

        //then
        assertEquals(10, orderResponseList.size());
        for (long i = 0; i < 10; i++) {
            OrderResponse orderResponse = orderResponseList.get((int) i);

            assertEquals(i, orderResponse.getId());
            assertEquals(code, orderResponse.getCode());
            if (i % 2 == 0) {
                assertEquals(Gubun.ASK, orderResponse.getGubun());
            } else {
                assertEquals(Gubun.BID, orderResponse.getGubun());
            }
            assertEquals(2000000d, orderResponse.getPrice());
            assertEquals(i / 10.0, orderResponse.getAmount());
            assertEquals(dateTime, orderResponse.getDateTime());
        }
    }

    @Test
    @DisplayName("매수 주문 테스트")
    void buyOrder() {
        //given
        doNothing().when(userService).updateUserCash(anyLong(), any(OrderRequest.class));
        String code = "code";
        Double price = 200000d;
        Double amount = 1.2d;
        Long userId = 1L;
        OrderRequest orderRequest = new OrderRequest(code, price, amount);
        LocalDateTime dateTime = LocalDateTime.now();
        when(orderRepository.save(any(Order.class))).thenReturn(
                Order.builder()
                        .id(1L)
                        .userId(userId)
                        .code(code)
                        .gubun(Gubun.ASK)
                        .price(price)
                        .amount(amount)
                        .dateTime(dateTime)
                        .build()
        );

        //when
        OrderResponse orderResponse = orderService.buyOrder(userId, orderRequest);

        //then
        assertEquals(1L, orderResponse.getId());
        assertEquals(code, orderResponse.getCode());
        assertEquals(Gubun.ASK, orderResponse.getGubun());
        assertEquals(price, orderResponse.getPrice());
        assertEquals(amount, orderResponse.getAmount());
        assertEquals(dateTime, orderResponse.getDateTime());

    }

    @Test
    @DisplayName("매도 주문 테스트")
    void sellOrder() {
        //given
        doNothing().when(userService).updateUserCash(anyLong(), any(OrderRequest.class));
        String code = "code";
        Double price = 200000d;
        Double amount = 1.2d;
        Long userId = 1L;
        OrderRequest orderRequest = new OrderRequest(code, price, amount);
        LocalDateTime dateTime = LocalDateTime.now();
        when(orderRepository.save(any(Order.class))).thenReturn(
                Order.builder()
                        .id(1L)
                        .userId(userId)
                        .code(code)
                        .gubun(Gubun.BID)
                        .price(price)
                        .amount(amount)
                        .dateTime(dateTime)
                        .build()
        );

        //when
        OrderResponse orderResponse = orderService.buyOrder(userId, orderRequest);

        //then
        assertEquals(1L, orderResponse.getId());
        assertEquals(code, orderResponse.getCode());
        assertEquals(Gubun.BID, orderResponse.getGubun());
        assertEquals(price, orderResponse.getPrice());
        assertEquals(amount, orderResponse.getAmount());
        assertEquals(dateTime, orderResponse.getDateTime());
    }

    @Test
    @DisplayName("주문 취소 테스트")
    void cancelOrder() {
        //given
        Long userId = 1L;
        doNothing().when(userService).updateUserCash(any(Order.class));
        when(assetService.updateAssetForBidOrderCancel(any(Order.class))).thenReturn(null);
        Order askOrder = Order.builder()
                .id(1L)
                .userId(userId)
                .gubun(Gubun.ASK)
                .build();
        Order bidOrder = Order.builder()
                .id(2L)
                .userId(userId)
                .gubun(Gubun.BID)
                .build();
        when(orderRepository.findByIdAndUserIdForUpdate(1L, userId)).thenReturn(Optional.of(askOrder));
        when(orderRepository.findByIdAndUserIdForUpdate(2L, userId)).thenReturn(Optional.of(bidOrder));
        when(orderRepository.findByIdAndUserIdForUpdate(3L, userId)).thenReturn(Optional.empty());

        //when
        orderService.cancelOrder(userId, 2L);
        orderService.cancelOrder(userId, 1L);

        //then
        verify(userService, times(1)).updateUserCash(askOrder);
        verify(userService, times(0)).updateUserCash(bidOrder);
        verify(assetService, times(0)).updateAssetForBidOrderCancel(askOrder);
        verify(assetService, times(1)).updateAssetForBidOrderCancel(bidOrder);

        assertThrows(NoOrderException.class, () -> orderService.cancelOrder(1L, 3L));
    }

    @Test
    @DisplayName("주문 체결로 인한 order 수정 테스트")
    void updateOrder() {
        long orderLessId = 1L;
        long orderExactId = 2L;
        long orderMoreId = 3L;
        Double lessVolume = 1.242d;
        Double tradeVolume = 1.342d;
        Double moreVolume = 1.442d;
        Trade trade = new Trade();
        trade.setTradeVolume(tradeVolume);
        Order orderLessVolume = Order.builder()
                .id(orderLessId)
                .amount(lessVolume)
                .build();
        Order orderExactVolume = Order.builder()
                .id(orderExactId)
                .amount(tradeVolume)
                .build();
        Order orderMoreVolume = Order.builder()
                .id(orderMoreId)
                .amount(moreVolume)
                .build();
        doNothing().when(orderRepository).deleteById(anyLong());

        //when
        Double executeLessAmount = orderService.updateOrder(trade, orderLessVolume);
        Double executeExactAmount = orderService.updateOrder(trade, orderExactVolume);
        Double executeMoreAmount = orderService.updateOrder(trade, orderMoreVolume);

        //then

        verify(orderRepository, never()).deleteById(orderMoreId);
        verify(orderRepository, times(1)).deleteById(orderExactId);
        verify(orderRepository, times(1)).deleteById(orderLessId);

        assertEquals(lessVolume, executeLessAmount);
        assertEquals(tradeVolume, executeExactAmount);
        assertEquals(tradeVolume, executeMoreAmount);


    }
}