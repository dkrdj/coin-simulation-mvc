package com.mvc.coinsimulation.repository.postgres;

import com.mvc.coinsimulation.entity.Asset;
import com.mvc.coinsimulation.repository.postgres.custom.AssetCustomRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssetRepository extends JpaRepository<Asset, Long>, AssetCustomRepository {
    List<Asset> findByUserId(Long userId);
}
