package queue;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import entities.Order;
import entities.Product;
import org.json.JSONObject;
import util.PropertiesLoader;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RabbitMQProducer {

    private final ConnectionFactory connectionFactory;
    private final int concurrentRequests;

    public RabbitMQProducer(ConnectionFactory connectionFactory, int concurrentRequests) {
        this.connectionFactory = connectionFactory;
        this.concurrentRequests = concurrentRequests;
    }

    public void sendOrderRequest(Order order) throws IOException, TimeoutException {
        try (Connection connection = connectionFactory.newConnection()) {
            try (Channel channel = connection.createChannel()) {
                String ordersQueue = PropertiesLoader.getProperty("rabbitmq.orders.queue");
                channel.queueDeclare(ordersQueue, false, false, false, null);
                channel.basicQos(concurrentRequests);

                JSONObject orderJson = new JSONObject();
                orderJson.put("products", order.getProducts());
                orderJson.put("name", order.getName());
                orderJson.put("client", order.getClient());

                channel.basicPublish("", ordersQueue, null, orderJson.toString().getBytes());
            }
        }
    }

    public void receiveOrderRequests() throws IOException, TimeoutException {
        try (Connection connection = connectionFactory.newConnection()) {
            try (Channel channel = connection.createChannel()) {
                String ordersQueue = PropertiesLoader.getProperty("rabbitmq.orders.queue");
                channel.queueDeclare(ordersQueue, false, false, false, null);
                channel.basicQos(concurrentRequests);
                System.out.println(" [*] Waiting for orders. To exit press CTRL+C");

                DefaultConsumer consumer = new DefaultConsumer(channel) {
                    @Override
                    public void handleDelivery(String consumerTag, Envelope envelope,
                                               com.rabbitmq.client.AMQP.BasicProperties properties, byte[] body)
                            throws IOException {
                        JSONObject orderJson = new JSONObject(new String(body));
                        String products = orderJson.getJSONArray("products").toString();
                        Order order = new Order(orderJson.getString("name"), orderJson.getString("client"),
                                products);

                        String response = "RESERVED";
                        for (Object productObj : orderJson.getJSONArray("products")) {
                            JSONObject productJson = (JSONObject) productObj;
                            Product product = new Product(productJson.getString("name"),
                                    productJson.getInt("stock"));
                            if (!product.checkStock(productJson.getInt("stock"))) {
                                response = "INSUFFICIENT_STOCKS";
                                break;
                            }
                        }

                        String ordersResponseQueue = PropertiesLoader.getProperty("rabbitmq.orders.response.queue");
                        channel.queueDeclare(ordersResponseQueue, false, false, false, null);
                        channel.basicPublish("", ordersResponseQueue, null, response.getBytes());
                        channel.basicAck(envelope.getDeliveryTag(), false);
                    }
                };

                channel.basicConsume(ordersQueue, false, consumer);
            }
        }
    }
}

