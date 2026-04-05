package com.edulearn.payment.controller;

import com.edulearn.common.ApiResponse;
import com.edulearn.common.Constants;
import com.edulearn.payment.dto.CreateOrderRequest;
import com.edulearn.payment.dto.OrderCheckoutResponse;
import com.edulearn.payment.dto.OrderResponse;
import com.edulearn.payment.service.PaymentOrderService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(Constants.API_V1_PREFIX)
public class OrderController {

    private final PaymentOrderService paymentOrderService;

    @PostMapping("/courses/{courseId}/orders")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<OrderCheckoutResponse>> checkoutCourse(
            @PathVariable Long courseId,
            @RequestBody(required = false) CreateOrderRequest request,
            Authentication authentication
    ) {
        OrderCheckoutResponse response = paymentOrderService.checkoutEnrollment(
                courseId,
                request != null ? request.getPaymentMethod() : null,
                authentication.getName()
        );
        return ResponseEntity.ok(ApiResponse.success("Order created and payment completed", response));
    }

    @GetMapping("/orders/me")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getMyOrders(Authentication authentication) {
        List<OrderResponse> response = paymentOrderService.getMyOrders(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("My orders fetched", response));
    }
}

