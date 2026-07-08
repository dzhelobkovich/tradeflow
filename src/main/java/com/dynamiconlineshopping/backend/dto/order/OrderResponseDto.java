package com.dynamiconlineshopping.backend.dto.order;

import lombok.*;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponseDto {
    private Long id;
    private Double totalAmount;
    private String status;
    private String paymentMethod;
    private String paymentStatus;
    private String deliveryMethod;
    private String orderType;
    private String deliveryAddress;
    private DeliveryInfoDto deliveryInfo;
    private String recipientName;
    private String recipientPhone;
    private String comment;
    private String cancelReason;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant paidAt;
    private Instant confirmedAt;
    private Instant assembledAt;
    private Instant deliveredAt;
    private Instant completedAt;
    private List<OrderItemDto> items;
    private Long userId;
    private String userEmail;
    private String userFullName;
    private String userPhoneNumber;
    private Long pickerId;
    private String pickerFullName;
    private Long courierId;
    private String courierFullName;
    private String stripePaymentIntentId;
    private String externalPaymentId;
    private Double originalAmount;
    private Integer usedBonusAmount;
    private Double bonusDiscountAmount;
    private Integer accruedBonusAmount;
}
