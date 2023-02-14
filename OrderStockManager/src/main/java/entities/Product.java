package entities;

public class Product {
    private String name;
    private int id;
    private int stock;

    public Product() {}

    public Product(String name, int id, int stock) {
        this.name = name;
        this.id = id;
        this.stock = stock;
    }

    public Product(String name, int stock) {
        this.name = name;
        this.stock = stock;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public boolean checkStock(int quantity) {
        return quantity <= this.stock;
    }

}
