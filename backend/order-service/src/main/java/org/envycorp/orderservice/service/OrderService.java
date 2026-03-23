package org.envycorp.orderservice.service;

import lombok.RequiredArgsConstructor;
import org.envycorp.orderservice.exception.OrderIsNotExisted;
import org.envycorp.orderservice.exception.WrongOrderIdException;
import org.envycorp.orderservice.model.dto.response.OrderResponseDto;
import org.envycorp.orderservice.model.entity.Order;
import org.envycorp.orderservice.repository.OrderRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final ModelMapper modelMapper;

    public List<OrderResponseDto> getAllOrders(Long userId) {
        List<Order> orders = orderRepository.getAllByUserId(userId);

        return orders.stream()
                .map(order -> {
                    return modelMapper.map(order, OrderResponseDto.class);
                }).toList();
    }

    public OrderResponseDto getOrderById(Long userId, Long id) {
        Order order = orderRepository.getOrderByUserId(id)
                .orElseThrow(() -> new OrderIsNotExisted("Order with id " + id + "isn't exist"));

        if(!order.getUserId().equals(userId))
            throw new WrongOrderIdException("Wrong Order Id");

        return modelMapper.map(order, OrderResponseDto.class);
    }
}
