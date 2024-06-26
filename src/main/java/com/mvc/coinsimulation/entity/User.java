package com.mvc.coinsimulation.entity;

import com.mvc.coinsimulation.dto.common.UserDto;
import com.mvc.coinsimulation.dto.response.UserResponse;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@DynamicUpdate
@Entity(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Setter
    private String nickname;
    @Setter
    private String role;
    @Setter
    private String profile;
    private Long providerId;
    @Setter
    private BigDecimal cash;

    public UserResponse toResponse() {
        UserResponse userResponse = new UserResponse();
        BeanUtils.copyProperties(this, userResponse);
        return userResponse;
    }

    public UserDto toDto() {
        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(this, userDto);
        return userDto;
    }
}
