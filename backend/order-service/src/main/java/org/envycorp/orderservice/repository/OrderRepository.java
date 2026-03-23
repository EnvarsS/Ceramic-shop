package org.envycorp.orderservice.repository;

import org.envycorp.orderservice.model.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order,Long> {
    @Query("SELECT o FROM Order o JOIN FETCH o.orderItems WHERE o.userId = :userId")
    List<Order> getAllByUserId(@Param("userId") Long userId);

    @Query("SELECT o FROM Order o JOIN FETCH o.orderItems WHERE o.id = :orderId")
    Order getOrderByUserId(@Param("orderId") Long id);
}
