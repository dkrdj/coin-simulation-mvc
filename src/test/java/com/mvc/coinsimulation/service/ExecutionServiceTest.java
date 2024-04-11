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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("체결 내역 조회 테스트")
    void getExecutions() {
        //given
        List<Execution> executionList = new ArrayList<>();
        LocalDateTime localDateTime = LocalDateTime.now();
        for (long i = 0; i < 10; i++) {
            Execution execution = Execution.builder()
                    .id(i)
                    .userId(1L)
                    .gubun(Gubun.ASK)
                    .amount(0.1 * i)
                    .code("code")
                    .price(1.02 + i)
                    .totalPrice((0.1 * i) * (1.02 + i))
                    .dateTime(localDateTime)
                    .sequentialId(1294238 + i)
                    .build();
            executionList.add(execution);
        }
        when(executionRepository.findTop10ByUserId(anyLong())).thenReturn(executionList);

        //when
        List<ExecutionResponse> executionResponseList = executionService.getExecutions(1L);

        //then
        assertEquals(10, executionResponseList.size());
        for (int i = 0; i < 10; i++) {
            ExecutionResponse executionResponse = executionResponseList.get(i);
            assertEquals(i, executionResponse.getId());
            assertEquals(Gubun.ASK, executionResponse.getGubun());
            assertEquals(0.1 * i, executionResponse.getAmount());
            assertEquals("code", executionResponse.getCode());
            assertEquals(1.02 + i, executionResponse.getPrice());
            assertEquals((0.1 * i) * (1.02 + i), executionResponse.getTotalPrice());
            assertEquals(localDateTime, executionResponse.getDateTime());
        }


    }

    @Test
    @DisplayName("매수 체결 시 매도 주문 체결 테스트")
    void executeAsk() {
        //given
        Trade trade = new Trade();
        trade.setTradeVolume(0.6d);
        trade.setSequentialId(34L);
        List<Order> orderList = new ArrayList<>();
        List<User> userList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Order order = Order.builder()
                    .amount(0.1 * i)
                    .price((double) 2000000 * (1 + i))
                    .userId((long) i)
                    .gubun(Gubun.BID)
                    .code("code")
                    .build();
            orderList.add(order);
            User user = User.builder()
                    .id((long) i)
                    .build();
            userList.add(user);

            when(orderService.updateOrder(trade, order))
                    .thenReturn(Math.min(trade.getTradeVolume(), Math.max(trade.getTradeVolume(), order.getAmount())));
        }
        when(orderRepository.findBidOrders(any(), any(), any())).thenReturn(orderList);
        when(userService.getUsers(any())).thenReturn(userList);
        when(executionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        //when
        executionService.executeAsk(trade);

        //then
        verify(orderService, times(orderList.size())).updateOrder(any(), any());
        verify(userService, times(orderList.size())).updateUserCash(any(), anyDouble());
        verify(sseService, times(orderList.size())).sendExecution(any());

    }

    @Test
    @DisplayName("매도 주문 체결 시 insert 메서드 테스트")
    void executeAsk_verify_insert() {
        //given
        Trade trade = new Trade();
        trade.setTradeVolume(0.6d);
        trade.setSequentialId(34L);
        Order order = Order.builder()
                .amount(0.1d)
                .price(2000000d)
                .userId(1L)
                .gubun(Gubun.BID)
                .code("code")
                .build();
        User user = User.builder()
                .id(1L)
                .build();
        List<Order> orderList = List.of(order);
        List<User> userList = List.of(user);

        Double executeAmount = 0.1d;

        when(orderService.updateOrder(trade, order)).thenReturn(executeAmount);
        when(orderRepository.findBidOrders(any(), any(), any())).thenReturn(orderList);
        when(userService.getUsers(any())).thenReturn(userList);
        when(executionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        //when
        executionService.executeAsk(trade);

        //then
        verify(executionRepository).save(argThat(execution ->
                execution.getPrice().equals(2000000d) &&
                        execution.getUserId().equals(1L) &&
                        execution.getGubun().equals(Gubun.BID) &&
                        execution.getCode().equals("code") &&
                        execution.getAmount().equals(executeAmount) &&
                        execution.getTotalPrice().equals(200000d) &&
                        execution.getDateTime() != null &&
                        execution.getSequentialId().equals(34L)
        ));
    }

    @Test
    @DisplayName("매도 체결 시 매수 주문 체결 테스트")
    void executeBid() {
        //given
        Trade trade = new Trade();
        trade.setTradeVolume(0.6d);
        trade.setSequentialId(34L);
        List<Order> orderList = new ArrayList<>();
        List<Asset> assetList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Order order = Order.builder()
                    .amount(0.1 * i)
                    .price((double) 2000000 * (1 + i))
                    .userId((long) i)
                    .gubun(Gubun.ASK)
                    .code("code")
                    .build();
            orderList.add(order);
            Asset asset = Asset.builder()
                    .userId((long) i)
                    .build();
            assetList.add(asset);

            when(orderService.updateOrder(trade, order))
                    .thenReturn(Math.min(trade.getTradeVolume(), Math.max(trade.getTradeVolume(), order.getAmount())));
        }
        when(orderRepository.findAskOrders(any(), any(), any())).thenReturn(orderList);
        when(assetService.getAssets(any(), any())).thenReturn(assetList);
        when(executionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        //when
        executionService.executeBid(trade);

        //then
        verify(orderService, times(orderList.size())).updateOrder(any(), any());
        verify(assetService, times(orderList.size())).updateAskAsset(any(), any());
        verify(sseService, times(orderList.size())).sendExecution(any());

    }

    @Test
    @DisplayName("매수 주문 체결 시 insert 메서드 테스트")
    void executeBid_verify_insert() {
        //given
        Trade trade = new Trade();
        trade.setTradeVolume(0.6d);
        trade.setSequentialId(34L);
        Order order = Order.builder()
                .amount(0.1d)
                .price(2000000d)
                .userId(1L)
                .gubun(Gubun.ASK)
                .code("code")
                .build();
        Asset asset = Asset.builder()
                .userId(1L)
                .build();
        List<Order> orderList = List.of(order);
        List<Asset> assetList = List.of(asset);

        Double executeAmount = 0.1d;
        when(orderService.updateOrder(trade, order)).thenReturn(executeAmount);
        when(orderRepository.findAskOrders(any(), any(), any())).thenReturn(orderList);
        when(assetService.getAssets(any(), any())).thenReturn(assetList);
        when(executionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        //when
        executionService.executeBid(trade);

        //then
        verify(executionRepository).save(argThat(execution ->
                execution.getPrice().equals(2000000d) &&
                        execution.getUserId().equals(1L) &&
                        execution.getGubun().equals(Gubun.ASK) &&
                        execution.getCode().equals("code") &&
                        execution.getAmount().equals(executeAmount) &&
                        execution.getTotalPrice().equals(200000d) &&
                        execution.getDateTime() != null &&
                        execution.getSequentialId().equals(34L)
        ));
    }

}