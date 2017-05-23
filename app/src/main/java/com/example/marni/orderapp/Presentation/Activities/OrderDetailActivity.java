package com.example.marni.orderapp.Presentation.Activities;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.auth0.android.jwt.JWT;
import com.example.marni.orderapp.BusinessLogic.TotalFromAssortment;
import com.example.marni.orderapp.DataAccess.Account.AccountGetTask;
import com.example.marni.orderapp.DataAccess.Orders.OrdersGetTask;
import com.example.marni.orderapp.DataAccess.Product.ProductsGetTask;
import com.example.marni.orderapp.Domain.Account;
import com.example.marni.orderapp.Domain.Order;
import com.example.marni.orderapp.Domain.Product;
import com.example.marni.orderapp.Presentation.Adapters.ProductsListviewAdapter;
import com.example.marni.orderapp.R;
import com.example.marni.orderapp.cardemulation.AccountStorage;

import java.text.DecimalFormat;
import java.util.ArrayList;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

import static com.example.marni.orderapp.Presentation.Activities.LogInActivity.JWT_STR;
import static com.example.marni.orderapp.Presentation.Activities.LogInActivity.USER;
import static com.example.marni.orderapp.Presentation.Activities.OrderHistoryActivity.ORDER;

public class OrderDetailActivity extends AppCompatActivity implements TotalFromAssortment.OnTotalChanged,
        ProductsGetTask.OnProductAvailable, AccountGetTask.OnBalanceAvailable, OrdersGetTask.OnOrderAvailable, ProductsListviewAdapter.OnMethodAvailable {

    private final String TAG = getClass().getSimpleName();

    private StickyListHeadersListView stickyList;

    private ArrayList<Product> products = new ArrayList<>();
    private ProductsListviewAdapter mAdapter;

    private double current_balance;
    private TextView textview_balance;

    private Order order;
    private double priceTotal;

    private JWT jwt;
    private int user;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        Bundle bundle = getIntent().getExtras();
        jwt = bundle.getParcelable(JWT_STR);
        user = bundle.getInt(USER);
        order = (Order) bundle.get(ORDER);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

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

        textview_balance = (TextView) findViewById(R.id.toolbar_balance);

        getBalance("https://mysql-test-p4.herokuapp.com/account/" + user);
        getCurrentOrder("https://mysql-test-p4.herokuapp.com/order/current/" + user);
        getProducts("https://mysql-test-p4.herokuapp.com/products/order/" + order.getOrderId());

        if (savedInstanceState == null) {
            AccountStorage.SetAccount(this, "" + order.getOrderId());
        }
    }

    private void getCurrentOrder(String apiUrl) {

        OrdersGetTask task = new OrdersGetTask(this);
        String[] urls = new String[]{apiUrl, jwt.toString()};
        task.execute(urls);
    }

    @Override
    public void onOrderAvailable(Order order) {

        Boolean currentOrder = (order.getOrderId() == this.order.getOrderId());

        mAdapter = new ProductsListviewAdapter(getApplicationContext(), getLayoutInflater(), products, order, currentOrder, this, this, jwt, user);

        stickyList.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    public void getBalance(String ApiUrl) {

        String[] urls = new String[]{ApiUrl, jwt.toString()};
        AccountGetTask getBalance = new AccountGetTask(this);
        getBalance.execute(urls);
    }

    public void onBalanceAvailable(Account bal) {
        DecimalFormat formatter = new DecimalFormat("#0.00");

        current_balance = bal.getBalance();
        textview_balance.setText("€ " + formatter.format(current_balance));
    }

    public void getProducts(String ApiUrl) {

        ProductsGetTask task = new ProductsGetTask(this, "myorder");
        String[] urls = new String[]{ApiUrl, jwt.toString()};
        task.execute(urls);
    }

    @Override
    public void onProductAvailable(Product product) {

        products.add(product);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onTotalChanged(Double priceTotal, int quantity) {
        this.priceTotal = priceTotal;

        DecimalFormat formatter = new DecimalFormat("#0.00");

        TextView textViewTotal = (TextView) findViewById(R.id.textViewTotal);
        TextView textViewQuantity = (TextView) findViewById(R.id.textViewTotalQuantity);
        textViewTotal.setText("€ " + formatter.format(priceTotal));
        textViewQuantity.setText(quantity + "");
    }

    @Override
    public void onMethodAvailable(String method, Product product, Order order) {

    }
}
