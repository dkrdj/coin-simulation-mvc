package com.mvc.coinsimulation.repository.postgres.custom;

import com.mvc.coinsimulation.entity.Order;

import java.util.List;
import java.util.Optional;

public interface OrderCustomRepository {
    Optional<Order> findByIdAndUserIdForUpdate(Long id, Long userId);

    List<Order> findOrdersForAsk(String gubun, String code, Double price);

    List<Order> findOrdersForBid(String gubun, String code, Double price);

}
