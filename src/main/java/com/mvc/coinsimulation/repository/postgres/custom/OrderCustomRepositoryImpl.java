package com.mvc.coinsimulation.repository.postgres.custom;

import com.mvc.coinsimulation.entity.Order;
import com.mvc.coinsimulation.enums.Gubun;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.mvc.coinsimulation.entity.QOrder.order;

@RequiredArgsConstructor
@Repository
public class OrderCustomRepositoryImpl implements OrderCustomRepository {

    private final JPAQueryFactory query;

    //    @Query("select orders.* " +
//            "from orders " +
//            "and orders.id = :id " +
//            "and orders.user_id = :userId " +
//            "for update"
//    )
    @Override
    public Optional<Order> findByIdAndUserIdForUpdate(Long id, Long userId) {
        return query.selectFrom(order)
                .where(order.id.eq(id))
                .where(order.userId.eq(userId))
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .stream().findFirst();
    }

    //    @Query("select orders.* " +
//            "from orders " +
//            "where orders.gubun = :gubun " +
//            "and orders.code = :code " +
//            "and orders.price >= :price " +
//            "for update"
//    )
    @Override
    public List<Order> findAskOrders(Gubun gubun, String code, Double price) {
        return query.selectFrom(order)
                .where(order.gubun.eq(gubun))
                .where(order.code.eq(code))
                .where(order.price.goe(price))
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .stream().toList();
    }

    //    @Query("select orders.* " +
//            "from orders " +
//            "where orders.gubun = :gubun " +
//            "and orders.code = :code " +
//            "and orders.price <= :price " +
//            "for update"
//    )z
    @Override
    public List<Order> findBidOrders(Gubun gubun, String code, Double price) {
        return query.selectFrom(order)
                .where(order.gubun.eq(gubun))
                .where(order.code.eq(code))
                .where(order.price.loe(price))
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .stream().toList();
    }
}
