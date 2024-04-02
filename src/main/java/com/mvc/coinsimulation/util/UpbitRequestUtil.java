package com.mvc.coinsimulation.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.mvc.coinsimulation.dto.common.FormatData;
import com.mvc.coinsimulation.dto.common.TickerData;
import com.mvc.coinsimulation.dto.common.TicketData;
import com.mvc.coinsimulation.enums.UpbitRequestType;
import lombok.experimental.UtilityClass;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@UtilityClass
public class UpbitRequestUtil {
    private final ObjectMapper camelOM = new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE);


    public String makeBody(UpbitRequestType type) throws JsonProcessingException {
        // 티켓 데이터 생성
        TicketData ticketData = createTicketData();
        // 티커 데이터 생성
        TickerData tickerData = createTickerData(type);
        // 포맷 데이터 생성
        FormatData formatData = createFormatData();

        // 티켓 데이터, 티커 데이터, 포맷 데이터를 리스트에 추가
        List<Object> dataList = Arrays.asList(ticketData, tickerData, formatData);

        // 리스트를 JSON 형식으로 변환하여 반환
        return convertToJson(dataList);
    }

    /**
     * 티켓 데이터를 생성하여 반환합니다.
     *
     * @return 생성된 티켓 데이터
     */
    private TicketData createTicketData() {
        return new TicketData("test example");
    }

    /**
     * 티커 데이터를 생성하여 반환합니다.
     *
     * @return 생성된 티커 데이터
     */
    private TickerData createTickerData(UpbitRequestType type) {
        TickerData tickerData = new TickerData();
        tickerData.setType(type.getValue());
        tickerData.setCodes(Collections.singletonList("KRW-BTC"));
        tickerData.setOnlySnapshot(false);
        tickerData.setOnlyRealtime(true);
        return tickerData;
    }

    /**
     * 포맷 데이터를 생성하여 반환합니다.
     *
     * @return 생성된 포맷 데이터
     */
    private FormatData createFormatData() {
        return new FormatData("DEFAULT");
    }

    /**
     * 주어진 리스트를 JSON 형식으로 변환하여 반환합니다.
     *
     * @param dataList 변환할 리스트
     * @return 변환된 JSON 형식의 문자열
     * @throws RuntimeException JSON 변환 중 발생한 예외
     */
    private String convertToJson(List<Object> dataList) throws JsonProcessingException {
        return camelOM.writeValueAsString(dataList);
    }
}
