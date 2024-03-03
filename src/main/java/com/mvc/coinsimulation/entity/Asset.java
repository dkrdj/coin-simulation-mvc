package com.mvc.coinsimulation.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Entity
public class Asset {
    @Id
    private Long id;
    private Long userId;
    private String code;
    @Setter
    private Double averagePrice;
    @Setter
    private Double amount;
}
