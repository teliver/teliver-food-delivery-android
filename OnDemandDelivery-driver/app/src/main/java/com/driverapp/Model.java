package com.driverapp;

/**
 * Created by selva on 29/6/17.
 */

public class Model {

    private String name, mobile, address, orderId;

    public Model(String name, String mobile, String address, String orderId) {
        this.name = name;
        this.mobile = mobile;
        this.address = address;
        this.orderId = orderId;
    }

    public String getName() {
        return name;
    }

    public String getMobile() {
        return mobile;
    }

    public String getAddress() {
        return address;
    }

    public String getOrderId() {
        return orderId;
    }
}
