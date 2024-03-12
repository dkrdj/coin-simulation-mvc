package com.mvc.coinsimulation.repository.postgres.custom;

import com.mvc.coinsimulation.entity.Asset;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.mvc.coinsimulation.entity.QAsset.asset;

@Repository
@RequiredArgsConstructor
public class AssetCustomRepositoryImpl implements AssetCustomRepository {

    private final JPAQueryFactory query;

    @Override
    public List<Asset> findByUserIdAndCode(List<Long> userId, String code) {
        return query.selectFrom(asset)
                .where(asset.userId.in(userId))
                .where(asset.code.eq(code))
                //DB 비관적 락 걸기
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .stream().toList();
    }
}
