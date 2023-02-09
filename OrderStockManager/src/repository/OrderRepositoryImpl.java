package repository;

import entities.Order;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderRepositoryImpl implements OrderRepository {
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/orderstockmanagerdb";
    private static final String USER = "root";
    private static final String PASS = "!Tdv7711335";

    public OrderRepositoryImpl() {
        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addOrder(Order order) {
        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS)) {
            String sql = "INSERT INTO orders (name, client, status) VALUES (?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, order.getName());
            statement.setString(2, order.getClient());
            statement.setString(3, order.getStatus());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateOrder(Order order) {
        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS)) {
            String sql = "UPDATE orders SET name = ?, client = ?, status = ? WHERE name = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, order.getName());
            statement.setString(2, order.getClient());
            statement.setString(3, order.getStatus());
            statement.setString(4, order.getName());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteOrder(int id) {
        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS)) {
            String sql = "DELETE FROM orders WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Order getOrderById(int id) {
        Order order = null;
        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS)) {
            String sql = "SELECT * FROM orders WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String name = resultSet.getString("name");
                String client = resultSet.getString("client");
                String status = resultSet.getString("status");
                order = new Order(name, client, status);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return order;
    }

    @Override
    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS)) {
             String sql = "SELECT * FROM orders";
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql); {
            while (resultSet.next()) {
                Order order = new Order();
                order.setName(resultSet.getString("name"));
                order.setClient(resultSet.getString("client"));
                order.setStatus(resultSet.getString("status"));
                orders.add(order);
            }
        }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return orders;
    }
}