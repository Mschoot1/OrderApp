package com.example.marni.orderapp.BusinessLogic;

import com.example.marni.orderapp.Domain.Product;

import java.util.ArrayList;

/**
 * Created by marni on 7-5-2017.
 */

public class TotalFromAssortment {

    private ArrayList<ArrayList<Product>> products;

    private double priceTotal = 0;

    public TotalFromAssortment(ArrayList<ArrayList<Product>> products) {

        this.products = products;
    }

    public Double getPriceTotal() {

        for (ArrayList<Product> specificProducts : products) {

            for (Product product : specificProducts) {

                priceTotal += product.getPrice();
            }
        }

        return priceTotal;
    }

    public interface OnTotalChanged {

        void onTotalChanged(Double priceTotal);
    }
}
