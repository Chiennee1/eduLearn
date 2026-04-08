package com.edulearn.payment.service;

import com.edulearn.auth.entity.User;
import com.edulearn.course.dto.EnrollmentResponse;
import com.edulearn.course.entity.Course;
import com.edulearn.course.entity.CourseStatus;
import com.edulearn.course.repository.CourseRepository;
import com.edulearn.course.service.EnrollmentService;
import com.edulearn.course.service.LearningAccessService;
import com.edulearn.exception.BusinessException;
import com.edulearn.exception.ResourceNotFoundException;
import com.edulearn.payment.dto.OrderCheckoutResponse;
import com.edulearn.payment.dto.OrderItemResponse;
import com.edulearn.payment.dto.OrderResponse;
import com.edulearn.payment.entity.Order;
import com.edulearn.payment.entity.OrderItem;
import com.edulearn.payment.entity.OrderStatus;
import com.edulearn.payment.entity.PaymentMethod;
import com.edulearn.payment.repository.OrderRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentOrderService {

    private final OrderRepository orderRepository;
    private final CourseRepository courseRepository;
    private final LearningAccessService learningAccessService;
    private final EnrollmentService enrollmentService;

    @Transactional
    public OrderCheckoutResponse checkoutEnrollment(Long courseId, PaymentMethod requestedMethod, String actorEmail) {
        User actor = learningAccessService.getActor(actorEmail);
        learningAccessService.requireStudent(actor);

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        if (course.getStatus() != CourseStatus.PUBLISHED) {
            throw new ResourceNotFoundException("Course not found");
        }

        PaymentMethod paymentMethod = resolvePaymentMethod(course.getPrice(), requestedMethod);

        Order order = Order.builder()
                .user(actor)
                .totalAmount(course.getPrice())
                .status(OrderStatus.PENDING)
                .paymentMethod(paymentMethod)
                .build();

        OrderItem orderItem = OrderItem.builder()
                .order(order)
                .course(course)
                .price(course.getPrice())
                .build();
        order.getItems().add(orderItem);

        order = orderRepository.save(order);

        completeInstantPayment(order, paymentMethod);
        EnrollmentResponse enrollment = enrollmentService.enroll(courseId, actorEmail);

        return OrderCheckoutResponse.builder()
                .order(toResponse(order))
                .enrollment(enrollment)
                .build();
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getMyOrders(String actorEmail) {
        User actor = learningAccessService.getActor(actorEmail);
        return orderRepository.findByUserIdOrderByCreatedAtDesc(actor.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private void completeInstantPayment(Order order, PaymentMethod paymentMethod) {
        order.setStatus(OrderStatus.COMPLETED);
        String transactionPrefix = paymentMethod != null ? paymentMethod.name() : "PAY";
        order.setTransactionId(transactionPrefix + "-" + UUID.randomUUID());
        orderRepository.save(order);
    }

    private PaymentMethod resolvePaymentMethod(BigDecimal amount, PaymentMethod requestedMethod) {
        if (requestedMethod != null) {
            if (requestedMethod == PaymentMethod.FREE && amount.compareTo(BigDecimal.ZERO) > 0) {
                throw new BusinessException("FREE method is only allowed for zero-price courses",
                        HttpStatus.BAD_REQUEST);
            }
            return requestedMethod;
        }
        return amount.compareTo(BigDecimal.ZERO) == 0 ? PaymentMethod.FREE : PaymentMethod.MOMO;
    }

    private OrderResponse toResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .paymentMethod(order.getPaymentMethod())
                .transactionId(order.getTransactionId())
                .createdAt(order.getCreatedAt())
                .items(order.getItems().stream().map(item -> OrderItemResponse.builder()
                        .id(item.getId())
                        .courseId(item.getCourse().getId())
                        .courseTitle(item.getCourse().getTitle())
                        .price(item.getPrice())
                        .build()).toList())
                .build();
    }
}
