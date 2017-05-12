package com.example.marni.orderapp.Presentation.Activities;

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
import android.widget.Toast;

import com.example.marni.orderapp.BusinessLogic.TotalFromAssortment;
import com.example.marni.orderapp.DataAccess.Balance.BalanceGetTask;
import com.example.marni.orderapp.DataAccess.OrdersTask;
import com.example.marni.orderapp.DataAccess.Product.ProductsDeleteTask;
import com.example.marni.orderapp.DataAccess.Product.ProductsGetTask;
import com.example.marni.orderapp.DataAccess.Product.ProductsPostTask;
import com.example.marni.orderapp.DataAccess.Product.ProductsPutTask;
import com.example.marni.orderapp.Domain.Balance;
import com.example.marni.orderapp.Domain.Order;
import com.example.marni.orderapp.Domain.Product;
import com.example.marni.orderapp.Presentation.Adapters.ProductsListviewAdapter;
import com.example.marni.orderapp.BusinessLogic.DrawerMenu;
import com.example.marni.orderapp.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class ProductsActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        TotalFromAssortment.OnTotalChanged,
        ProductsGetTask.OnProductAvailable,
        BalanceGetTask.OnBalanceAvailable, OrdersTask.OnOrderAvailable, ProductsListviewAdapter.OnMethodAvailable,
        ProductsPutTask.SuccessListener, ProductsPostTask.SuccessListener, ProductsDeleteTask.SuccessListener{

    private final String TAG = getClass().getSimpleName();

    private ArrayList<Product> products = new ArrayList<>();
    private ProductsListviewAdapter mAdapter;
    private StickyListHeadersListView stickyList;

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

        textview_balance = (TextView) findViewById(R.id.toolbar_balance);

        getBalance();
        getCurrentOrder("https://mysql-test-p4.herokuapp.com/order/current/284");
        getProducts("https://mysql-test-p4.herokuapp.com/products/284");

        stickyList = (StickyListHeadersListView) findViewById(R.id.listViewProducts);
        stickyList.setAreHeadersSticky(true);
        stickyList.setFastScrollEnabled(true);
        stickyList.setFastScrollAlwaysVisible(true);
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

        new DrawerMenu(getApplicationContext(), id);

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

    public void getProducts(String ApiUrl) {

        String[] urls = new String[]{ApiUrl};
        ProductsGetTask task = new ProductsGetTask(this, "assortment");
        task.execute(urls);
    }

    @Override
    public void onProductAvailable(Product product) {

        products.add(product);
        mAdapter.getAllergyIcons(product);
        mAdapter.notifyDataSetChanged();
    }

    public void getBalance() {

        String[] urls = new String[] { "https://mysql-test-p4.herokuapp.com/balance/284" };
        BalanceGetTask getBalance = new BalanceGetTask(this);
        getBalance.execute(urls);
    }

    @Override
    public void onBalanceAvailable(Balance bal) {
        current_balance = bal.getBalance();
        textview_balance.setText("€ " + current_balance);
    }

    private void getCurrentOrder(String apiUrl) {

        String[] urls = new String[]{apiUrl};
        OrdersTask task = new OrdersTask(this);
        task.execute(urls);
    }

    @Override
    public void onOrderAvailable(Order order) {

        mAdapter = new ProductsListviewAdapter(getApplicationContext(), getLayoutInflater(), products, order, true, this, this);

        stickyList.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onMethodAvailable(String method, Product product, Order order){
        switch (method){
            case "put":
                String[] urls = new String[] { "https://mysql-test-p4.herokuapp.com/product/quantity/edit", Integer.toString(order.getOrderId()), Integer.toString(product.getProductId()), "284", Integer.toString(product.getQuantity()) };
                ProductsPutTask putProduct = new ProductsPutTask(this);
                putProduct.execute(urls);
                break;
            case "post":
                String[] urls2 = new String[] { "https://mysql-test-p4.herokuapp.com/product/quantity/add", Integer.toString(order.getOrderId()), Integer.toString(product.getProductId()), "284", Integer.toString(product.getQuantity()) };
                ProductsPostTask postProduct = new ProductsPostTask(this);
                postProduct.execute(urls2);
                break;
            case "delete":
                String[] urls3 = new String[] { "https://mysql-test-p4.herokuapp.com/product/quantity/delete", Integer.toString(order.getOrderId()), Integer.toString(product.getProductId()), "284" };
                ProductsDeleteTask deleteProduct = new ProductsDeleteTask(this);
                deleteProduct.execute(urls3);
        }
    }

    @Override
    public void successful(Boolean successful) {
        if (successful){
            Toast.makeText(this, "Product amount changed", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Product quantity couldn't be changed", Toast.LENGTH_SHORT).show();
        }
    }
}

