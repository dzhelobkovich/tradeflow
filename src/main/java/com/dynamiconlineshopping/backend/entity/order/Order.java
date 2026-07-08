package com.dynamiconlineshopping.backend.entity.order;

import com.dynamiconlineshopping.backend.entity.user.DeliveryInfo;
import com.dynamiconlineshopping.backend.entity.user.User;
import com.dynamiconlineshopping.backend.enums.delivery.DeliveryMethod;
import com.dynamiconlineshopping.backend.enums.order.OrderType;
import com.dynamiconlineshopping.backend.enums.payment.PaymentMethod;
import com.dynamiconlineshopping.backend.enums.payment.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private Double totalAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id")
    private OrderStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method")
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    private PaymentStatus paymentStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_method")
    private DeliveryMethod deliveryMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_type")
    private OrderType orderType;

    @Column(name = "delivery_address")
    private String deliveryAddress;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_info_id")
    private DeliveryInfo deliveryInfo;

    @Column(name = "recipient_name")
    private String recipientName;

    @Column(name = "recipient_phone")
    private String recipientPhone;

    @Column(name = "comment")
    private String comment;

    @Column(name = "cancel_reason")
    private String cancelReason;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "paid_at")
    private Instant paidAt;

    @Column(name = "confirmed_at")
    private Instant confirmedAt;

    @Column(name = "assembled_at")
    private Instant assembledAt;

    @Column(name = "delivered_at")
    private Instant deliveredAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "picker_id")
    private User picker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "courier_id")
    private User courier;

    @Column(name = "stripe_payment_intent_id")
    private String stripePaymentIntentId;

    @Column(name = "external_payment_id")
    private String externalPaymentId;

    @Column(name = "original_amount")
    private Double originalAmount;

    @Column(name = "used_bonus_amount")
    private Integer usedBonusAmount;

    @Column(name = "bonus_discount_amount")
    private Double bonusDiscountAmount;

    @Column(name = "accrued_bonus_amount")
    private Integer accruedBonusAmount;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();
}
