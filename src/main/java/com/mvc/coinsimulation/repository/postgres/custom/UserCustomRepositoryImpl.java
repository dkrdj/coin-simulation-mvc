package com.mvc.coinsimulation.repository.postgres.custom;

import com.mvc.coinsimulation.entity.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.mvc.coinsimulation.entity.QUser.user;

@Repository
@RequiredArgsConstructor
public class UserCustomRepositoryImpl implements UserCustomRepository {

    private final JPAQueryFactory query;

    //    @Query("select users.* " +
//            "from users " +
//            "where users.id = :id " +
//            "for update")
    @Override
    public Optional<User> findByIdForUpdate(Long id) {
        return query.selectFrom(user)
                .where(user.id.eq(id))
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .stream().findAny();
    }

    @Override
    public List<User> findAllByIdForUpdate(List<Long> userIds) {
        return query.selectFrom(user)
                .where(user.id.in(userIds))
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .stream().toList();
    }

}
