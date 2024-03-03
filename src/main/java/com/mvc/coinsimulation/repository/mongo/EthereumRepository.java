package com.mvc.coinsimulation.repository.mongo;

import com.mvc.coinsimulation.entity.Ethereum;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EthereumRepository extends MongoRepository<Ethereum, String> {
}
