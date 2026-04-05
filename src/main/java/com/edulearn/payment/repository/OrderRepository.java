package com.edulearn.payment.repository;

import com.edulearn.payment.entity.Order;
import com.edulearn.payment.entity.OrderStatus;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByIdAndUserId(Long orderId, Long userId);

    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);

    long countByStatus(OrderStatus status);

    @Query(value = "select coalesce(sum(total_amount), 0) from orders where status = :status", nativeQuery = true)
    BigDecimal sumTotalAmountByStatus(@Param("status") OrderStatus status);
}

