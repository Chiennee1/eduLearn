package com.edulearn.payment.dto;

import com.edulearn.course.dto.EnrollmentResponse;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderCheckoutResponse {

    private final OrderResponse order;
    private final EnrollmentResponse enrollment;
}

