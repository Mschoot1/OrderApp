package com.example.marni.orderapp.businesslogic;

import com.example.marni.orderapp.domain.Product;

import java.util.List;

public class TotalFromAssortment {

    private OnTotalChanged listener;

    public TotalFromAssortment(OnTotalChanged listener) {
        this.listener = listener;
    }

    public void getTotals(List<Product> products) {
        double priceTotal = 0;
        for (Product product : products) {
            priceTotal += product.getQuantity()*product.getPrice();
        }
        int quantity = 0;
        for (Product product : products) {
            quantity += product.getQuantity();
        }
        listener.onTotalChanged(priceTotal, quantity);
    }

    public interface OnTotalChanged {
        void onTotalChanged(Double priceTotal, int quantity);
    }
}
