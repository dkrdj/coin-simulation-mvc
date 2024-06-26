package com.mvc.coinsimulation.repository.postgres;

import com.mvc.coinsimulation.entity.Order;
import com.mvc.coinsimulation.repository.postgres.custom.OrderCustomRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long>, OrderCustomRepository {
    List<Order> findByUserId(Long userId);

}
