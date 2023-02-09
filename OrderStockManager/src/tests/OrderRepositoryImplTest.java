package tests;

import entities.Order;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import repository.OrderRepositoryImpl;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class OrderRepositoryImplTest {
    private OrderRepositoryImpl orderRepository;

    public OrderRepositoryImplTest() {
        this.orderRepository = new OrderRepositoryImpl();
    }

    @Test
    void addOrder() {
        Order order = new Order("test order", "test client", "test status");
        orderRepository.addOrder(order);
        Order retrievedOrder = orderRepository.getOrderById(order.getId());
//        if (retrievedOrder != null) {
//            assertEquals(order, retrievedOrder);
//        } else {
//            Assertions.fail("Order not found");
//        }
    }

    @Test
    void updateOrder() {
        Order order = new Order("test order", "test client", "test status");
        orderRepository.addOrder(order);
        order.setStatus("updated status");
        orderRepository.updateOrder(order);
        Order retrievedOrder = orderRepository.getOrderById(order.getId());
//        if (retrievedOrder != null) {
//            assertEquals(order, retrievedOrder);
//        } else {
//            Assertions.fail("Order not found");
//        }
    }

    @Test
    void deleteOrder() {
        Order order = new Order("test order", "test client", "test status");
        orderRepository.addOrder(order);
        orderRepository.deleteOrder(order.getId());
        Order retrievedOrder = orderRepository.getOrderById(order.getId());
        assertNull(retrievedOrder);
    }

    @Test
    void getOrderById() {
        Order order = new Order("test order", "test client", "test status");
        orderRepository.addOrder(order);
        Order retrievedOrder = orderRepository.getOrderById(order.getId());
//        if (retrievedOrder != null) {
//            assertEquals(order, retrievedOrder);
//        } else {
//            Assertions.fail("Order not found");
//        }
    }

    @Test
    void getAllOrders() {
        Order order1 = new Order("test order 1", "test client 1", "test status 1");
        Order order2 = new Order("test order 2", "test client 2", "test status 2");
        orderRepository.addOrder(order1);
        orderRepository.addOrder(order2);
        List<Order> orders = orderRepository.getAllOrders();
        Order[] expectedOrders = {order1, order2};
//        assertArrayEquals(expectedOrders, orders.toArray());
    }
}

