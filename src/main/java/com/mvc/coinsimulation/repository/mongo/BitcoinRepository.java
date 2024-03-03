package com.mvc.coinsimulation.repository.mongo;

import com.mvc.coinsimulation.entity.Bitcoin;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BitcoinRepository extends MongoRepository<Bitcoin, String> {
}
