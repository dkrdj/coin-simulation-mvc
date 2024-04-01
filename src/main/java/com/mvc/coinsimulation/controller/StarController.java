package com.mvc.coinsimulation.controller;

import com.mvc.coinsimulation.dto.response.StarResponse;
import com.mvc.coinsimulation.service.StarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("star")
public class StarController {
    private final StarService starService;

    @GetMapping
    public ResponseEntity<List<StarResponse>> getStarredCoin(@SessionAttribute("user") Long userId) {
        return ResponseEntity.ok(starService.getStars(userId));
    }

    @PostMapping
    public ResponseEntity<StarResponse> makeStarredCoin(@SessionAttribute("user") Long userId, String code) {
        return ResponseEntity.ok(starService.setStar(userId, code));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteStarredCoin(@SessionAttribute("user") Long userId, String code) {
        starService.setUnStar(userId, code);
        return ResponseEntity.ok().build();
    }
}
