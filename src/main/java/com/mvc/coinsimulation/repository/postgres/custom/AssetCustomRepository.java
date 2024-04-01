package com.mvc.coinsimulation.repository.postgres.custom;

import com.mvc.coinsimulation.entity.Asset;

import java.util.List;
import java.util.Optional;

public interface AssetCustomRepository {
    List<Asset> findByUserIdListAndCode(List<Long> userId, String code);

    Optional<Asset> findByUserIdAndCode(Long userId, String code);
}
