package com.mvc.coinsimulation.controller;

import com.mvc.coinsimulation.dto.request.UserInfoChangeRequest;
import com.mvc.coinsimulation.dto.response.UserResponse;
import com.mvc.coinsimulation.exception.FileValidatorException;
import com.mvc.coinsimulation.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<UserResponse> getUserInfo(@SessionAttribute("user") Long userId) {
        return ResponseEntity.ok(userService.getUserInfo(userId));
    }

    @PatchMapping
    public ResponseEntity<UserResponse> changeUserInfo(@SessionAttribute("user") Long userId, @RequestBody UserInfoChangeRequest request) {
        return ResponseEntity.ok(userService.changeUserInfo(userId, request));
    }

    @PatchMapping("profile")
    public ResponseEntity<UserResponse> changeUserProfile(@SessionAttribute("user") Long userId, @RequestPart("profile") MultipartFile file) {
        try {
            return ResponseEntity.ok(userService.changeUserProfile(userId, file));
        } catch (IOException e) {
            throw new FileValidatorException();
        }
    }


}
