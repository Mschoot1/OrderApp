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
import com.example.marni.orderapp.DataAccess.Balance.BalanceGetTask;
import com.example.marni.orderapp.DataAccess.CategoriesTask;
import com.example.marni.orderapp.DataAccess.ProductsTask;
import com.example.marni.orderapp.Domain.Balance;
import com.example.marni.orderapp.Domain.Category;
import com.example.marni.orderapp.Domain.Product;
import com.example.marni.orderapp.Presentation.Adapters.ProductsListviewAdapter;
import com.example.marni.orderapp.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class ProductsActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        TotalFromAssortment.OnTotalChanged, CategoriesTask.OnCategoryAvailable, ProductsTask.OnProductAvailable,
        BalanceGetTask.OnBalanceAvailable {

    private final String TAG = getClass().getSimpleName();

    private ArrayList<Product> products = new ArrayList<>();
    private ArrayList<Category> categories = new ArrayList<>();
    private ProductsListviewAdapter mAdapter;

    private double current_balance;
    private TextView textview_balance;

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

        textview_balance = (TextView)findViewById(R.id.toolbar_balance);

        getBalance();

        StickyListHeadersListView stickyList = (StickyListHeadersListView) findViewById(R.id.listViewProducts);
        stickyList.setAreHeadersSticky(true);
        stickyList.setFastScrollEnabled(true);
        stickyList.setFastScrollAlwaysVisible(true);

        mAdapter = new ProductsListviewAdapter(getApplicationContext(), getLayoutInflater(), products, categories, this);

        stickyList.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        getCategories("");
        getProducts("");
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

    public void getCategories(String ApiUrl) {

        CategoriesTask task = new CategoriesTask(this);
        String[] urls = new String[]{ApiUrl};
        task.execute(urls);
    }

    @Override
    public void onCategoryAvailable(Category category){

        categories.add(category);
        mAdapter.notifyDataSetChanged();
    }

    public void getProducts(String ApiUrl) {

        ProductsTask task = new ProductsTask(this);
        String[] urls = new String[]{ApiUrl};
        task.execute(urls);
    }

    @Override
    public void onProductAvailable(Product product) {

        products.add(product);
//        mAdapter.getAllergyIcons(product);
        mAdapter.notifyDataSetChanged();
    }

    public void getBalance(){
        String[] urls = new String[] { "https://mysql-test-p4.herokuapp.com/balance/284" };

        BalanceGetTask getBalance = new BalanceGetTask(this);
        getBalance.execute(urls);
    }

    public void onBalanceAvailable(Balance bal){
        current_balance = bal.getBalance();
        textview_balance.setText("€ " + current_balance);
    }
}

