package com.example.marni.orderapp;

import com.example.marni.orderapp.businesslogic.TotalFromAssortment;
import com.example.marni.orderapp.domain.Product;

import org.junit.Test;

import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;

public class TotalFromAssortmentTest implements TotalFromAssortment.OnTotalChanged {

    private ArrayList<Product> products = new ArrayList<>();
    private double priceTotal;
    private int quantity;

    public TotalFromAssortmentTest() {
        Product p = new Product();
        p.setPrice(1.0);
        p.setQuantity(1);
        products.add(p);
    }

    @Test
    public void totalFromAssortment_correctGetTotals_isCorrect() throws Exception {

        TotalFromAssortment tfa = new TotalFromAssortment(this);
        tfa.getTotals(products);

        assertThat(priceTotal, instanceOf(Double.class));
        assertThat(quantity, instanceOf(Integer.class));
        assertEquals(priceTotal, 1.0, 0.01);
        assertEquals(quantity, 1);
    }

    @Override
    public void onTotalChanged(Double priceTotal, int quantity) {
        this.priceTotal = priceTotal;
        this.quantity = quantity;
    }
}