package com.mvc.coinsimulation.controller;

import com.mvc.coinsimulation.dto.response.AssetResponse;
import com.mvc.coinsimulation.dto.response.UserResponse;
import com.mvc.coinsimulation.service.AssetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 자산 관련 요청을 처리하는 컨트롤러 클래스
 *
 * @Author 이상현
 * @Version 1.0.0
 * @See None
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("asset")
public class AssetController {
    private final AssetService assetService;

    /**
     * 특정 사용자의 자산 정보를 반환하는 메서드
     *
     * @param userId 세션에 저장된 사용자 ID
     * @return ResponseEntity<List < AssetResponse>>
     */
    @GetMapping
    public ResponseEntity<List<AssetResponse>> getAsset(@SessionAttribute("user") Long userId) {
        return ResponseEntity.ok(assetService.getAsset(userId));
    }

    /**
     * 특정 사용자의 자산을 초기화하는 메서드
     *
     * @param userId 세션에 저장된 사용자 ID
     * @return ResponseEntity<UserResponse>
     */
    @PatchMapping("reset")
    public ResponseEntity<UserResponse> resetAsset(@SessionAttribute("user") Long userId) {
        return ResponseEntity.ok(assetService.resetCash(userId));
    }
}
