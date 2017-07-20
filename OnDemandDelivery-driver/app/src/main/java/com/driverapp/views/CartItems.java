package com.driverapp.views;

/**
 * Created by selva on 6/7/17.
 */

public class CartItems {

    private String name, itemPrice, finalPrice,quantity;

    private int icon;

    public int getIcon() {
        return icon;
    }

    public String getQuantity() {
        return quantity;
    }

    public CartItems(String name, String quantity, String itemPrice, String finalPrice, int icon) {
        this.name = name;
        this.itemPrice = itemPrice;
        this.finalPrice = finalPrice;
        this.icon = icon;

        this.quantity = quantity;

    }

    public String getName() {
        return name;
    }


    public String getItemPrice() {
        return itemPrice;
    }


    public String getFinalPrice() {
        return finalPrice;
    }
}
