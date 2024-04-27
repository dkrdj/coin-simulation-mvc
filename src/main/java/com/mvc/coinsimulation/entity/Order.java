package com.mvc.coinsimulation.entity;

import com.mvc.coinsimulation.dto.response.OrderResponse;
import com.mvc.coinsimulation.enums.Gubun;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@ToString
@DynamicUpdate
@Entity(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;
    private String code;
    @Enumerated(EnumType.STRING)
    private Gubun gubun;
    private BigDecimal price;
    @Setter
    private BigDecimal amount;
    private BigDecimal prePrice;
    private LocalDateTime dateTime;

    public OrderResponse toResponse() {
        OrderResponse orderResponse = new OrderResponse();
        BeanUtils.copyProperties(this, orderResponse);
        return orderResponse;
    }
}
