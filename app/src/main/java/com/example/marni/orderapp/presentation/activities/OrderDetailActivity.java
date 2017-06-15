package com.example.marni.orderapp.presentation.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.example.marni.orderapp.businesslogic.TotalFromAssortment;
import com.example.marni.orderapp.dataaccess.account.AccountGetTask;
import com.example.marni.orderapp.dataaccess.orders.OrdersGetTask;
import com.example.marni.orderapp.dataaccess.product.ProductsGetTask;
import com.example.marni.orderapp.domain.Account;
import com.example.marni.orderapp.domain.Order;
import com.example.marni.orderapp.domain.Product;
import com.example.marni.orderapp.presentation.adapters.OrderDetailListViewAdapter;
import com.example.marni.orderapp.R;
import com.example.marni.orderapp.cardemulation.AccountStorage;

import java.text.DecimalFormat;
import java.util.ArrayList;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

import static com.example.marni.orderapp.presentation.activities.LoginActivity.JWT_STR;
import static com.example.marni.orderapp.presentation.activities.LoginActivity.USER;
import static com.example.marni.orderapp.presentation.activities.OrderHistoryActivity.ORDER;

public class OrderDetailActivity extends AppCompatActivity implements TotalFromAssortment.OnTotalChanged,
        ProductsGetTask.OnProductAvailable, AccountGetTask.OnBalanceAvailable, OrdersGetTask.OnOrderAvailable, ProductsGetTask.OnEmptyList {

    private StickyListHeadersListView stickyList;

    private ArrayList<Product> products = new ArrayList<>();
    private OrderDetailListViewAdapter mAdapter;

    private TextView textViewBalance;

    private String jwt;
    private int user;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        jwt = prefs.getString(JWT_STR, "");
        user = prefs.getInt(USER, 0);

        Bundle bundle = getIntent().getExtras();
        Order order = (Order) bundle.get(ORDER);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        AccountStorage.resetAccount(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.findViewById(R.id.toolbar_balance).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TopUpActivity.class);
                intent.putExtra(JWT_STR, jwt);
                intent.putExtra(USER, user);
                startActivity(intent);
            }
        });

        getSupportActionBar().setTitle("Order");

        stickyList = (StickyListHeadersListView) findViewById(R.id.listViewProducts);
        stickyList.setAreHeadersSticky(true);

        textViewBalance = (TextView) findViewById(R.id.toolbar_balance);

        getBalance("https://mysql-test-p4.herokuapp.com/account/" + user);
        getCurrentOrder("https://mysql-test-p4.herokuapp.com/order/current/" + user);
        getProducts("https://mysql-test-p4.herokuapp.com/products/order/" + order.getOrderId());
    }

    private void getCurrentOrder(String apiUrl) {

        OrdersGetTask task = new OrdersGetTask(this);
        String[] urls = new String[]{apiUrl, jwt};
        task.execute(urls);
    }

    @Override
    public void onOrderAvailable(Order order) {

        mAdapter = new OrderDetailListViewAdapter(getApplicationContext(), getLayoutInflater(), products);

        stickyList.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    public void getBalance(String apiUrl) {

        String[] urls = new String[]{apiUrl, jwt};
        AccountGetTask getBalance = new AccountGetTask(this);
        getBalance.execute(urls);
    }

    public void onBalanceAvailable(Account bal) {
        DecimalFormat formatter = new DecimalFormat("#0.00");

        double currentBalance = bal.getBalance();
        textViewBalance.setText("€ " + formatter.format(currentBalance));
    }

    public void getProducts(String apiUrl) {
        ProductsGetTask task = new ProductsGetTask(this, "myorder");
        String[] urls = new String[]{apiUrl, jwt};
        task.execute(urls);
    }

    @Override
    public void onProductAvailable(Product product) {

        products.add(product);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onTotalChanged(Double priceTotal, int quantity) {
        DecimalFormat formatter = new DecimalFormat("#0.00");

        TextView textViewTotal = (TextView) findViewById(R.id.textViewTotal);
        TextView textViewQuantity = (TextView) findViewById(R.id.textViewTotalQuantity);
        textViewTotal.setText("€ " + formatter.format(priceTotal));
        textViewQuantity.setText(Integer.toString(quantity));
    }

    @Override
    public void isEmpty(Boolean b) {
        // empty
    }
}
