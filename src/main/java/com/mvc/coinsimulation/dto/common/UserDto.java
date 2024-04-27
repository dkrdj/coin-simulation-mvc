package com.mvc.coinsimulation.dto.common;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserDto {
    private Long id;
    private String nickname;
    private String role;
    private String profile;
    private Long providerId;
    private BigDecimal cash;
}
