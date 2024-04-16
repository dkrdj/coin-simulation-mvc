package com.mvc.coinsimulation.entity;

import com.mvc.coinsimulation.dto.response.OrderResponse;
import com.mvc.coinsimulation.enums.Gubun;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@ToString
@Entity(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private String code;
    @Enumerated(EnumType.STRING)
    private Gubun gubun;
    private Double price;
    @Setter
    private Double amount;
    private Double prePrice;
    private LocalDateTime dateTime;

    public OrderResponse toResponse() {
        OrderResponse orderResponse = new OrderResponse();
        BeanUtils.copyProperties(this, orderResponse);
        return orderResponse;
    }
}
