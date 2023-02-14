package queue;

import com.rabbitmq.client.*;
import entities.Product;
import org.json.JSONArray;
import org.json.JSONObject;
import util.PropertiesLoader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class RabbitMQConsumer {
    private static final int CONCURRENT_ORDERS = 5;

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(PropertiesLoader.getProperty("rabbitmq.host"));
        factory.setPort(Integer.parseInt(PropertiesLoader.getProperty("rabbitmq.port")));
        factory.setUsername(PropertiesLoader.getProperty("rabbitmq.username"));
        factory.setPassword(PropertiesLoader.getProperty("rabbitmq.password"));
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        String ordersQueue = PropertiesLoader.getProperty("rabbitmq.orders.queue");
        channel.queueDeclare(ordersQueue, false, false, false, null);

        String ordersResponseQueue = PropertiesLoader.getProperty("rabbitmq.orders.response.queue");
        channel.queueDeclare(ordersResponseQueue, false, false, false, null);

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                JSONObject order = new JSONObject(new String(body, StandardCharsets.UTF_8));
                String orderId = order.getString("orderId");
                String response = processOrder(order);
                sendResponse(orderId, response, channel);
            }
        };

        channel.basicQos(CONCURRENT_ORDERS);
        channel.basicConsume(ordersQueue, false, consumer);
    }

    public static String processOrder(JSONObject order) {
        // Retrieve the products from the order
        JSONArray products = order.getJSONArray("products");

        // Check if there are enough products in stock to fulfill the order
        boolean stockSufficient = checkStock(products);

        // Return the response based on the stock check result
        if (stockSufficient) {
            return "RESERVED";
        } else {
            return "INSUFFICIENT_STOCKS";
        }
    }

    public static boolean checkStock(JSONArray products) {
        List<Product> productList = new ArrayList<>();

        for (int i = 0; i < products.length(); i++) {
            JSONObject product = products.getJSONObject(i);
            String name = product.getString("name");
            int stock = product.getInt("stock");
            productList.add(new Product(name, stock));
        }

        for (Product p : productList) {
            if (p.getStock() <= 0) {
                return false;
            }
        }
        return true;
    }

    private static void sendResponse(String orderId, String response, Channel channel) throws IOException {
        JSONObject responseObject = new JSONObject();
        responseObject.put("orderId", orderId);
        responseObject.put("response", response);
        channel.basicPublish("", PropertiesLoader.getProperty("rabbitmq.orders.response.queue"), null, responseObject.toString().getBytes());
    }

}
