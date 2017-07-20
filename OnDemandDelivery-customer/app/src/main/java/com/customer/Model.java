package com.customer;


public class Model {

    private String name, itemPrice, finalPrice,quantity;

    private int icon;

    int getIcon() {
        return icon;
    }

    String getQuantity() {
        return quantity;
    }

    public Model(String name, String quantity, String itemPrice, String finalPrice, int icon) {
        this.name = name;
        this.itemPrice = itemPrice;
        this.finalPrice = finalPrice;
        this.icon = icon;

        this.quantity = quantity;

    }

    public String getName() {
        return name;
    }


    String getItemPrice() {
        return itemPrice;
    }


    String getFinalPrice() {
        return finalPrice;
    }

}
