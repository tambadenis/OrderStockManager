package repository;

import entities.Product;
import util.PropertiesLoader;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductRepositoryImpl implements ProductRepository {
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_URL = PropertiesLoader.getProperty("database.url");
    private static final String USER = PropertiesLoader.getProperty("database.username");
    private static final String PASS = PropertiesLoader.getProperty("database.password");

    public ProductRepositoryImpl() {
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
    public void addProduct(Product product) {
        try (Connection connection = getConnection()) {
            String sql = "INSERT INTO product (name, stock) VALUES (?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, product.getName());
                statement.setInt(2, product.getStock());
                statement.executeUpdate();
                ResultSet resultSet = statement.getGeneratedKeys();
                if (resultSet.next()) {
                    int id = resultSet.getInt(1);
                    product.setId(id);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to add product", e);
        }
    }

    @Override
    public void updateProduct(Product product) {
        try (Connection connection = getConnection()) {
            String sql = "UPDATE product SET name = ?, stock = ? WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, product.getName());
                statement.setInt(2, product.getStock());
                statement.setInt(3, product.getId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update product", e);
        }
    }

    @Override
    public void deleteProduct(int id) {
        try (Connection connection = getConnection()) {
            String sql = "DELETE FROM product WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, id);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete product", e);
        }
    }

    @Override
    public Product getProductById(int id) throws SQLException {
        Product product = null;
        try (Connection connection = getConnection()) {
            String sql = "SELECT * FROM product WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, id);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    String name = resultSet.getString("name");
                    int stock = resultSet.getInt("stock");
                    product = new Product(name, id, stock);
                }
            } catch (SQLException e) {
                throw new RuntimeException("Failed to delete product", e);
            }
            return product;
        }
    }

    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        try (Connection connection = getConnection()) {
            String sql = "SELECT * FROM product";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                ResultSet resultSet = statement.executeQuery();
                System.out.println("Number of rows in the result set: " + resultSet.getRow());
                while (resultSet.next()) {
                    String name = resultSet.getString("name");
                    int stock = resultSet.getInt("stock");
                    products.add(new Product(name, stock));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get products", e);
        }
        return products;
    }
}
