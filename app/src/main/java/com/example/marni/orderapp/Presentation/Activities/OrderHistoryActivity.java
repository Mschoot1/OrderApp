package com.example.marni.orderapp.Presentation.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
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
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.marni.orderapp.DataAccess.Balance.BalanceGetTask;
import com.example.marni.orderapp.DataAccess.Orders.OrdersGetTask;
import com.example.marni.orderapp.Domain.Balance;
import com.example.marni.orderapp.Domain.Order;
import com.example.marni.orderapp.Presentation.Adapters.OrdersListviewAdapter;
import com.example.marni.orderapp.BusinessLogic.DrawerMenu;
import com.example.marni.orderapp.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class OrderHistoryActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        OrdersGetTask.OnOrderAvailable, BalanceGetTask.OnBalanceAvailable {

    private final String TAG = getClass().getSimpleName();

    public static final String ORDER = "ORDER";

    private BaseAdapter ordersAdapter;
    private double current_balance;
    private TextView textview_balance;

    private ArrayList<Order> orders = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        // hide title
        getSupportActionBar().setTitle("Home");
        toolbar.findViewById(R.id.toolbar_balance).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TopUpActivity.class);
                startActivity(intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // set current menu item checked
        navigationView.setCheckedItem(R.id.nav_order_history);

        textview_balance = (TextView)findViewById(R.id.toolbar_balance);

        getBalance();

        ListView listView = (ListView) findViewById(R.id.listViewOrders);

        ordersAdapter = new OrdersListviewAdapter(getApplicationContext(), getLayoutInflater(), orders);

        listView.setAdapter(ordersAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getApplicationContext(), OrderDetailActivity.class);

                intent.putExtra(ORDER, orders.get(position));
                startActivity(intent);
            }
        });

        getOrders("https://mysql-test-p4.herokuapp.com/orders/284");
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

        new DrawerMenu(getApplicationContext(), id);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void getOrders(String ApiUrl) {

        OrdersGetTask task = new OrdersGetTask(this);
        String[] urls = new String[]{ApiUrl};
        task.execute(urls);
    }

    @Override
    public void onOrderAvailable(Order order) {

        Log.i(TAG, "OrderId returned: " + order.getOrderId());

        orders.add(order);
        ordersAdapter.notifyDataSetChanged();
    }

    public void getBalance(){
        String[] urls = new String[] { "https://mysql-test-p4.herokuapp.com/balance/284" };

        BalanceGetTask getBalance = new BalanceGetTask(this);
        getBalance.execute(urls);
    }

    public void onBalanceAvailable(Balance bal){
        DecimalFormat formatter = new DecimalFormat("#0.00");

        current_balance = bal.getBalance();
        textview_balance.setText("€ " + formatter.format(current_balance));
    }
}
