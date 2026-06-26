package com.dynamiconlineshopping.backend.service.order;

import com.dynamiconlineshopping.backend.dto.order.CreateOrderRequestDto;
import com.dynamiconlineshopping.backend.dto.order.OrderResponseDto;
import com.dynamiconlineshopping.backend.dto.order.OrderStatusHistoryDto;

import java.util.List;

public interface OrderService {
    OrderResponseDto placeOrderForCurrentUser(CreateOrderRequestDto request);
    OrderResponseDto getById(Long id);
    List<OrderResponseDto> listOrdersForCurrentUser();

    List<OrderResponseDto> getAllOrders();
    List<OrderResponseDto> getOrdersByStatus(String statusName);
    OrderResponseDto updateOrderStatus(Long orderId, String statusName);
    List<OrderStatusHistoryDto> getOrderHistoryForCurrentUser(Long orderId);
}
