package com.dynamiconlineshopping.backend.entity.payment;

import com.dynamiconlineshopping.backend.entity.order.Order;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Order order;
    private String provider;
    private String providerPaymentId;
    private String providerOrderId;
    private Double amount;
    private String status;
    private Instant paidAt;
}
