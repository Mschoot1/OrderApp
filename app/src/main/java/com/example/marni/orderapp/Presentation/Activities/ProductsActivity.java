package com.example.marni.orderapp.Presentation.Activities;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.marni.orderapp.Domain.Product;
import com.example.marni.orderapp.Presentation.Adapters.ProductsListviewAdapter;
import com.example.marni.orderapp.R;

import java.util.ArrayList;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class ProductsActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        ArrayList<Product> products = new ArrayList<>();

        // dummy data
        Product product;

        product = new Product();
        product.setName("Cola");
        product.setCategory("Non Alcoholic");
        product.setPrice(2.0);
        product.setSize(300);

        for (int i = 0; i < 10; i++) {

            products.add(product);
        }

        product = new Product();
        product.setName("Wine");
        product.setCategory("Alcoholic");
        product.setPrice(3.5);
        product.setAlcohol_percentage(12.0);
        product.setSize(150);
        products.add(product);

        for (int i = 0; i < 10; i++) {

            products.add(product);
        }
        // end

        StickyListHeadersListView stickyList = (StickyListHeadersListView) findViewById(R.id.listViewProducts);
        stickyList.setAreHeadersSticky(true);
        stickyList.setFastScrollEnabled(true);
        stickyList.setFastScrollAlwaysVisible(true);

        ProductsListviewAdapter productsAdapter = new ProductsListviewAdapter(getLayoutInflater(), products);

        stickyList.setAdapter(productsAdapter);
        productsAdapter.notifyDataSetChanged();
    }
}

