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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * 유저의 자산을 관리하는 서비스입니다.
 * 자산이 일정 이하로 떨어질 경우 자산을 초기화합니다.
 *
 * @Author 이상현
 * @Version 1.0.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AssetService {
    private final AssetRepository assetRepository;
    private final TicketService ticketService;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    /**
     * 특정 유저의 자산 상태를 조회합니다.
     *
     * @param userId 유저의 고유 식별자
     * @return 유저의 현재 자산 상황
     */
    public List<AssetResponse> getAsset(Long userId) {
        return assetRepository.findByUserId(userId)
                .stream()
                .map(asset -> AssetResponse.builder()
                        .code(asset.getCode())
                        .buyingPrice(asset.getAveragePrice())
                        .amount(asset.getAmount())
                        .currentPrice(ticketService.getCurrentPrice(asset.getCode()).multiply(asset.getAmount()))
                        .build())
                .collect(Collectors.toList());
    }

    public List<Asset> getAssets(List<Order> orders, Trade trade) {
        List<Long> userIds = orders.stream().map(order -> order.getUser().getId()).collect(Collectors.toList());
        return assetRepository.findByUserIdListAndCode(userIds, trade.getCode());
    }

    /**
     * 유저의 현재 자산이 일정 이하일 때 자산을 초기화하는 메서드입니다.
     *
     * @param userId 유저의 고유 식별자
     * @return 자산 초기화 후 유저 정보
     * @throws OrderExistsException 주문이 존재할 경우 예외 발생
     * @throws CashOverException    자산이 초기화 임계값보다 클 경우 예외 발생
     */
    @Transactional
    public UserResponse resetCash(Long userId) {
        // 유저의 총 자산을 계산하기 위한 변수
        AtomicReference<BigDecimal> totalAsset = new AtomicReference<>(BigDecimal.ZERO);

        // 현재 매수 또는 매도 주문이 존재하는지 확인하고, 있다면 예외를 발생시킵니다.
        if (!orderRepository.findByUserId(userId).isEmpty()) {
            throw new OrderExistsException();
        }

        // 유저의 자산 목록을 조회하고, 각 자산의 현재 가치를 합산하여 총 자산을 계산합니다.
        assetRepository.findByUserId(userId).forEach(asset -> totalAsset.updateAndGet(
                total -> total.add(ticketService.getCurrentPrice(asset.getCode()).multiply(asset.getAmount()))));

        // 유저의 현금 자산도 합산합니다.
        User user = userRepository.findById(userId).orElseThrow(NoUserException::new);
        totalAsset.updateAndGet(total -> total.add(user.getCash()));

        // 자산 초기화 임계값을 초과하는지 확인하고, 초과할 경우 예외를 발생시킵니다.
        if (totalAsset.get().compareTo(CoinConstant.ASSET_RESET_THRESHOLD.getValue()) > 0) {
            throw new CashOverException();
        }

        // 유저의 자산을 초기화하고 초기화된 유저 정보를 반환합니다.
        user.setCash(CoinConstant.INITIAL_ASSET_VALUE.getValue());

        return UserResponse.builder()
                .cash(user.getCash())
                .nickname(user.getNickname())
                .profile(user.getProfile())
                .build();
    }

    @Transactional
    public Asset updateAssetForBidOrder(Long userId, OrderRequest orderRequest) {
        Asset asset = assetRepository.findByUserIdAndCode(userId, orderRequest.getCode())
                .orElseThrow(NotEnoughCoinException::new);
        if (asset.getAmount().compareTo(orderRequest.getAmount()) >= 0) {
            asset.setAmount(asset.getAmount().subtract(orderRequest.getAmount()));
        } else {
            throw new NotEnoughCoinException();
        }
        return asset;
    }

    @Transactional
    public Asset updateAssetForBidOrderCancel(Order order) {
        Asset asset = assetRepository.findByUserIdAndCode(order.getUser().getId(), order.getCode())
                .orElse(Asset.builder()
                        .amount(BigDecimal.ZERO)
                        .averagePrice(BigDecimal.ZERO)
                        .code(order.getCode())
                        .userId(order.getUser().getId())
                        .build());
        asset.setAveragePrice(calculateAveragePrice(asset, order));
        asset.setAmount(asset.getAmount().add(order.getAmount()));
        return asset;
    }

    @Transactional
    public void updateAssetForExecution(Asset asset, Execution execution) {
        if (asset == null) {
            asset = Asset.builder()
                    .amount(BigDecimal.ZERO)
                    .averagePrice(BigDecimal.ZERO)
                    .code(execution.getCode())
                    .userId(execution.getUserId())
                    .build();
            asset = assetRepository.save(asset);
        }
        asset.setAveragePrice(calculateAveragePrice(asset, execution));
        asset.setAmount(asset.getAmount().add(execution.getAmount()));
    }

    private BigDecimal calculateAveragePrice(Asset asset, Execution execution) {
        return asset.getAmount().multiply(asset.getAveragePrice()).add(execution.getTotalPrice())
                .divide(asset.getAmount().add(execution.getAmount()), RoundingMode.HALF_UP);
    }

    private BigDecimal calculateAveragePrice(Asset asset, Order order) {
        return asset.getAveragePrice().multiply(asset.getAmount()).add(order.getPrePrice().multiply(order.getAmount()))
                .divide(asset.getAmount().add(order.getAmount()), RoundingMode.HALF_UP);
    }

}
