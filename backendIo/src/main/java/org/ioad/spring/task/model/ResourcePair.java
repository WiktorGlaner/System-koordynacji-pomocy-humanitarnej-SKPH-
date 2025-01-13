package org.ioad.spring.task.model;


public class ResourcePair {
    private long id;
    private double quantity;

    public ResourcePair(long id, int value) {
        this.id = id;
        this.quantity = value;
    }

    public long getId() {
        return id;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "ResourcePair{id=" + id + ", value=" + quantity + '}';
    }
}

