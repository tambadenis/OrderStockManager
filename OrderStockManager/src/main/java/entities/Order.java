package entities;

import java.util.List;

public class Order {
    private List<Product> products;
    private String name;
    private String client;
    private String status;
    private int id;

    public Order() {}

    public Order(List<Product> products, String name, String client, String status, int id) {
        this.products = products;
        this.name = name;
        this.client = client;
        this.status = status;
        this.id = id;
    }

    public Order(String name, String client, String status) {
        this.name = name;
        this.client = client;
        this.status = status;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
