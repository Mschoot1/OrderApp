package com.example.marni.orderapp.BusinessLogic;

import com.example.marni.orderapp.Domain.Order;
import com.example.marni.orderapp.Domain.Product;

import java.util.ArrayList;

/**
 * Created by marni on 7-5-2017.
 */

public class TotalFromAssortment {

    private ArrayList<Product> products;

    public TotalFromAssortment(ArrayList<Product> products) {
        this.products = products;
    }

    public Double getPriceTotal() {

        double priceTotal = 0;

        for (Product product : products) {

            priceTotal += product.getQuantity()*product.getPrice();
        }

        return priceTotal;
    }

    public int getQuanitity() {

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
