import entities.Order;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import repository.OrderRepository;
import repository.OrderRepositoryImpl;

import java.util.List;

import static org.junit.Assert.*;

public class OrderRepositoryImplTest {
    private OrderRepository orderRepository;
    private Order order;

    @Before
    public void setUp() throws Exception {
        orderRepository = new OrderRepositoryImpl();
        order = new Order("order1", "client1", "status1");
    }

    @After
    public void tearDown() throws Exception {
        orderRepository = null;
        order = null;
    }

    @Test
    public void testAddOrder() {
        orderRepository.addOrder(order);
        Order result = orderRepository.getOrderById(order.getId());
        assertEquals(order.getName(), result.getName());
        assertEquals(order.getClient(), result.getClient());
        assertEquals(order.getStatus(), result.getStatus());
    }

    @Test
    public void testUpdateOrder() {
        orderRepository.addOrder(order);
        order.setName("newOrder");
        order.setClient("newClient");
        order.setStatus("newStatus");
        orderRepository.updateOrder(order);
        Order result = orderRepository.getOrderById(order.getId());
        assertEquals(order.getName(), result.getName());
        assertEquals(order.getClient(), result.getClient());
        assertEquals(order.getStatus(), result.getStatus());
    }

    @Test
    public void testDeleteOrder() {
        orderRepository.addOrder(order);
        orderRepository.deleteOrder(order.getId());
        Order result = orderRepository.getOrderById(order.getId());
        assertNull(result);
    }

    @Test
    public void testGetOrderById() {
        orderRepository.addOrder(order);
        Order result = orderRepository.getOrderById(order.getId());
        assertEquals(order.getName(), result.getName());
        assertEquals(order.getClient(), result.getClient());
        assertEquals(order.getStatus(), result.getStatus());
    }

    @Test
    public void testGetAllOrders() {
        orderRepository.addOrder(order);
        List<Order> result = orderRepository.getAllOrders();
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }
}