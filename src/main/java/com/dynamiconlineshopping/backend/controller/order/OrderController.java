package com.dynamiconlineshopping.backend.controller.order;

import com.dynamiconlineshopping.backend.dto.order.CreateOrderRequestDto;
import com.dynamiconlineshopping.backend.dto.order.OrderResponseDto;
import com.dynamiconlineshopping.backend.dto.order.OrderStatusHistoryDto;
import com.dynamiconlineshopping.backend.service.order.OrderService;
import com.dynamiconlineshopping.backend.service.order.OrderStatusHistoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderStatusHistoryService orderStatusHistoryService;

    @PostMapping("/place")
    public ResponseEntity<OrderResponseDto> placeOrder(@Valid @RequestBody CreateOrderRequestDto request) {
        return ResponseEntity.status(201).body(orderService.placeOrderForCurrentUser(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDto> getOrder(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponseDto>> listOrders() {
        return ResponseEntity.ok(orderService.listOrdersForCurrentUser());
    }

    @GetMapping("/{id}/history")
    public List<OrderStatusHistoryDto> getOrderHistory(@PathVariable Long id) {
        return orderStatusHistoryService.getOrderHistory(id);
    }

    @GetMapping("/my/{id}/history")
    public List<OrderStatusHistoryDto> getOrderHistoryForCurrentUser(@PathVariable Long id) {
        return orderService.getOrderHistoryForCurrentUser(id);
    }
}
