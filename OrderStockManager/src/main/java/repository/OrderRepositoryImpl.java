package repository;

import entities.Order;
import util.PropertiesLoader;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderRepositoryImpl implements OrderRepository {
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_URL = PropertiesLoader.getProperty("database.url");
    private static final String USER = PropertiesLoader.getProperty("database.username");
    private static final String PASS = PropertiesLoader.getProperty("database.password");

    public OrderRepositoryImpl() {
        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to initialize JDBC driver", e);
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

    @Override
    public void addOrder(Order order) {
        try (Connection connection = getConnection()) {
            String sql = "INSERT INTO orders (name, client, status) VALUES (?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, order.getName());
            statement.setString(2, order.getClient());
            statement.setString(3, order.getStatus());
            statement.executeUpdate();
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                order.setId(generatedKeys.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateOrder(Order order) {
        try (Connection connection = getConnection()) {
            String sql = "UPDATE orders SET name = ?, client = ?, status = ? WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, order.getName());
            statement.setString(2, order.getClient());
            statement.setString(3, order.getStatus());
            statement.setInt(4, order.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteOrder(int id) {
        try (Connection connection = getConnection()) {
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
        try (Connection connection = getConnection()) {
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
        try (Connection connection = getConnection()) {
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