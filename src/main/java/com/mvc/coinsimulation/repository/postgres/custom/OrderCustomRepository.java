package com.mvc.coinsimulation.repository.postgres.custom;

import com.mvc.coinsimulation.entity.Order;
import com.mvc.coinsimulation.enums.Gubun;

import java.util.List;
import java.util.Optional;

public interface OrderCustomRepository {
    Optional<Order> findByIdAndUserIdForUpdate(Long id, Long userId);

    List<Order> findBidOrders(Gubun gubun, String code, Double price);

    List<Order> findAskOrders(Gubun gubun, String code, Double price);

}
