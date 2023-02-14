import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.List;

import entities.Product;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import repository.ProductRepositoryImpl;

public class ProductRepositoryImplTest {
    private ProductRepositoryImpl productRepository;

    @Before
    public void setUp() throws Exception {
        productRepository = new ProductRepositoryImpl();
    }

    @After
    public void tearDown() throws Exception {
        productRepository = null;
    }

    @Test
    public void testAddProduct() {
        Product product = new Product("Product A", 100);
        productRepository.addProduct(product);

        Product actual = null;
        try {
            actual = productRepository.getProductById(product.getId());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        assertEquals("Product A", actual.getName());
        assertEquals(100, actual.getStock());
    }

    @Test
    public void testUpdateProduct() {
        Product product = new Product("Product A", 100);
        productRepository.addProduct(product);

        product.setName("Product B");
        product.setStock(200);
        productRepository.updateProduct(product);

        Product actual = null;
        try {
            actual = productRepository.getProductById(product.getId());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        assertEquals("Product B", actual.getName());
        assertEquals(200, actual.getStock());
    }

    @Test
    public void testDeleteProduct() {
        Product product = new Product("Product A", 100);
        productRepository.addProduct(product);

        productRepository.deleteProduct(product.getId());
        Product actual = null;
        try {
            actual = productRepository.getProductById(product.getId());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        assertNull(actual);
    }

    @Test
    public void testGetProductById() throws SQLException {
        Product product = new Product("Product A", 100);
        productRepository.addProduct(product);

        Product actual = productRepository.getProductById(product.getId());
        assertEquals("Product A", actual.getName());
        assertEquals(100, actual.getStock());
    }

    @Test
    public void testGetAllProducts() {
        Product product1 = new Product("Product A", 100);
        Product product2 = new Product("Product B", 200);
        productRepository.addProduct(product1);
        productRepository.addProduct(product2);
        List<Product> actual = productRepository.getAllProducts();
        assertNotNull(actual);
        assertFalse(actual.isEmpty());
    }
}