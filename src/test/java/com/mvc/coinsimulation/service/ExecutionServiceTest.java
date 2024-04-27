package com.mvc.coinsimulation.service;

import com.mvc.coinsimulation.dto.common.Trade;
import com.mvc.coinsimulation.dto.response.ExecutionResponse;
import com.mvc.coinsimulation.entity.Asset;
import com.mvc.coinsimulation.entity.Execution;
import com.mvc.coinsimulation.entity.Order;
import com.mvc.coinsimulation.entity.User;
import com.mvc.coinsimulation.enums.Gubun;
import com.mvc.coinsimulation.repository.postgres.ExecutionRepository;
import com.mvc.coinsimulation.repository.postgres.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExecutionServiceTest {
    @Mock
    private ExecutionRepository executionRepository;
    @Mock
    private UserService userService;
    @Mock
    private OrderService orderService;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private AssetService assetService;
    @Mock
    private SseService sseService;
    @InjectMocks
    private ExecutionService executionService;

    @Test
    @DisplayName("체결 내역 조회 테스트")
    void getExecutions() {
        //given
        List<Execution> executionList = new ArrayList<>();
        LocalDateTime localDateTime = LocalDateTime.now();
        for (int i = 0; i < 10; i++) {
            BigDecimal amount = BigDecimal.valueOf(0.1).multiply(BigDecimal.valueOf(i));
            BigDecimal price = BigDecimal.valueOf(1.02).add(BigDecimal.valueOf(i));
            Execution execution = Execution.builder()
                    .id((long) i)
                    .userId(1L)
                    .gubun(Gubun.ASK)
                    .amount(amount)
                    .code("code")
                    .price(price)
                    .totalPrice(amount.multiply(price))
                    .dateTime(localDateTime)
                    .sequentialId((long) (1294238 + i))
                    .build();
            executionList.add(execution);
        }
        when(executionRepository.findTop10ByUserId(anyLong())).thenReturn(executionList);

        //when
        List<ExecutionResponse> executionResponseList = executionService.getExecutions(1L);

        //then
        assertEquals(10, executionResponseList.size());
        for (int i = 0; i < 10; i++) {
            BigDecimal amount = BigDecimal.valueOf(0.1).multiply(BigDecimal.valueOf(i));
            BigDecimal price = BigDecimal.valueOf(1.02).add(BigDecimal.valueOf(i));
            ExecutionResponse executionResponse = executionResponseList.get(i);
            assertEquals(i, executionResponse.getId());
            assertEquals(Gubun.ASK, executionResponse.getGubun());
            assertEquals(amount, executionResponse.getAmount());
            assertEquals("code", executionResponse.getCode());
            assertEquals(price, executionResponse.getPrice());
            assertEquals(amount.multiply(price), executionResponse.getTotalPrice());
            assertEquals(localDateTime, executionResponse.getDateTime());
        }


    }

    @Test
    @DisplayName("매수 체결 시 매도 주문 체결 테스트")
    void executeAsk() {
        //given
        Trade trade = new Trade();
        trade.setTradeVolume(BigDecimal.valueOf(0.6));
        trade.setTradePrice(BigDecimal.valueOf(1000000));
        trade.setSequentialId(34L);
        List<Order> orderList = new ArrayList<>();
        List<User> userList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            BigDecimal amount = BigDecimal.valueOf(0.1).multiply(BigDecimal.valueOf(i));
            Order order = Order.builder()
                    .amount(amount)
                    .price(BigDecimal.valueOf(2000000 * (1 + i)))
                    .user(User.builder().id((long) i).build())
                    .gubun(Gubun.BID)
                    .code("code")
                    .build();
            orderList.add(order);
            User user = User.builder()
                    .id((long) i)
                    .build();
            userList.add(user);

            when(orderService.updateOrder(trade, order))
                    .thenReturn(trade.getTradeVolume().min(trade.getTradeVolume().max(order.getAmount())));
        }
        when(orderRepository.findBidOrders(any(), any())).thenReturn(orderList);

        //when
        executionService.executeAsk(trade);

        //then
        verify(orderService, times(orderList.size())).updateOrder(any(), any());
        verify(userService, times(orderList.size())).updateUserCash(any(), any(BigDecimal.class));
        verify(sseService, times(orderList.size())).sendExecution(any());

    }

    @Test
    @DisplayName("매도 주문 체결 시 insert 메서드 테스트")
    void executeAsk_verify_insert() {
        //given
        Trade trade = new Trade();
        trade.setTradeVolume(BigDecimal.valueOf(0.6));
        trade.setTradePrice(BigDecimal.valueOf(1000000));
        trade.setSequentialId(34L);
        Order order = Order.builder()
                .amount(BigDecimal.valueOf(0.1))
                .price(BigDecimal.valueOf(2000000))
                .user(User.builder().id(1L).build())
                .gubun(Gubun.BID)
                .code("code")
                .build();
        User user = User.builder()
                .id(1L)
                .build();
        List<Order> orderList = List.of(order);
        List<User> userList = List.of(user);

        BigDecimal executeAmount = BigDecimal.valueOf(0.1);

        when(orderService.updateOrder(trade, order)).thenReturn(executeAmount);
        when(orderRepository.findBidOrders(any(), any())).thenReturn(orderList);

        //when
        executionService.executeAsk(trade);

        //then
        verify(executionRepository).bulkInsert(argThat(execution ->
                execution.get(0).getPrice().compareTo(BigDecimal.valueOf(2000000)) == 0 &&
                        execution.get(0).getUserId().equals(1L) &&
                        execution.get(0).getGubun().equals(Gubun.BID) &&
                        execution.get(0).getCode().equals("code") &&
                        execution.get(0).getAmount().compareTo(executeAmount) == 0 &&
                        execution.get(0).getTotalPrice().compareTo(BigDecimal.valueOf(200000)) == 0 &&
                        execution.get(0).getDateTime() != null &&
                        execution.get(0).getSequentialId().equals(34L)
        ));
    }

    @Test
    @DisplayName("매도 체결 시 매수 주문 체결 테스트")
    void executeBid() {
        //given
        Trade trade = new Trade();
        trade.setTradeVolume(BigDecimal.valueOf(0.6));
        trade.setSequentialId(34L);
        List<Order> orderList = new ArrayList<>();
        List<Asset> assetList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            BigDecimal amount = BigDecimal.valueOf(0.1).multiply(BigDecimal.valueOf(i));
            Order order = Order.builder()
                    .amount(amount)
                    .price(BigDecimal.valueOf(2000000 * (1 + i)))
                    .user(User.builder().id((long) i).build())
                    .gubun(Gubun.ASK)
                    .code("code")
                    .build();
            orderList.add(order);
            Asset asset = Asset.builder()
                    .userId((long) i)
                    .build();
            assetList.add(asset);

            when(orderService.updateOrder(trade, order))
                    .thenReturn(trade.getTradeVolume().min(trade.getTradeVolume().max(order.getAmount())));
        }
        when(orderRepository.findAskOrders(any(), any())).thenReturn(orderList);
        when(assetService.getAssets(any(), any())).thenReturn(assetList);
        when(executionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        //when
        executionService.executeBid(trade);

        //then
        verify(orderService, times(orderList.size())).updateOrder(any(), any());
        verify(assetService, times(orderList.size())).updateAssetForExecution(any(), any());
        verify(sseService, times(orderList.size())).sendExecution(any());

    }

    @Test
    @DisplayName("매수 주문 체결 시 insert 메서드 테스트")
    void executeBid_verify_insert() {
        //given
        Trade trade = new Trade();
        trade.setTradeVolume(BigDecimal.valueOf(0.6));
        trade.setSequentialId(34L);
        Order order = Order.builder()
                .amount(BigDecimal.valueOf(0.1))
                .price(BigDecimal.valueOf(2000000))
                .user(User.builder().id(1L).build())
                .gubun(Gubun.ASK)
                .code("code")
                .build();
        Asset asset = Asset.builder()
                .userId(1L)
                .build();
        List<Order> orderList = List.of(order);
        List<Asset> assetList = List.of(asset);

        BigDecimal executeAmount = BigDecimal.valueOf(0.1);
        when(orderService.updateOrder(trade, order)).thenReturn(executeAmount);
        when(orderRepository.findAskOrders(any(), any())).thenReturn(orderList);
        when(assetService.getAssets(any(), any())).thenReturn(assetList);
        when(executionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        //when
        executionService.executeBid(trade);

        //then
        verify(executionRepository).save(argThat(execution ->
                execution.getPrice().compareTo(BigDecimal.valueOf(2000000)) == 0 &&
                        execution.getUserId().equals(1L) &&
                        execution.getGubun().equals(Gubun.ASK) &&
                        execution.getCode().equals("code") &&
                        execution.getAmount().compareTo(executeAmount) == 0 &&
                        execution.getTotalPrice().compareTo(BigDecimal.valueOf(200000)) == 0 &&
                        execution.getDateTime() != null &&
                        execution.getSequentialId().equals(34L)
        ));
    }

}