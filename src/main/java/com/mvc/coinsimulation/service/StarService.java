package com.mvc.coinsimulation.service;

import com.mvc.coinsimulation.dto.response.StarResponse;
import com.mvc.coinsimulation.entity.Star;
import com.mvc.coinsimulation.repository.postgres.StarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 사용자가 즐겨찾기로 등록한 코인에 관련된 비즈니스 로직을 처리하는 서비스 클래스
 *
 * @Author 이상현
 * @Version 1.0.0
 * @See None
 */
@Service
@RequiredArgsConstructor
public class StarService {

    private final StarRepository starRepository;

    /**
     * 사용자가 등록한 즐겨찾기 목록을 조회하는 메서드
     *
     * @param userId 사용자의 ID
     * @return List<StarResponse> 사용자가 등록한 즐겨찾기 목록
     */
    public List<StarResponse> getStars(Long userId) {
        return starRepository.findByUserId(userId)
                .stream()
                .map(Star::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 사용자가 즐겨찾기로 등록한 코인을 추가하는 메서드
     *
     * @param userId 사용자의 ID
     * @param code   코인 코드
     * @return StarResponse 사용자가 추가한 즐겨찾기 정보
     */
    @Transactional
    public StarResponse setStar(Long userId, String code) {
        Star star = starRepository.save(
                Star.builder()
                        .userId(userId)
                        .code(code)
                        .build()
        );
        return star.toResponse();
    }

    /**
     * 사용자가 즐겨찾기로 등록한 코인을 삭제하는 메서드
     *
     * @param userId 사용자의 ID
     * @param code   코인 코드
     */
    @Transactional
    public void setUnStar(Long userId, String code) {
        starRepository.deleteByUserIdAndCode(userId, code);
    }
}
