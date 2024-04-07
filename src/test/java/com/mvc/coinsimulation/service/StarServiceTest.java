package com.mvc.coinsimulation.service;

import com.mvc.coinsimulation.dto.response.StarResponse;
import com.mvc.coinsimulation.entity.Star;
import com.mvc.coinsimulation.repository.postgres.StarRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StarServiceTest {
    @Mock
    private StarRepository starRepository;

    @InjectMocks
    private StarService starService;

    @Test
    @DisplayName("즐겨찾기 불러오기 테스트")
    void getStars() {
        //given
        Long starUser = 1L;
        Long noStarUser = 2L;
        List<Star> starList = new ArrayList<>();
        for (long i = 0; i < 10; i++) {
            Star star = Star.builder()
                    .code("code" + i)
                    .id(i)
                    .userId(starUser)
                    .build();
            starList.add(star);
        }
        List<Star> noStarList = new ArrayList<>();
        when(starRepository.findByUserId(starUser)).thenReturn(starList);
        when(starRepository.findByUserId(noStarUser)).thenReturn(noStarList);

        //when
        List<StarResponse> starResponseList = starService.getStars(starUser);
        List<StarResponse> noStarResponseList = starService.getStars(noStarUser);

        //then
        assertEquals(10, starResponseList.size());
        for (int i = 0; i < 10; i++) {
            StarResponse starResponse = starResponseList.get(i);
            assertEquals("code" + i, starResponse.getCode());
            assertEquals(starUser, starResponse.getUserId());
        }

        assertTrue(noStarResponseList.isEmpty());

    }

    @Test
    @DisplayName("즐겨찾기 설정 테스트")
    void setStar() {
        //given
        String code = "code";
        Star savedStar = Star.builder().id(1L).userId(1L).code(code).build();
        when(starRepository.save(any(Star.class))).thenReturn(savedStar);

        //when
        StarResponse starResponse = starService.setStar(1L, code);

        //then
        assertEquals(1L, starResponse.getUserId());
        assertEquals(code, starResponse.getCode());
    }

}