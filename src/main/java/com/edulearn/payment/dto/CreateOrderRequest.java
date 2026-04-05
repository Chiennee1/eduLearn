package com.edulearn.payment.dto;

import com.edulearn.payment.entity.PaymentMethod;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateOrderRequest {

    private PaymentMethod paymentMethod;
}

