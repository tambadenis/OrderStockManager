package repository;

import entities.Order;

import java.util.List;

public interface OrderRepository {
    void addOrder(Order order);
    void updateOrder(Order order);
    void deleteOrder(int id);
    Order getOrderById(int id);
    List<Order> getAllOrders();
}
