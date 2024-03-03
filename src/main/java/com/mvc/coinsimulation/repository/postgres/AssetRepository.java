package com.mvc.coinsimulation.repository.postgres;

import com.mvc.coinsimulation.entity.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {
    List<Asset> findByUserId(Long userId);

    //    @Query("select asset " +
//            "from asset " +
//            "where asset.user_id = :userId " +
//            "and asset.code = :code " +
//            "for update")
    Optional<Asset> findByUserIdAndCode(Long userId, String code);
}
