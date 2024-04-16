package com.mvc.coinsimulation.dto.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String nickname;
    private String role;
    private String profile;
    private Long providerId;
    private Double cash;
}
