package com.example.marni.orderapp.domain;

/**
 * Created by MarcdenUil on 9-5-2017.
 */

public class Account {
    private double balance;
    private String email;

    public Account(double balance, String email) {
        this.balance = balance;
        this.email = email;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
