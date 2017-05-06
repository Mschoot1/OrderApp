package com.example.marni.orderapp.Presentation.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.example.marni.orderapp.DataAccess.CategoriesTask;
import com.example.marni.orderapp.DataAccess.ProductsTask;
import com.example.marni.orderapp.Domain.Category;
import com.example.marni.orderapp.Domain.Product;
import com.example.marni.orderapp.Presentation.Adapters.ProductsListviewAdapter;
import com.example.marni.orderapp.R;

import java.util.ArrayList;

public class ProductsActivity extends AppCompatActivity implements CategoriesTask.OnCategoryAvailable, ProductsTask.OnProductAvailable {

    private ProductsListviewAdapter mAdapter;
    private ArrayList<Category> categories = new ArrayList<>();
    private ArrayList<Product> products = new ArrayList<>();
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        listView = (ListView) findViewById(R.id.listViewProducts);
        mAdapter = new ProductsListviewAdapter(this);

        getCategory();

        listView.setAdapter(mAdapter);
    }

    public void getCategory(){
        String[] urls = new String[] { "https://androidtestapi.herokuapp.com/api/v1/categories"};

        CategoriesTask getCategory = new CategoriesTask(this);
        getCategory.execute(urls);
    }

    public void getProduct(int id){
        String[] urls = new String[] { "https://androidtestapi.herokuapp.com/api/v1/products/category/" + id};

        ProductsTask getProduct = new ProductsTask(this);
        getProduct.execute(urls);
    }

    public void onCategoryAvailable(Category category){
        categories.clear();
        categories.add(category);

       for (Category c : categories) {

           getProduct(c.getId());
           mAdapter.addSectionHeaderItem(c.getCategoryname());

       }

        mAdapter.notifyDataSetChanged();
    }

    public void onProductAvailable(Product product){
        products.clear();
        products.add(product);

        //mAdapter.addItem(product);

        mAdapter.notifyDataSetChanged();
    }

}



