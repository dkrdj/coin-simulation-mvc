package com.mvc.coinsimulation.entity;

import com.mvc.coinsimulation.dto.response.StarResponse;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Entity
public class Star {
    @Id
    private Long id;
    private Long userId;
    private String code;

    public StarResponse toResponse() {
        return new StarResponse(userId, code);
    }
}
