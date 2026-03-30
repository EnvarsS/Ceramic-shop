package org.envycorp.orderservice.controller;

import lombok.RequiredArgsConstructor;
import org.envycorp.orderservice.model.dto.response.OrderResponseDto;
import org.envycorp.orderservice.service.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @GetMapping
    public List<OrderResponseDto> getOrdersHistory(@RequestHeader("X-User-Id") Long userId){
        return orderService.getAllOrders(userId);
    }

    @GetMapping("/{id}")
    public OrderResponseDto getOrderById(@RequestHeader("X-User-Id") Long userId, @PathVariable Long id){
        return orderService.getOrderById(userId, id);
    }
}
