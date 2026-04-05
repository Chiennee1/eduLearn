package com.edulearn.payment.dto;

import com.edulearn.payment.entity.OrderStatus;
import com.edulearn.payment.entity.PaymentMethod;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderResponse {

    private final Long id;
    private final BigDecimal totalAmount;
    private final OrderStatus status;
    private final PaymentMethod paymentMethod;
    private final String transactionId;
    private final LocalDateTime createdAt;
    private final List<OrderItemResponse> items;
}

