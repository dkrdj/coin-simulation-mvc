package com.mvc.coinsimulation.dto.common;

import lombok.*;

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
    private Double cash;
}
