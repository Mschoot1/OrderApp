package com.example.marni.orderapp.presentation.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.marni.orderapp.dataaccess.deviceinfo.DevicePutTask;
import com.example.marni.orderapp.dataaccess.account.AccountGetTask;
import com.example.marni.orderapp.dataaccess.orders.OrdersGetTask;
import com.example.marni.orderapp.domain.Account;
import com.example.marni.orderapp.domain.Order;
import com.example.marni.orderapp.presentation.adapters.OrdersListViewAdapter;
import com.example.marni.orderapp.presentation.DrawerMenu;
import com.example.marni.orderapp.R;
import com.example.marni.orderapp.cardemulation.AccountStorage;

import java.text.DecimalFormat;
import java.util.ArrayList;

import static com.example.marni.orderapp.presentation.activities.LoginActivity.JWT_STR;
import static com.example.marni.orderapp.presentation.activities.LoginActivity.USER;
import static com.example.marni.orderapp.presentation.activities.MyOrderActivity.setupDrawer;
import static com.example.marni.orderapp.presentation.activities.MyOrderActivity.setupToolbar;


public class OrderHistoryActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        OrdersGetTask.OnOrderAvailable, AccountGetTask.OnBalanceAvailable {

    private final String tag = getClass().getSimpleName();

    public static final String ORDER = "ORDER";

    private String jwt;
    private int user;

    private BaseAdapter ordersAdapter;
    private TextView textViewBalance;
    private TextView accountEmail;

    private ArrayList<Order> orders = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        setupToolbar(this, "Order History");
        setupDrawer(this);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        jwt = prefs.getString(JWT_STR, "");
        user = prefs.getInt(USER, 0);

        AccountStorage.resetAccount(this);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        navigationView.setCheckedItem(R.id.nav_order_history);
        textViewBalance = (TextView) findViewById(R.id.toolbar_balance);
        accountEmail = (TextView) headerView.findViewById(R.id.nav_email);

        ListView listView = (ListView) findViewById(R.id.listViewOrders);

        ordersAdapter = new OrdersListViewAdapter(getApplicationContext(), getLayoutInflater(), orders);

        listView.setAdapter(ordersAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent;
                if (orders.get(position).getStatus() == 0) {
                    intent = new Intent(getApplicationContext(), MyOrderActivity.class);
                } else {
                    intent = new Intent(getApplicationContext(), OrderDetailActivity.class);
                }
                intent.putExtra(ORDER, orders.get(position));
                startActivity(intent);
            }
        });

        getBalance("https://mysql-test-p4.herokuapp.com/account/" + user);
        getOrders("https://mysql-test-p4.herokuapp.com/orders/" + user);
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

        Log.i(tag, item.toString() + " clicked.");

        int id = item.getItemId();

        Log.i(tag, "user: " + user);

        new DrawerMenu(getApplicationContext(), id, jwt, user);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void getOrders(String apiUrl) {

        OrdersGetTask task = new OrdersGetTask(this);
        String[] urls = new String[]{apiUrl, jwt};
        task.execute(urls);
    }

    @Override
    public void onOrderAvailable(Order order) {

        Log.i(tag, "OrderId returned: " + order.getOrderId());

        orders.add(order);

        ordersAdapter.notifyDataSetChanged();
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
        accountEmail.setText(bal.getEmail());
    }
}
