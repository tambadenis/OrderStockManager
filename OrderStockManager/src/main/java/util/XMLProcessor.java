package util;

import entities.Product;
import repository.ProductRepository;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class XMLProcessor {
    private static final Logger LOGGER = Logger.getLogger(XMLProcessor.class.getName());
    private final ProductRepository productRepository;
    public String inputFolderPath = "src/main/java/input/stocks_new";
    public String processedFolderPath = "src/main/java/input/stocks_processed";

    public XMLProcessor(ProductRepository productRepository) {
        this.productRepository = productRepository;
        createFolderIfNotExists(inputFolderPath);
        createFolderIfNotExists(processedFolderPath);
    }

    public void createFolderIfNotExists(String folderPath) {
        File folder = new File(folderPath);
        if (!folder.exists()) {
            if (folder.mkdir()) {
                LOGGER.info("Folder created successfully: " + folderPath);
            } else {
                LOGGER.warning("Failed to create folder: " + folderPath);
            }
        }
    }

    public void processXMLFiles() {
        File inputFolder = new File(inputFolderPath);
        File[] files = inputFolder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".xml")) {
                    List<Product> products = parseXMLFile(file);
                    persistProducts(products);
                }
            }
        }
    }

    public List<Product> parseXMLFile(File file) {
        List<Product> products = new ArrayList<>();
        boolean parsingSuccessful = false;
        if (file.exists() && file.isFile() && file.canRead()) {
            try {
                Serializer serializer = new Persister();
                Product product = serializer.read(Product.class, file);
                products.add(product);
                parsingSuccessful = true;
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error while parsing XML file: " + file.getName(), e);
            }
        } else {
            LOGGER.log(Level.SEVERE, "File does not exist, is not a file, or is not readable: " + file.getName());
        }
        if (parsingSuccessful) {
            moveFile(file, processedFolderPath);
        }
        return products;
    }


    public void persistProducts(List<Product> products) {
        for (Product product : products) {
            productRepository.addProduct(product);
        }
    }

    public void moveFile(File file, String processedFolderPath) {
        if (!file.exists()) {
            LOGGER.warning("Source file does not exist: " + file.getName());
            return;
        }

        File processedFolder = new File(processedFolderPath);
        if (!processedFolder.exists() && !processedFolder.mkdirs()) {
            LOGGER.warning("Failed to create processed folder: " + processedFolderPath);
            return;
        }

        File newFile = new File(processedFolder, file.getName());
        if (newFile.exists() && !newFile.delete()) {
            LOGGER.warning("Failed to delete destination file: " + newFile.getName());
            return;
        }

        if (file.renameTo(newFile)) {
            LOGGER.info("File moved successfully: " + file.getName());
        } else {
            LOGGER.warning("Failed to move the file: " + file.getName());
        }
    }

}
