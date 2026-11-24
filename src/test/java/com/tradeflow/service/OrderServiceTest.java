
package com.tradeflow.service;

import com.tradeflow.dto.OrderRequest;
import com.tradeflow.dto.OrderResponse;
import com.tradeflow.entity.Order;
import com.tradeflow.repository.OrderRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    // Test 1: Create Order Successfully
    @Test
    void createOrderSuccessfully() {

        OrderRequest request = new OrderRequest();
        request.setSymbol("NIFTY");
        request.setOrderType("BUY");
        request.setQuantity(50);
        request.setPrice(new BigDecimal("24500.50"));

        Order savedOrder = new Order();
        savedOrder.setOrderId(1L);
        savedOrder.setSymbol("NIFTY");
        savedOrder.setOrderType("BUY");
        savedOrder.setQuantity(50);
        savedOrder.setPrice(new BigDecimal("24500.50"));
        savedOrder.setStatus("PENDING");

        when(orderRepository.save(any(Order.class)))
                .thenReturn(savedOrder);

        OrderResponse response = orderService.createOrder(request);

        assertEquals(1L, response.getOrderId());
        assertEquals("NIFTY", response.getSymbol());
        assertEquals("BUY", response.getOrderType());
        assertEquals(50, response.getQuantity());
        assertEquals(0, new BigDecimal("24500.50")
                .compareTo(response.getPrice()));
        assertEquals("PENDING", response.getStatus());

        verify(orderRepository, times(1)).save(any(Order.class));
    }

    // Test 2: Reject Negative Quantity
    @Test
    void createOrderWithInvalidQuantity() {

        OrderRequest request = new OrderRequest();
        request.setSymbol("NIFTY");
        request.setOrderType("BUY");
        request.setQuantity(-50);
        request.setPrice(new BigDecimal("24500.50"));

        assertThrows(
                IllegalArgumentException.class,
                () -> orderService.createOrder(request)
        );

        verify(orderRepository, never()).save(any(Order.class));
    }

    // Test 3: Reject Negative Price
    @Test
    void createOrderWithInvalidPrice() {

        OrderRequest request = new OrderRequest();
        request.setSymbol("NIFTY");
        request.setOrderType("BUY");
        request.setQuantity(50);
        request.setPrice(new BigDecimal("-100"));

        assertThrows(
                IllegalArgumentException.class,
                () -> orderService.createOrder(request)
        );

        verify(orderRepository, never()).save(any(Order.class));
    }

    // Test 4: Get Order By ID
    @Test
    void getOrderByIdSuccessfully() {

        Order order = new Order();
        order.setOrderId(1L);
        order.setSymbol("NIFTY");
        order.setOrderType("BUY");
        order.setQuantity(50);
        order.setPrice(new BigDecimal("24500.50"));
        order.setStatus("PENDING");

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        OrderResponse response = orderService.getOrderById(1L);

        assertEquals(1L, response.getOrderId());
        assertEquals("NIFTY", response.getSymbol());
        assertEquals("BUY", response.getOrderType());
        assertEquals(50, response.getQuantity());
        assertEquals("PENDING", response.getStatus());

        verify(orderRepository, times(1)).findById(1L);
    }

    // Test 5: Cancel Order Successfully
    @Test
    void cancelOrderSuccessfully() {

        Order order = new Order();
        order.setOrderId(1L);
        order.setSymbol("NIFTY");
        order.setOrderType("BUY");
        order.setQuantity(50);
        order.setPrice(new BigDecimal("24500.50"));
        order.setStatus("PENDING");

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        OrderResponse response = orderService.cancelOrder(1L);

        assertEquals("CANCELLED", response.getStatus());
        assertEquals(1L, response.getOrderId());

        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    // Test 6: Update Order Successfully
    @Test
    void updateOrderSuccessfully() {

        Order existingOrder = new Order();
        existingOrder.setOrderId(1L);
        existingOrder.setSymbol("NIFTY");
        existingOrder.setOrderType("BUY");
        existingOrder.setQuantity(50);
        existingOrder.setPrice(new BigDecimal("24500.50"));
        existingOrder.setStatus("PENDING");

        OrderRequest request = new OrderRequest();
        request.setSymbol("BANKNIFTY");
        request.setOrderType("SELL");
        request.setQuantity(25);
        request.setPrice(new BigDecimal("52000.50"));

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(existingOrder));

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        OrderResponse response = orderService.updateOrder(1L, request);

        assertEquals(1L, response.getOrderId());
        assertEquals("BANKNIFTY", response.getSymbol());
        assertEquals("SELL", response.getOrderType());
        assertEquals(25, response.getQuantity());
        assertEquals(0, new BigDecimal("52000.50")
                .compareTo(response.getPrice()));
        assertEquals("PENDING", response.getStatus());

        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    // Test 7: Get All Orders Successfully
    @Test
    void getAllOrdersSuccessfully() {

        Order order1 = new Order();
        order1.setOrderId(1L);
        order1.setSymbol("NIFTY");
        order1.setOrderType("BUY");
        order1.setQuantity(50);
        order1.setPrice(new BigDecimal("24500.50"));
        order1.setStatus("PENDING");

        Order order2 = new Order();
        order2.setOrderId(2L);
        order2.setSymbol("BANKNIFTY");
        order2.setOrderType("SELL");
        order2.setQuantity(25);
        order2.setPrice(new BigDecimal("52000.50"));
        order2.setStatus("PENDING");

        when(orderRepository.findAll())
                .thenReturn(Arrays.asList(order1, order2));

        List<OrderResponse> responses = orderService.getAllOrders();

        assertEquals(2, responses.size());
        assertEquals("NIFTY", responses.get(0).getSymbol());
        assertEquals("BUY", responses.get(0).getOrderType());
        assertEquals("BANKNIFTY", responses.get(1).getSymbol());
        assertEquals("SELL", responses.get(1).getOrderType());

        verify(orderRepository, times(1)).findAll();
    }

    // Test 8: Delete Order Successfully
    @Test
    void deleteOrderSuccessfully() {

        Order order = new Order();
        order.setOrderId(1L);
        order.setSymbol("NIFTY");
        order.setOrderType("BUY");
        order.setQuantity(50);
        order.setPrice(new BigDecimal("24500.50"));
        order.setStatus("PENDING");

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        orderService.deleteOrder(1L);

        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).delete(order);
    }

    // Test 9: Prevent Updating a Cancelled Order
    @Test
    void updateCancelledOrderShouldThrowException() {

        Order cancelledOrder = new Order();
        cancelledOrder.setOrderId(1L);
        cancelledOrder.setSymbol("NIFTY");
        cancelledOrder.setOrderType("BUY");
        cancelledOrder.setQuantity(50);
        cancelledOrder.setPrice(new BigDecimal("24500.50"));
        cancelledOrder.setStatus("CANCELLED");

        OrderRequest request = new OrderRequest();
        request.setSymbol("BANKNIFTY");
        request.setOrderType("SELL");
        request.setQuantity(25);
        request.setPrice(new BigDecimal("52000.50"));

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(cancelledOrder));

        assertThrows(
                IllegalStateException.class,
                () -> orderService.updateOrder(1L, request)
        );

        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, never()).save(any(Order.class));
    }

    // Test 10: Prevent Cancelling an Already Cancelled Order
    @Test
    void cancelAlreadyCancelledOrderShouldThrowException() {

        Order order = new Order();
        order.setOrderId(1L);
        order.setSymbol("NIFTY");
        order.setOrderType("BUY");
        order.setQuantity(50);
        order.setPrice(new BigDecimal("24500.50"));
        order.setStatus("CANCELLED");

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        assertThrows(
                IllegalStateException.class,
                () -> orderService.cancelOrder(1L)
        );

        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, never()).save(any(Order.class));
    }

    // Test 11: Order Not Found
    @Test
    void getOrderByIdNotFound() {

        when(orderRepository.findById(999L))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> orderService.getOrderById(999L)
        );

        assertEquals(
                "Order not found with ID: 999",
                exception.getMessage()
        );

        verify(orderRepository, times(1)).findById(999L);
    }

    // Test 12: Delete Order Not Found
    @Test
    void deleteOrderNotFound() {

        when(orderRepository.findById(999L))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> orderService.deleteOrder(999L)
        );

        assertEquals(
                "Order not found with ID: 999",
                exception.getMessage()
        );

        verify(orderRepository, times(1)).findById(999L);
        verify(orderRepository, never()).delete(any(Order.class));
    }
}
