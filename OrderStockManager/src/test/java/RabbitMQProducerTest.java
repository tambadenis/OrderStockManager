import com.rabbitmq.client.*;
import entities.Order;
import entities.Product;
import org.json.JSONObject;
import org.junit.*;
import queue.RabbitMQProducer;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertEquals;

public class RabbitMQProducerTest {

    private static final String TEST_ORDERS_QUEUE = "test_orders_queue";
    private static final String TEST_RESPONSE_QUEUE = "test_orders_response_queue";
    private static final int TEST_CONCURRENT_REQUESTS = 5;

    private ConnectionFactory connectionFactory;
    private Channel channel;
    private RabbitMQProducer producer;

    @Before
    public void setUp() throws IOException, TimeoutException {
        connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");

        channel = connectionFactory.newConnection().createChannel();
        channel.queueDeclare(TEST_ORDERS_QUEUE, false, false, false, null);
        channel.queueDeclare(TEST_RESPONSE_QUEUE, false, false, false, null);

        producer = new RabbitMQProducer(connectionFactory, TEST_CONCURRENT_REQUESTS);
    }

    @After
    public void tearDown() throws IOException, TimeoutException {
        channel.queueDelete(TEST_ORDERS_QUEUE);
        channel.queueDelete(TEST_RESPONSE_QUEUE);
        channel.close();
        connectionFactory = null;
    }

    @Test
    public void testSendOrderRequest() throws IOException, TimeoutException {
        Order order = new Order("Test Order", "Test Client",
                "{\"name\":\"Product 1\",\"quantity\":1}");

        producer.sendOrderRequest(order);

        GetResponse response = channel.basicGet(TEST_ORDERS_QUEUE, false);
        Assert.assertNotNull(response);

        JSONObject orderJson = new JSONObject(new String(response.getBody()));
        assertEquals("Test Order", orderJson.getString("name"));
        assertEquals("Test Client", orderJson.getString("client"));

        Product product = new Product("Product 1", 1);
        JSONObject productJson = orderJson.getJSONArray("products").getJSONObject(0);
        assertEquals(product.getName(), productJson.getString("name"));
        assertEquals(product.getStock(), productJson.getInt("quantity"));
    }

    @Test
    public void testReceiveOrderRequests() throws IOException, TimeoutException, InterruptedException {
        final String[] response = new String[1];

        new Thread(() -> {
            try {
                producer.receiveOrderRequests();
            } catch (IOException | TimeoutException e) {
                e.printStackTrace();
            }
        }).start();

        Order order = new Order("Test Order", "Test Client",
                "{\"name\":\"Product 1\",\"quantity\":1}");
        JSONObject orderJson = new JSONObject();
        orderJson.put("name", order.getName());
        orderJson.put("client", order.getClient());
        orderJson.put("products", order.getProducts());

        channel.basicPublish("", TEST_ORDERS_QUEUE, null, orderJson.toString().getBytes());

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       com.rabbitmq.client.AMQP.BasicProperties properties, byte[] body)
                    throws IOException {
                response[0] = new String(body);
            }
        };
        channel.basicConsume(TEST_RESPONSE_QUEUE, true, consumer);

        Thread.sleep(1000);

        assertEquals("RESERVED", response[0]);
    }

}
