package com.mvc.coinsimulation.entity;

import com.mvc.coinsimulation.dto.response.StarResponse;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
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
