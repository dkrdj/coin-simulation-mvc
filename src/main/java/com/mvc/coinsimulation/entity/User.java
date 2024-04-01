package com.mvc.coinsimulation.entity;

import com.mvc.coinsimulation.dto.response.UserResponse;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Entity(name = "users")
public class User implements Serializable {
    @Id
    private Long id;
    @Setter
    private String nickname;
    @Setter
    private String role;
    @Setter
    private String profile;
    private Long providerId;
    @Setter
    private Double cash;

    public UserResponse toResponse() {
        UserResponse userResponse = new UserResponse();
        BeanUtils.copyProperties(this, userResponse);
        return userResponse;
    }
}
