package com.mvc.coinsimulation.repository.postgres.custom;

import com.mvc.coinsimulation.entity.Asset;

import java.util.List;

public interface AssetCustomRepository {
    List<Asset> findByUserIdAndCode(List<Long> userId, String code);
}
