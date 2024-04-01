package com.mvc.coinsimulation.controller;

import com.mvc.coinsimulation.dto.response.ExecutionResponse;
import com.mvc.coinsimulation.service.ExecutionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("execution")
public class ExecutionController {
    private final ExecutionService executionService;

    @GetMapping
    public ResponseEntity<List<ExecutionResponse>> getExecution(@SessionAttribute("user") Long userId) {
        return ResponseEntity.ok(executionService.getExecutions(userId));
    }
}
