package com.example.marni.orderapp.Presentation.Activities;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.marni.orderapp.BusinessLogic.TotalFromAssortment;
import com.example.marni.orderapp.DataAccess.CategoriesTask;
import com.example.marni.orderapp.DataAccess.ProductsTask;
import com.example.marni.orderapp.Domain.Category;
import com.example.marni.orderapp.Domain.Product;
import com.example.marni.orderapp.Presentation.Adapters.ProductsListviewAdapter;
import com.example.marni.orderapp.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class ProductsActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        TotalFromAssortment.OnTotalChanged, CategoriesTask.OnCategoryAvailable, ProductsTask.OnProductAvailable {

    private final String TAG = getClass().getSimpleName();

    private ArrayList<Category> categories = new ArrayList<>();
    private ArrayList<Product> products = new ArrayList<>();
    private ProductsListviewAdapter mAdapter;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        // hide title
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // set current menu item checked
        navigationView.setCheckedItem(R.id.nav_assortment);

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

        StickyListHeadersListView stickyList = (StickyListHeadersListView) findViewById(R.id.listViewProducts);
        stickyList.setAreHeadersSticky(true);
        stickyList.setFastScrollEnabled(true);
        stickyList.setFastScrollAlwaysVisible(true);

        mAdapter = new ProductsListviewAdapter(getApplicationContext(), getLayoutInflater(), products, this);

        stickyList.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
//            case R.id.action_settings:
//                // User chose the "Settings" item, show the app settings UI...
//                return true;
//
//            case R.id.action_favorite:
//                // User chose the "Favorite" action, mark the current item
//                // as a favorite...
//                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        Log.i(TAG, item.toString() + " clicked.");

        int id = item.getItemId();

        Intent intent;

        switch (id) {
            case R.id.nav_assortment:
                intent = new Intent(getApplicationContext(), ProductsActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_my_order:
//                intent = new Intent(getApplicationContext(), OrderActivity.class);
//                startActivity(intent);
                break;
            case R.id.nav_order_history:
                intent = new Intent(getApplicationContext(), OrderHistoryActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_top_up:
                intent = new Intent(getApplicationContext(), TopUpActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_allergy_information:
                intent = new Intent(getApplicationContext(), AllergiesActivity.class);
                startActivity(intent);
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onTotalChanged(Double priceTotal) {

        DecimalFormat formatter = new DecimalFormat("#0.00");

        TextView textViewTotal = (TextView) findViewById(R.id.textViewTotal);
        textViewTotal.setText("Total: € " + formatter.format(priceTotal));
    }

    public void getCategory(){
        String[] urls = new String[] { "https://androidtestapi.herokuapp.com/api/v1/categories"};

        CategoriesTask getCategory = new CategoriesTask(this);
        getCategory.execute(urls);
    }

    public void onCategoryAvailable(Category category){
        categories.clear();
        categories.add(category);

        for (Category c : categories) {

            getProduct(c.getId());

        }

        mAdapter.notifyDataSetChanged();
    }

    public void getProduct(int id){
        String[] urls = new String[] { "https://androidtestapi.herokuapp.com/api/v1/products/category/" + id};

        ProductsTask getProduct = new ProductsTask(this);
        getProduct.execute(urls);
    }

    public void onProductAvailable(Product product){
        products.clear();
        products.add(product);

        mAdapter.notifyDataSetChanged();
    }
}

