package repository;

import entities.Product;

import javax.sql.DataSource;
import java.util.List;

public interface ProductRepository {
    void addProduct(Product product);
    void updateProduct(Product product);
    void deleteProduct(int id);
    Product getProductById(int id);
    List<Product> getAllProducts();
}
