import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import entities.Product;
import repository.ProductRepository;
import util.XMLProcessor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class XMLProcessorTest {
    private XMLProcessor xmlProcessor;
    private ProductRepository productRepository;
    private File inputFile;
    private List<Product> products;

    @Before
    public void setUp() {
        productRepository = Mockito.mock(ProductRepository.class);
        xmlProcessor = new XMLProcessor(productRepository);
        inputFile = new File("test.xml");

        try (FileOutputStream fos = new FileOutputStream(inputFile)) {
            byte[] data = ("<products><name>Product 1</name><id>1</id><stock>10</stock></products>").getBytes();
            fos.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }

        products = new ArrayList<>();
    }


    @Test
    public void testCreateFolderIfNotExists() {
        File inputFolder = new File(xmlProcessor.inputFolderPath);
        xmlProcessor.createFolderIfNotExists(xmlProcessor.inputFolderPath);
        assertTrue(inputFolder.exists());
    }

    @Test
    public void testParseXMLFileSuccessful() {
        boolean success = inputFile.setReadable(true);
        products = xmlProcessor.parseXMLFile(inputFile);
        assertTrue(success);
        assertNotNull(products);
    }

    @Test
    public void testParseXMLFileFailure() {
        boolean success = inputFile.setReadable(false);
        products = xmlProcessor.parseXMLFile(inputFile);
        assertTrue(success);
        assertTrue(products.isEmpty());
    }

    @Test
    public void testPersistProducts() {
        products.add(new Product());
        xmlProcessor.persistProducts(products);
        Mockito.verify(productRepository, Mockito.times(1)).addProduct(Mockito.any(Product.class));
    }

    @Test
    public void testMoveFileSuccessful() {
        File processedFolder = new File(xmlProcessor.processedFolderPath);
        xmlProcessor.moveFile(inputFile, xmlProcessor.processedFolderPath);
        assertTrue(processedFolder.exists());
        if (processedFolder.listFiles() != null) {
            assertTrue(processedFolder.listFiles().length > 0);
        }
    }
}
