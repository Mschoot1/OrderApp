package com.example.marni.orderapp.businesslogic;

import com.example.marni.orderapp.domain.Product;

import java.util.List;

public class TotalFromAssortment {

    public static Double getPriceTotal(List<Product> products) {

        double priceTotal = 0;

        for (Product product : products) {

            priceTotal += product.getQuantity()*product.getPrice();
        }

        return priceTotal;
    }

    public static int getQuanitity(List<Product> products) {

        int quantity = 0;

        for (Product product : products) {

            quantity += product.getQuantity();
        }

        return quantity;
    }

    public interface OnTotalChanged {

        void onTotalChanged(Double priceTotal, int quantity);
    }
}
