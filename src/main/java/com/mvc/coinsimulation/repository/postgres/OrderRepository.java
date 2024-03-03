package com.mvc.coinsimulation.repository.postgres;

import com.mvc.coinsimulation.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);

    //    @Query("select orders.* " +
//            "from orders " +
//            "and orders.id = :id " +
//            "and orders.user_id = :userId " +
//            "for update"
//    )
//    Optional<Order> findByIdAndUserIdForUpdate(Long id, Long userId);

    //    @Query("select orders.* " +
//            "from orders " +
//            "where orders.gubun = :gubun " +
//            "and orders.code = :code " +
//            "and orders.price >= :price " +
//            "for update"
//    )
//    List<Order> findOrdersForAsk(String gubun, String code, Double price);

    //    @Query("select orders.* " +
//            "from orders " +
//            "where orders.gubun = :gubun " +
//            "and orders.code = :code " +
//            "and orders.price <= :price " +
//            "for update"
//    )
//    List<Order> findOrdersForBid(String gubun, String code, Double price);

}
