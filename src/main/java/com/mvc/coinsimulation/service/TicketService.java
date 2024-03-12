package com.mvc.coinsimulation.service;

import com.mvc.coinsimulation.dto.common.TicketDto;
import com.mvc.coinsimulation.entity.Bitcoin;
import com.mvc.coinsimulation.entity.Ethereum;
import com.mvc.coinsimulation.repository.mongo.BitcoinRepository;
import com.mvc.coinsimulation.repository.mongo.EthereumRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * TicketService 클래스는 코인 시세 정보를 관리하고 업데이트하는 서비스입니다.
 * 주요 기능으로는 MongoDB에 코인 시세 로그를 저장하고 현재 코인의 가격을 관리합니다.
 * 서비스가 시작될 때, Bitcoin과 Ethereum 코인의 MongoDB 리포지토리를 초기화하고,
 * 해당 코인의 TicketDto를 이용하여 코인 엔티티를 생성하는 함수를 설정합니다.
 *
 * @Author 이상현
 * @Version 1.0.0
 */
@Service
public class TicketService {
    private final Map<String, Double> currentPrices = new ConcurrentHashMap<>();
    private final Map<String, MongoRepository<?, String>> mongoRepositories = new HashMap<>();
    private final Map<String, Function<TicketDto, ?>> coinFunctions = new HashMap<>();

    public TicketService(BitcoinRepository bitcoinRepository, EthereumRepository ethereumRepository) {
        setupRepository("KRW-BTC", bitcoinRepository, Bitcoin::fromTicket);
        setupRepository("KRW-ETC", ethereumRepository, Ethereum::fromTicket);
    }

    /**
     * MongoDB 리포지토리와 함수 설정을 수행하는 내부 메서드입니다.
     *
     * @param code       코인 코드
     * @param repository 해당 코인의 MongoDB 리포지토리
     * @param function   해당 코인의 TicketDto를 엔티티로 변환하는 함수
     * @param <T>        코인 엔티티의 타입
     */
    private <T> void setupRepository(String code, MongoRepository<T, String> repository, Function<TicketDto, T> function) {
        mongoRepositories.put(code, repository);
        coinFunctions.put(code, function);
    }

    /**
     * 주어진 코인 코드에 해당하는 현재 가격을 반환합니다.
     *
     * @param code 코인 코드
     * @return 현재 코인의 가격
     */
    public Double getCurrentPrice(String code) {
        return currentPrices.get(code);
    }

    /**
     * 주어진 TicketDto를 이용하여 코인 시세 로그를 저장하고, 현재 가격을 업데이트합니다.
     *
     * @param ticketDto 코인 시세 정보를 담은 DTO
     */
    public void process(TicketDto ticketDto) {
        setLog(ticketDto);
        setCurrentPrice(ticketDto.getCode(), ticketDto.getTradePrice());
    }

    /**
     * 주어진 TicketDto를 이용하여 코인 시세 로그를 저장합니다.
     *
     * @param ticketDto 코인 시세 정보를 담은 DTO
     * @param <T>       코인 엔티티의 타입
     */
    private <T> void setLog(TicketDto ticketDto) {
        String code = ticketDto.getCode();
        MongoRepository<T, String> repository = (MongoRepository<T, String>) mongoRepositories.get(code);
        Function<TicketDto, T> function = (Function<TicketDto, T>) coinFunctions.get(code);
        repository.insert(function.apply(ticketDto));
    }

    /**
     * 주어진 코인 코드와 가격을 이용하여 현재 가격을 업데이트합니다.
     *
     * @param code  코인 코드
     * @param price 현재 코인의 가격
     */
    private void setCurrentPrice(String code, Double price) {
        currentPrices.put(code, price);
    }
}
