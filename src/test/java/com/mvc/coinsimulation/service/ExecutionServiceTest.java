package com.mvc.coinsimulation.service;

import com.mvc.coinsimulation.dto.response.ExecutionResponse;
import com.mvc.coinsimulation.entity.Execution;
import com.mvc.coinsimulation.enums.Gubun;
import com.mvc.coinsimulation.repository.postgres.ExecutionRepository;
import com.mvc.coinsimulation.repository.postgres.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExecutionServiceTest {
    @Mock
    private ExecutionRepository executionRepository;
    @Mock
    private OrderService orderService;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private AssetService assetService;
    @Mock
    private SseService sseService;
    @InjectMocks
    private ExecutionService executionService;

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("체결 내역 조회 테스트")
    void getExecutions() {
        //given
        List<Execution> executionList = new ArrayList<>();
        LocalDateTime localDateTime = LocalDateTime.now();
        for (long i = 0; i < 10; i++) {
            Execution execution = Execution.builder()
                    .id(i)
                    .userId(1L)
                    .gubun(Gubun.ASK)
                    .amount(0.1 * i)
                    .code("code")
                    .price(1.02 + i)
                    .totalPrice((0.1 * i) * (1.02 + i))
                    .dateTime(localDateTime)
                    .sequentialId(1294238 + i)
                    .build();
            executionList.add(execution);
        }
        when(executionRepository.findTop10ByUserId(anyLong())).thenReturn(executionList);

        //when
        List<ExecutionResponse> executionResponseList = executionService.getExecutions(1L);

        //then
        assertEquals(10, executionResponseList.size());
        for (int i = 0; i < 10; i++) {
            ExecutionResponse executionResponse = executionResponseList.get(i);
            assertEquals(i, executionResponse.getId());
            assertEquals(Gubun.ASK, executionResponse.getGubun());
            assertEquals(0.1 * i, executionResponse.getAmount());
            assertEquals("code", executionResponse.getCode());
            assertEquals(1.02 + i, executionResponse.getPrice());
            assertEquals((0.1 * i) * (1.02 + i), executionResponse.getTotalPrice());
            assertEquals(localDateTime, executionResponse.getDateTime());
        }


    }

    @Test
    void executeAsk() {
    }

    @Test
    void executeBid() {
    }

    @Test
    void insert() {
    }
}