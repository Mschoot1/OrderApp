package com.example.marni.orderapp.Presentation.Activities;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.example.marni.orderapp.BusinessLogic.TotalFromAssortment;
import com.example.marni.orderapp.DataAccess.Balance.BalanceGetTask;
import com.example.marni.orderapp.DataAccess.ProductsTask;
import com.example.marni.orderapp.Domain.Balance;
import com.example.marni.orderapp.Domain.Category;
import com.example.marni.orderapp.Domain.Order;
import com.example.marni.orderapp.Domain.Product;
import com.example.marni.orderapp.Presentation.Adapters.ProductsListviewAdapter;
import com.example.marni.orderapp.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

import static com.example.marni.orderapp.Presentation.Activities.OrderHistoryActivity.ORDER;

public class OrderDetailActivity extends AppCompatActivity implements
        TotalFromAssortment.OnTotalChanged,
        ProductsTask.OnProductAvailable, BalanceGetTask.OnBalanceAvailable {

    private final String TAG = getClass().getSimpleName();

    private ArrayList<Product> products = new ArrayList<>();
    private ProductsListviewAdapter mAdapter;

    private double current_balance;
    private TextView textview_balance;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        Bundle bundle = getIntent().getExtras();

        Order order = (Order) bundle.get(ORDER);

        TextView textViewOrderId = (TextView) findViewById(R.id.textViewOrderId);
        TextView textViewStatus = (TextView) findViewById(R.id.textViewStatus);
        TextView textViewDateTime = (TextView) findViewById(R.id.textViewDateTime);

        assert order != null;
        textViewOrderId.setText(order.getOrderId() + "");
        switch (order.getStatus()) {
            case 0:
                textViewStatus.setText(getResources().getString(R.string.open));
                break;
            default:
                textViewStatus.setText(getResources().getString(R.string.paid));
        }        textViewDateTime.setText(order.getTimestamp());

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        StickyListHeadersListView stickyList = (StickyListHeadersListView) findViewById(R.id.listViewProducts);
        stickyList.setAreHeadersSticky(true);
        stickyList.setFastScrollEnabled(true);
        stickyList.setFastScrollAlwaysVisible(true);

        mAdapter = new ProductsListviewAdapter(getApplicationContext(), getLayoutInflater(), products, this);

        stickyList.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        textview_balance = (TextView) findViewById(R.id.toolbar_balance);

        getBalance("https://mysql-test-p4.herokuapp.com/balance/284");
        getProducts("https://mysql-test-p4.herokuapp.com/products/284");
    }

    public void getBalance(String ApiUrl) {

        String[] urls = new String[]{ApiUrl};
        BalanceGetTask getBalance = new BalanceGetTask(this);
        getBalance.execute(urls);
    }

    public void onBalanceAvailable(Balance bal) {
        current_balance = bal.getBalance();
        textview_balance.setText("€ " + current_balance);
    }

    public void getProducts(String ApiUrl) {

        ProductsTask task = new ProductsTask(this);
        String[] urls = new String[]{ApiUrl};
        task.execute(urls);
    }

    @Override
    public void onProductAvailable(Product product) {

        products.add(product);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onTotalChanged(Double priceTotal) {

        DecimalFormat formatter = new DecimalFormat("#0.00");

        TextView textViewTotal = (TextView) findViewById(R.id.textViewTotal);
        textViewTotal.setText("Total: € " + formatter.format(priceTotal));
    }
}
