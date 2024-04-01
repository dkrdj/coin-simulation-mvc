package com.mvc.coinsimulation.controller;

import com.mvc.coinsimulation.dto.response.UserResponse;
import com.mvc.coinsimulation.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("rank")
public class RankController {
    private final UserService userService;

    @GetMapping()
    public ResponseEntity<List<UserResponse>> getRank() {
        return ResponseEntity.ok(userService.getTop10Users());
    }
}
