package com.example.marni.orderapp.Presentation.Activities;

import android.content.Intent;
import android.media.Image;
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
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.auth0.android.jwt.JWT;
import com.example.marni.orderapp.BusinessLogic.TotalFromAssortment;
import com.example.marni.orderapp.DataAccess.Account.AccountGetTask;
import com.example.marni.orderapp.DataAccess.Orders.OrdersGetCurrentTask;
import com.example.marni.orderapp.DataAccess.Orders.OrdersPutTask;
import com.example.marni.orderapp.DataAccess.Product.ProductsGetTask;
import com.example.marni.orderapp.DataAccess.Product.ProductsPostTask;
import com.example.marni.orderapp.DataAccess.Product.ProductsPutTask;
import com.example.marni.orderapp.Domain.Account;
import com.example.marni.orderapp.Domain.Order;
import com.example.marni.orderapp.Domain.Product;
import com.example.marni.orderapp.Presentation.Adapters.ProductsListViewAdapter;
import com.example.marni.orderapp.BusinessLogic.DrawerMenu;
import com.example.marni.orderapp.R;
import com.example.marni.orderapp.cardemulation.AccountStorage;

import java.text.DecimalFormat;
import java.util.ArrayList;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

import static com.example.marni.orderapp.Presentation.Activities.LogInActivity.JWT_STR;
import static com.example.marni.orderapp.Presentation.Activities.LogInActivity.USER;
import static com.example.marni.orderapp.Presentation.Activities.MyOrderActivity.setAnimation;
import static com.example.marni.orderapp.Presentation.Activities.OrderHistoryActivity.ORDER;

public class ProductsActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        TotalFromAssortment.OnTotalChanged, ProductsGetTask.OnProductAvailable,
        AccountGetTask.OnBalanceAvailable, OrdersGetCurrentTask.OnCurrentOrderAvailable,
        ProductsPutTask.SuccessListener, ProductsPostTask.SuccessListener, OrdersPutTask.PutSuccessListener {

    private final String TAG = getClass().getSimpleName();

    private ArrayList<Product> products = new ArrayList<>();
    private ProductsListViewAdapter mAdapter;
    private StickyListHeadersListView stickyList;

    private TextView textview_balance;
    private TextView account_email;

    private Order order = null;

    private TotalFromAssortment tfa;

    private JWT jwt;
    private int user;

    private int quantity;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        Bundle bundle = getIntent().getExtras();
        jwt = bundle.getParcelable(JWT_STR);
        user = bundle.getInt(USER);

        // hide title
        getSupportActionBar().setTitle("Assortment");
        toolbar.findViewById(R.id.toolbar_balance).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TopUpActivity.class);
                intent.putExtra(JWT_STR, jwt);
                intent.putExtra(USER, user);
                startActivity(intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_assortment);

        textview_balance = (TextView) findViewById(R.id.toolbar_balance);
        account_email = (TextView) headerView.findViewById(R.id.nav_email);

        getBalance("https://mysql-test-p4.herokuapp.com/account/" + user);
        getCurrentOrder("https://mysql-test-p4.herokuapp.com/order/current/" + user);
        getProducts("https://mysql-test-p4.herokuapp.com/products/" + user);

        stickyList = (StickyListHeadersListView) findViewById(R.id.listViewProducts);
        stickyList.setAreHeadersSticky(true);
        stickyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Product p = products.get(position);
                if (p.getQuantity() == 0) {
                    p.setQuantity(increase(p.getQuantity()));
                    postProduct("https://mysql-test-p4.herokuapp.com/product/quantity/add", p);
                } else {
                    p.setQuantity(increase(p.getQuantity()));
                    putProduct("https://mysql-test-p4.herokuapp.com/product/quantity/edit", p);
                }
                mAdapter.notifyDataSetChanged();

                tfa = new TotalFromAssortment(products);
                onTotalChanged(tfa.getPriceTotal(), tfa.getQuanitity());
                putOrderPrice("https://mysql-test-p4.herokuapp.com/order/price/edit", tfa.getPriceTotal());
            }
        });

        TextView textView = (TextView) findViewById(R.id.text_view_view_order);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MyOrderActivity.class);
                intent.putExtra(JWT_STR, jwt);
                intent.putExtra(USER, user);
                intent.putExtra(ORDER, order);
                startActivity(intent);
            }
        });
    }

    private int increase(int quantity) {
        return quantity + 1;
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

        new DrawerMenu(getApplicationContext(), id, jwt, user);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onTotalChanged(Double priceTotal, int quantity) {

        DecimalFormat formatter = new DecimalFormat("#0.00");

        TextView textViewTotal = (TextView) findViewById(R.id.textViewTotal);
        TextView textViewQuantity = (TextView) findViewById(R.id.textViewTotalQuantity);

        String t = "€" + formatter.format(priceTotal);
        String q = quantity + "";

        textViewTotal.setText(t);
        textViewQuantity.setText(q);

        if (this.quantity < quantity) {
            setAnimation(getApplicationContext(), (ImageView) findViewById(R.id.imageView_products_cart));
        }
        this.quantity = quantity;
    }

    public void getProducts(String ApiUrl) {
        String[] urls = new String[]{ApiUrl, jwt.toString()};
        ProductsGetTask task = new ProductsGetTask(this, "assortment");
        task.execute(urls);
    }

    @Override
    public void onProductAvailable(Product product) {
        products.add(product);
        tfa = new TotalFromAssortment(products);
        onTotalChanged(tfa.getPriceTotal(), tfa.getQuanitity());
        mAdapter.notifyDataSetChanged();
    }

    public void getBalance(String apiUrl) {
        String[] urls = new String[]{apiUrl, jwt.toString()};
        AccountGetTask getBalance = new AccountGetTask(this);
        getBalance.execute(urls);
    }

    @Override
    public void onBalanceAvailable(Account bal) {
        DecimalFormat formatter = new DecimalFormat("#0.00");
        double current_balance = bal.getBalance();
        textview_balance.setText("€ " + formatter.format(current_balance));
        account_email.setText(bal.getEmail());
    }

    private void getCurrentOrder(String apiUrl) {
        String[] urls = new String[]{apiUrl, jwt.toString()};
        OrdersGetCurrentTask task = new OrdersGetCurrentTask(this);
        task.execute(urls);
    }

    @Override
    public void onCurrentOrderAvailable(Order order) {
        this.order = order;
        mAdapter = new ProductsListViewAdapter(getApplicationContext(), getLayoutInflater(), products, order, jwt, user, this);
        stickyList.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    public void putOrderPrice(String apiUrl, Double priceTotal) {
        String[] urls = new String[]{apiUrl, jwt.toString(), priceTotal + "", Integer.toString(order.getOrderId())};
        OrdersPutTask task = new OrdersPutTask(this);
        task.execute(urls);
    }

    private void putProduct(String apiUrl, Product p) {
        String[] urls = new String[]{apiUrl, jwt.toString(), Integer.toString(order.getOrderId()), p.getProductId() + "", user + "", p.getQuantity() + ""};
        ProductsPutTask task = new ProductsPutTask(this);
        task.execute(urls);
    }

    private void postProduct(String apiUrl, Product p) {
        String[] urls = new String[]{apiUrl, jwt.toString(), Integer.toString(order.getOrderId()), p.getProductId() + "", user + "", p.getQuantity() + ""};
        ProductsPostTask task = new ProductsPostTask(this);
        task.execute(urls);
    }

    @Override
    public void successful(Boolean successful) {
        if (successful) {
            Log.i(TAG, "Product amount changed");
        } else {
            Toast.makeText(this, "Product amount couldn't be changed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void putSuccessful(Boolean successful) {
        if (successful) {
            Log.i(TAG, "Totalprice succesfully edited");
        } else {
            Log.i(TAG, "Error while updating totalprice");
        }
    }
}

