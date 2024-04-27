package com.mvc.coinsimulation.service;

import com.mvc.coinsimulation.dto.common.Trade;
import com.mvc.coinsimulation.dto.request.OrderRequest;
import com.mvc.coinsimulation.dto.response.AssetResponse;
import com.mvc.coinsimulation.dto.response.UserResponse;
import com.mvc.coinsimulation.entity.Asset;
import com.mvc.coinsimulation.entity.Execution;
import com.mvc.coinsimulation.entity.Order;
import com.mvc.coinsimulation.entity.User;
import com.mvc.coinsimulation.enums.CoinConstant;
import com.mvc.coinsimulation.exception.CashOverException;
import com.mvc.coinsimulation.exception.NoUserException;
import com.mvc.coinsimulation.exception.NotEnoughCoinException;
import com.mvc.coinsimulation.exception.OrderExistsException;
import com.mvc.coinsimulation.repository.postgres.AssetRepository;
import com.mvc.coinsimulation.repository.postgres.OrderRepository;
import com.mvc.coinsimulation.repository.postgres.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AssetServiceTest {
    @Mock
    private AssetRepository assetRepository;
    @Mock
    private TicketService ticketService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private OrderRepository orderRepository;
    @InjectMocks
    private AssetService assetService;


    @Test
    @DisplayName("자산 목록 조회 테스트")
    void getAsset() {
        String code = "code";
        //given
        Asset asset = Asset.builder()
                .code(code)
                .averagePrice(BigDecimal.valueOf(20392392))
                .amount(BigDecimal.valueOf(1.24))
                .build();
        when(assetRepository.findByUserId(1L)).thenReturn(List.of(asset));
        when(ticketService.getCurrentPrice(code)).thenReturn(BigDecimal.valueOf(10000));

        //when
        List<AssetResponse> assetResponses = assetService.getAsset(1L);
        AssetResponse assetResponse = assetResponses.get(0);

        //then
        assertEquals(1, assetResponses.size());
        assertEquals(code, assetResponse.getCode());
        assertEquals(BigDecimal.valueOf(20392392).compareTo(assetResponse.getBuyingPrice()), 0);
        assertEquals(BigDecimal.valueOf(1.24).compareTo(assetResponse.getAmount()), 0);
        assertEquals(BigDecimal.valueOf(12400).compareTo(assetResponse.getCurrentPrice()), 0);
    }

    @Test
    @DisplayName("자산 entity 목록 조회 테스트")
    void getAssets() {
        //given
        List<Order> orderList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Order order = Order.builder().user(User.builder().id((long) i).build()).build();
            orderList.add(order);
        }
        when(assetRepository.findByUserIdListAndCode(any(), any())).thenReturn(null);

        //when
        assetService.getAssets(orderList, new Trade());

        //then
        verify(assetRepository).findByUserIdListAndCode(argThat(
                userIds -> userIds.size() == 10 &&
                        userIds.get(0) == 0 &&
                        userIds.get(1) == 1 &&
                        userIds.get(2) == 2 &&
                        userIds.get(3) == 3 &&
                        userIds.get(4) == 4 &&
                        userIds.get(5) == 5 &&
                        userIds.get(6) == 6 &&
                        userIds.get(7) == 7 &&
                        userIds.get(8) == 8 &&
                        userIds.get(9) == 9
        ), any());

    }

    @Test
    @DisplayName("자산 초기화 테스트")
    void resetCash() {
        //given
        Long orderExistsUserId = 1L;
        Long assetOverUserId = 2L;
        Long cashOverUserId = 3L;
        Long normalUserId = 4L;
        Long deletedUserId = 5L;

        String code = "code";
        String nickname = "nickname";
        String profile = "profile";

        Asset smallAsset = Asset.builder().code(code).amount(BigDecimal.valueOf(0.1)).build();
        Asset bigAsset = Asset.builder().code(code).amount(BigDecimal.valueOf(100)).build();
        List<Asset> smallAssetList = List.of(smallAsset);
        List<Asset> bigAssetList = List.of(smallAsset, bigAsset);

        when(orderRepository.findByUserId(orderExistsUserId)).thenReturn(List.of(new Order()));
        when(orderRepository.findByUserId(assetOverUserId)).thenReturn(new ArrayList<>());
        when(orderRepository.findByUserId(cashOverUserId)).thenReturn(new ArrayList<>());
        when(orderRepository.findByUserId(normalUserId)).thenReturn(new ArrayList<>());
        when(orderRepository.findByUserId(deletedUserId)).thenReturn(new ArrayList<>());

        when(assetRepository.findByUserId(assetOverUserId)).thenReturn(bigAssetList);
        when(assetRepository.findByUserId(cashOverUserId)).thenReturn(smallAssetList);
        when(assetRepository.findByUserId(normalUserId)).thenReturn(smallAssetList);
        when(assetRepository.findByUserId(deletedUserId)).thenReturn(smallAssetList);

        when(ticketService.getCurrentPrice(code)).thenReturn(BigDecimal.valueOf(1000000));

        when(userRepository.findById(assetOverUserId)).thenReturn(
                Optional.of(User.builder()
                        .cash(BigDecimal.ZERO)
                        .build()));
        when(userRepository.findById(cashOverUserId)).thenReturn(
                Optional.of(User.builder()
                        .cash(BigDecimal.valueOf(100000000))
                        .build()));
        when(userRepository.findById(normalUserId)).thenReturn(
                Optional.of(User.builder()
                        .nickname(nickname)
                        .profile(profile)
                        .cash(BigDecimal.ZERO)
                        .build()));
        when(userRepository.findById(deletedUserId)).thenReturn(
                Optional.empty());

        //when
        UserResponse userResponse = assetService.resetCash(normalUserId);

        //then
        assertThrows(OrderExistsException.class, () -> assetService.resetCash(orderExistsUserId));
        assertThrows(CashOverException.class, () -> assetService.resetCash(assetOverUserId));
        assertThrows(CashOverException.class, () -> assetService.resetCash(cashOverUserId));
        assertThrows(NoUserException.class, () -> assetService.resetCash(deletedUserId));

        assertEquals(CoinConstant.INITIAL_ASSET_VALUE.getValue(), userResponse.getCash());
        assertEquals(nickname, userResponse.getNickname());
        assertEquals(profile, userResponse.getProfile());

    }

    @Test
    @DisplayName("매도 주문 시 자산 변경 테스트")
    void updateAssetForBidOrder() {
        //given
        Long noAssetUserId = 1L;
        Long notEnoughCoinUserId = 2L;
        Long normalUserId = 3L;
        String code = "code";
        BigDecimal amount = BigDecimal.valueOf(1.2);
        OrderRequest orderRequest = new OrderRequest(code, null, amount);

        when(assetRepository.findByUserIdAndCode(noAssetUserId, orderRequest.getCode()))
                .thenReturn(Optional.empty());
        when(assetRepository.findByUserIdAndCode(notEnoughCoinUserId, orderRequest.getCode()))
                .thenReturn(Optional.of(Asset.builder().amount(BigDecimal.valueOf(1.1)).build()));
        when(assetRepository.findByUserIdAndCode(normalUserId, orderRequest.getCode()))
                .thenReturn(Optional.of(Asset.builder().amount(BigDecimal.valueOf(1.3)).build()));

        //when
        Asset resultAsset = assetService.updateAssetForBidOrder(normalUserId, orderRequest);

        //then
        assertThrows(NotEnoughCoinException.class,
                () -> assetService.updateAssetForBidOrder(notEnoughCoinUserId, orderRequest));
        assertThrows(NotEnoughCoinException.class,
                () -> assetService.updateAssetForBidOrder(noAssetUserId, orderRequest));

        assertEquals(BigDecimal.valueOf(0.1), resultAsset.getAmount());
    }

    @Test
    @DisplayName("매도 주문 취소 시 자산 변경 테스트")
    void updateAssetForBidOrderCancel() {
        //given
        User noAssetUser = User.builder().id(1L).build();
        User normalUser = User.builder().id(2L).build();
        String code = "code";
        Order noAssetUserOrder = Order.builder()
                .user(noAssetUser)
                .prePrice(BigDecimal.valueOf(500))
                .amount(BigDecimal.valueOf(0.9))
                .code(code)
                .build();
        Order normalUserIdOrder = Order.builder()
                .user(normalUser)
                .prePrice(BigDecimal.valueOf(500))
                .amount(BigDecimal.valueOf(0.9))
                .code(code)
                .build();
        when(assetRepository.findByUserIdAndCode(noAssetUser.getId(), code))
                .thenReturn(Optional.empty());
        when(assetRepository.findByUserIdAndCode(normalUser.getId(), code))
                .thenReturn(Optional.of(Asset.builder()
                        .userId(normalUser.getId())
                        .code(code)
                        .amount(BigDecimal.valueOf(0.1))
                        .averagePrice(BigDecimal.valueOf(100))
                        .build()));

        //when
        Asset noAssetUserAsset = assetService.updateAssetForBidOrderCancel(noAssetUserOrder);
        Asset normalUserAsset = assetService.updateAssetForBidOrderCancel(normalUserIdOrder);

        //then
        assertEquals(code, noAssetUserAsset.getCode());
        assertEquals(BigDecimal.valueOf(500).compareTo(noAssetUserAsset.getAveragePrice()), 0);
        assertEquals(BigDecimal.valueOf(0.9).compareTo(noAssetUserAsset.getAmount()), 0);
        assertEquals(noAssetUser.getId(), noAssetUserAsset.getUserId());

        assertEquals(code, normalUserAsset.getCode());
        assertEquals(BigDecimal.valueOf(460).compareTo(normalUserAsset.getAveragePrice()), 0);
        assertEquals(BigDecimal.valueOf(1).compareTo(normalUserAsset.getAmount()), 0);
        assertEquals(normalUser.getId(), normalUserAsset.getUserId());

    }

    @Test
    @DisplayName("체결 시 자산 변경 테스트")
    void updateAssetForExecution() {
        //given
        Long normalUserId = 1L;
        Long noAssetUserId = 2L;
        Execution normalUserExecution = Execution.builder()
                .code("code")
                .userId(normalUserId)
                .amount(BigDecimal.valueOf(0.9))
                .totalPrice(BigDecimal.valueOf(900000))
                .build();
        Execution noAssetUserExecution = Execution.builder()
                .code("code")
                .userId(noAssetUserId)
                .amount(BigDecimal.valueOf(0.9))
                .totalPrice(BigDecimal.valueOf(900000))
                .build();
        when(assetRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Asset normalUserAsset = Asset.builder()
                .amount(BigDecimal.valueOf(0.1))
                .averagePrice(BigDecimal.valueOf(20000000))
                .code("code")
                .userId(normalUserId)
                .build();

        //when
        assetService.updateAssetForExecution(null, noAssetUserExecution);
        assetService.updateAssetForExecution(normalUserAsset, normalUserExecution);

        //then
        verify(assetRepository).save(argThat(
                asset -> asset.getAmount().equals(BigDecimal.valueOf(0.9)) &&
                        asset.getCode().equals("code") &&
                        asset.getAveragePrice().equals(BigDecimal.valueOf(1000000)) &&
                        Objects.equals(asset.getUserId(), noAssetUserId)
        ));
        assertEquals(BigDecimal.valueOf(2900000).compareTo(normalUserAsset.getAveragePrice()), 0);
        assertEquals(BigDecimal.valueOf(1).compareTo(normalUserAsset.getAmount()), 0);
    }

}