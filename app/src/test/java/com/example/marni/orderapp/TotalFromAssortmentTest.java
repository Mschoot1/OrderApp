package com.example.marni.orderapp;

import com.example.marni.orderapp.BusinessLogic.TotalFromAssortment;
import com.example.marni.orderapp.Domain.Product;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class TotalFromAssortmentTest {

    private ArrayList<Product> products = new ArrayList<>();
    private double price;
    private int quantity;

    public TotalFromAssortmentTest() {
        Product p = new Product();
        p.setPrice(1.0);
        p.setQuantity(1);
        products.add(p);

        price = 1;
        quantity = 1;
    }

    @Test
    public void totalFromAssortment_correctPriceTotal_isCorrect() throws Exception {
        assertEquals(TotalFromAssortment.getPriceTotal(products), price, 0.01);
    }

    @Test
    public void totalFromAssortment_correctQuantity_isCorrect() throws Exception {
        assertEquals(TotalFromAssortment.getQuanitity(products), quantity);
    }
}