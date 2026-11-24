
package com.tradeflow.service;

import com.tradeflow.dto.OrderRequest;
import com.tradeflow.dto.OrderResponse;
import com.tradeflow.entity.Order;
import com.tradeflow.repository.OrderRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    // Create Order
    public OrderResponse createOrder(OrderRequest request) {

        Order order = new Order();

        order.setSymbol(request.getSymbol());
        order.setOrderType(request.getOrderType());
        order.setQuantity(request.getQuantity());
        order.setPrice(request.getPrice());

        if (order.getQuantity() == null ||
                order.getQuantity() <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be greater than zero");
        }

        if (order.getPrice() == null ||
                order.getPrice().signum() <= 0) {
            throw new IllegalArgumentException(
                    "Price must be greater than zero");
        }

        if (order.getSymbol() == null ||
                order.getSymbol().trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Symbol cannot be empty");
        }

        if (order.getOrderType() == null ||
                (!order.getOrderType().equalsIgnoreCase("BUY") &&
                        !order.getOrderType().equalsIgnoreCase("SELL"))) {
            throw new IllegalArgumentException(
                    "Order type must be BUY or SELL");
        }

        Order savedOrder = orderRepository.save(order);

        return new OrderResponse(savedOrder);
    }

    // Get All Orders
    public List<OrderResponse> getAllOrders() {

        return orderRepository.findAll()
                .stream()
                .map(OrderResponse::new)
                .collect(Collectors.toList());
    }

    // Get Order By ID
    public OrderResponse getOrderById(Long id) {

        Order order = findOrder(id);

        return new OrderResponse(order);
    }

    // Update Order
    public OrderResponse updateOrder(
            Long id, OrderRequest request) {

        Order existingOrder = findOrder(id);

        if (!"PENDING".equals(existingOrder.getStatus())) {
            throw new IllegalStateException(
                    "Only pending orders can be updated");
        }

        if (request.getQuantity() == null ||
                request.getQuantity() <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be greater than zero");
        }

        if (request.getPrice() == null ||
                request.getPrice().signum() <= 0) {
            throw new IllegalArgumentException(
                    "Price must be greater than zero");
        }

        if (request.getSymbol() == null ||
                request.getSymbol().trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Symbol cannot be empty");
        }

        if (request.getOrderType() == null ||
                (!request.getOrderType().equalsIgnoreCase("BUY") &&
                        !request.getOrderType().equalsIgnoreCase("SELL"))) {
            throw new IllegalArgumentException(
                    "Order type must be BUY or SELL");
        }

        existingOrder.setSymbol(request.getSymbol());
        existingOrder.setOrderType(request.getOrderType());
        existingOrder.setQuantity(request.getQuantity());
        existingOrder.setPrice(request.getPrice());

        Order updatedOrder = orderRepository.save(existingOrder);

        return new OrderResponse(updatedOrder);
    }

    // Cancel Order
    public OrderResponse cancelOrder(Long id) {

        Order order = findOrder(id);

        if (!"PENDING".equals(order.getStatus())) {
            throw new IllegalStateException(
                    "Only pending orders can be cancelled");
        }

        order.setStatus("CANCELLED");

        Order cancelledOrder = orderRepository.save(order);

        return new OrderResponse(cancelledOrder);
    }

    // Delete Order
    public void deleteOrder(Long id) {

        Order order = findOrder(id);

        orderRepository.delete(order);
    }

    // Common method
    private Order findOrder(Long id) {

        return orderRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Order not found with ID: " + id));
    }
}
