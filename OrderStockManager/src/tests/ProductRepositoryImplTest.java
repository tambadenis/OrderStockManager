package tests;

import entities.Product;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import repository.ProductRepositoryImpl;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ProductRepositoryImplTest {
    private ProductRepositoryImpl productRepositoryImpl;

    public ProductRepositoryImplTest() {
        this.productRepositoryImpl = new ProductRepositoryImpl();
    }

    @Mock
    private PreparedStatement statement;
    @Mock
    private ResultSet resultSet;

    @Test
    public void addProductTest() throws SQLException {
        Product product = new Product("Test Product", 0, 5);
        productRepositoryImpl.addProduct(product);
        verify(statement).setString(1, "Test Product");
        verify(statement).setInt(2, 5);
        verify(statement).executeUpdate();
    }

    @Test
    public void updateProductTest() throws SQLException {
        Product product = new Product("Test Product", 1, 5);
        productRepositoryImpl.updateProduct(product);
        verify(statement).setString(1, "Test Product");
        verify(statement).setInt(2, 5);
        verify(statement).setInt(3, 1);
        verify(statement).executeUpdate();
    }

    @Test
    public void deleteProductTest() throws SQLException {
        productRepositoryImpl.deleteProduct(1);
        verify(statement).setInt(1, 1);
        verify(statement).executeUpdate();
    }

    @Test
    public void getProductByIdTest() throws SQLException {
        when(resultSet.next()).thenReturn(true).thenReturn(false);
        when(resultSet.getString("name")).thenReturn("Test Product");
        when(resultSet.getInt("id")).thenReturn(1);
        when(resultSet.getInt("stock")).thenReturn(5);
        Product product = productRepositoryImpl.getProductById(1);
        assertEquals("Test Product", product.getName());
        assertEquals(1, product.getId());
        assertEquals(5, product.getId());
    }
}

