package com.edulearn.payment.dto;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderItemResponse {

    private final Long id;
    private final Long courseId;
    private final String courseTitle;
    private final BigDecimal price;
}

