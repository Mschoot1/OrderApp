package com.example.marni.orderapp.service;

public class Config {

    private Config() {
        // empty constructor
    }

    public static final String URL_LOGIN = "http://mysql-test-p4.herokuapp.com/loginAuth";
    public static final String URL_CURRENT_ORDER = "http://mysql-test-p4.herokuapp.com/order/current/";
    public static final String URL_PUT_PRICE_ORDER = "http://mysql-test-p4.herokuapp.com/order/price/edit";
    public static final String URL_PRODUCTS = "http://mysql-test-p4.herokuapp.com/products/order/";
}
