package com.kursinis.prif4kursinis.fxControllers.userWindowControllers;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class OrderViewModel {
    private SimpleIntegerProperty id;
    private SimpleStringProperty totalPrice;
    private SimpleIntegerProperty quantity;
    private SimpleStringProperty managerName;
    private SimpleStringProperty status;

    public OrderViewModel(int id, double totalPrice, int quantity, String managerName, String status) {
        this.id = new SimpleIntegerProperty(id);
        this.totalPrice = new SimpleStringProperty("$" + totalPrice);
        this.quantity = new SimpleIntegerProperty(quantity);
        this.managerName = new SimpleStringProperty(managerName);
        this.status = new SimpleStringProperty(status);
    }

    // Getters and setters for each property
    public int getId() { return id.get(); }
    public void setId(int value) { id.set(value); }
    public String getTotalPrice() { return totalPrice.get(); }
    public void setTotalPrice(double value) { totalPrice.set("$" + value); }

    public int getQuantity() { return quantity.get(); }
    public void setQuantity(int value) { quantity.set(value); }

    public String getManagerName() { return managerName.get(); }
    public void setManagerName(String value) { managerName.set(value); }

    public String getStatus() { return status.get(); }
    public void setStatus(String value) { status.set(value); }
}
