package com.example.marni.orderapp.Domain;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by marni on 8-5-2017.
 */

public class Order implements Serializable {

    private int orderId;
    private int Status;
    private String timestamp;
    private double price_total;

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public double getPriceTotal() {
        return price_total;
    }

    public void setPriceTotal(double price_total) {
        this.price_total = price_total;
    }
}
