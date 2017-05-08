package com.example.marni.orderapp.Presentation.Activities;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.example.marni.orderapp.BusinessLogic.TotalFromAssortment;
import com.example.marni.orderapp.Domain.Product;
import com.example.marni.orderapp.Presentation.Adapters.ProductsListviewAdapter;
import com.example.marni.orderapp.R;

import java.util.ArrayList;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class OrderDetailActivity extends AppCompatActivity implements
        TotalFromAssortment.OnTotalChanged {

    private ArrayList<Product> products = new ArrayList<>();
    private ProductsListviewAdapter mAdapter;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        StickyListHeadersListView stickyList = (StickyListHeadersListView) findViewById(R.id.listViewProducts);
        stickyList.setAreHeadersSticky(true);
        stickyList.setFastScrollEnabled(true);
        stickyList.setFastScrollAlwaysVisible(true);

        // dummy data
        Product product;

        for (int i = 0; i < 10; i++) {

            product = new Product();
            product.setName("Cola");
            product.setCategory("Non Alcoholic");
            product.setCategoryid(0);
            product.setPrice(2.0);
            product.setSize(300);
            products.add(product);
        }

        for (int i = 0; i < 10; i++) {

            product = new Product();
            product.setName("Wine");
            product.setCategory("Alcoholic");
            product.setCategoryid(1);
            product.setPrice(3.5);
            product.setAlcohol_percentage(12.0);
            product.setSize(150);
            products.add(product);
        }
        // end

        mAdapter = new ProductsListviewAdapter(getApplicationContext(), getLayoutInflater(), products, this);

        stickyList.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onTotalChanged(Double priceTotal) {


    }
}
