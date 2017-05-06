package com.example.marni.orderapp.Presentation.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.example.marni.orderapp.Domain.Product;
import com.example.marni.orderapp.Presentation.Adapters.ProductsListviewAdapter;
import com.example.marni.orderapp.R;

import java.util.ArrayList;
import java.util.List;

public class ProductsActivity extends AppCompatActivity {

    private ProductsListviewAdapter mAdapter;
    private int a;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        ListView listView = (ListView)findViewById(R.id.listViewProducts);
        mAdapter = new ProductsListviewAdapter(this);

        for (int i = 1; i <= 4; i++) {
            if (i % 2 == 1) {
                    a++;
                    mAdapter.addSectionHeaderItem("Category #" + a);
            }

            Product product = new Product("Product Item: " + i);
            product.setPrice(12);
            product.setSize(500);
            product.setAlcohol_percentage(5);

            mAdapter.addItem(product);
        }
        listView.setAdapter(mAdapter);

    }
}

