import queue.RabbitMQConsumer;
import org.junit.Test;
import org.json.JSONArray;
import org.json.JSONObject;

import static org.junit.Assert.assertEquals;

public class RabbitMQConsumerTest {

    private static final int CONCURRENT_ORDERS = 5;

    @Test
    public void testProcessOrder() {
        JSONObject order = new JSONObject();
        order.put("orderId", "1");

        JSONArray products = new JSONArray();
        JSONObject product1 = new JSONObject();
        product1.put("name", "product1");
        product1.put("stock", 5);
        products.put(product1);
        JSONObject product2 = new JSONObject();
        product2.put("name", "product2");
        product2.put("stock", 0);
        products.put(product2);
        order.put("products", products);

        String response = RabbitMQConsumer.processOrder(order);
        assertEquals("INSUFFICIENT_STOCKS", response);
    }

    @Test
    public void testCheckStock() {
        JSONArray products = new JSONArray();
        JSONObject product1 = new JSONObject();
        product1.put("name", "product1");
        product1.put("stock", 5);
        products.put(product1);
        JSONObject product2 = new JSONObject();
        product2.put("name", "product2");
        product2.put("stock", 0);
        products.put(product2);

        boolean stockSufficient = RabbitMQConsumer.checkStock(products);
        assertEquals(false, stockSufficient);
    }
}
