package repository;

import entities.Product;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

public interface ProductRepository {
    void addProduct(Product product);
    void updateProduct(Product product);
    void deleteProduct(int id);
    Product getProductById(int id) throws SQLException;
    List<Product> getAllProducts();
}
