package com.example.marni.orderapp.Presentation.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.auth0.android.jwt.JWT;
import com.example.marni.orderapp.Presentation.DrawerMenu;
import com.example.marni.orderapp.BusinessLogic.TotalFromAssortment;
import com.example.marni.orderapp.DataAccess.Account.AccountGetTask;
import com.example.marni.orderapp.DataAccess.Orders.OrdersGetTask;
import com.example.marni.orderapp.DataAccess.Orders.OrdersPutTask;
import com.example.marni.orderapp.DataAccess.Product.ProductsGetTask;
import com.example.marni.orderapp.DataAccess.Product.ProductsPostTask;
import com.example.marni.orderapp.DataAccess.Product.ProductsPutTask;
import com.example.marni.orderapp.Domain.Account;
import com.example.marni.orderapp.Domain.Order;
import com.example.marni.orderapp.Domain.Product;
import com.example.marni.orderapp.Presentation.Adapters.MyOrderListViewAdapter;
import com.example.marni.orderapp.Presentation.Fragments.CategoryFragment;
import com.example.marni.orderapp.R;
import com.example.marni.orderapp.cardemulation.AccountStorage;

import java.text.DecimalFormat;
import java.util.ArrayList;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

import static com.example.marni.orderapp.Presentation.Activities.LogInActivity.JWT_STR;
import static com.example.marni.orderapp.Presentation.Activities.LogInActivity.USER;
import static com.example.marni.orderapp.Presentation.Activities.OrderHistoryActivity.ORDER;
import static com.example.marni.orderapp.cardemulation.CardService.PENDING_NUMBER_OPEN;
import static com.example.marni.orderapp.cardemulation.CardService.PENDING_NUMBER_PENDING;
import static com.example.marni.orderapp.cardemulation.CardService.PREF_PENDING_NUMBER;

public class MyOrderActivity extends AppCompatActivity implements CategoryFragment.OnItemSelected,
        TotalFromAssortment.OnTotalChanged,
        ProductsGetTask.OnProductAvailable, AccountGetTask.OnBalanceAvailable, OrdersGetTask.OnOrderAvailable,
        ProductsPutTask.SuccessListener, ProductsPostTask.SuccessListener,
        OrdersPutTask.PutSuccessListener, NavigationView.OnNavigationItemSelectedListener, ProductsGetTask.OnEmptyList, SharedPreferences.OnSharedPreferenceChangeListener {

    private final String TAG = getClass().getSimpleName();

    private StickyListHeadersListView stickyList;

    private ArrayList<Product> products = new ArrayList<>();
    public MyOrderListViewAdapter mAdapter;

    private double current_balance;

    private TextView textview_balance;
    private TextView account_email;

    private Order order;

    private String jwt;
    private int user;
    private int quantity;

    private SharedPreferences prefs;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        isEmpty(false);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);
        prefs.unregisterOnSharedPreferenceChangeListener(this);
        prefs.edit().putString(PREF_PENDING_NUMBER, PENDING_NUMBER_OPEN);
        prefs.registerOnSharedPreferenceChangeListener(this);

        jwt = prefs.getString(JWT_STR, "");
        user = prefs.getInt(USER, 0);

        NfcAdapter mNfcAdapter = NfcAdapter.getDefaultAdapter(this);


        if (mNfcAdapter != null) {
            if (!mNfcAdapter.isEnabled()) {
                Toast.makeText(this, "Please activate NFC and press Back to return to the application!", Toast.LENGTH_LONG).show();
                startActivity(new Intent(android.provider.Settings.ACTION_NFC_SETTINGS));
            }
        } else {
            Log.i(TAG, "Nfc adapter isn't working correctly");
        }


        getSupportActionBar().setTitle("My Order");
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
        navigationView.setCheckedItem(R.id.nav_my_order);

        ImageView imageView = (ImageView) findViewById(R.id.additem_orderdetail);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ProductsActivity.class);
                intent.putExtra(JWT_STR, jwt);
                intent.putExtra(USER, user);
                startActivity(intent);
            }
        });

        stickyList = (StickyListHeadersListView) findViewById(R.id.listViewProducts);
        stickyList.setAreHeadersSticky(true);
        stickyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, "Clicked item: " + position);
                Product p = products.get(position);
                if (p.getQuantity() == 0) {
                    p.setQuantity(increase(p.getQuantity()));
                    postProduct("https://mysql-test-p4.herokuapp.com/product/quantity/add", p);
                } else {
                    p.setQuantity(increase(p.getQuantity()));
                    putProduct("https://mysql-test-p4.herokuapp.com/product/quantity/edit", p);
                }
                mAdapter.notifyDataSetChanged();
                onTotalChanged(TotalFromAssortment.getPriceTotal(products), TotalFromAssortment.getQuanitity(products));
                putOrderPrice("https://mysql-test-p4.herokuapp.com/order/price/edit", TotalFromAssortment.getPriceTotal(products));
            }
        });

        textview_balance = (TextView) findViewById(R.id.toolbar_balance);
        account_email = (TextView) headerView.findViewById(R.id.nav_email);

        getBalance("https://mysql-test-p4.herokuapp.com/account/" + user);
        getCurrentOrder("https://mysql-test-p4.herokuapp.com/order/current/" + user);
    }

    private void getCurrentOrder(String apiUrl) {

        OrdersGetTask task = new OrdersGetTask(this);
        String[] urls = new String[]{apiUrl, jwt};
        task.execute(urls);
    }

    @Override
    public void onOrderAvailable(Order order) {
        this.order = order;
        getProducts("https://mysql-test-p4.herokuapp.com/products/order/" + order.getOrderId());
    }

    public void getBalance(String ApiUrl) {
        String[] urls = new String[]{ApiUrl, prefs.getString(JWT_STR, "")};
        AccountGetTask getBalance = new AccountGetTask(this);
        getBalance.execute(urls);
    }

    @Override
    public void onBalanceAvailable(Account bal) {
        DecimalFormat formatter = new DecimalFormat("#0.00");
        current_balance = bal.getBalance();
        String s = "€" + formatter.format(current_balance);
        textview_balance.setText(s);
        account_email.setText(bal.getEmail());
    }

    public void getProducts(String ApiUrl) {
        ProductsGetTask task = new ProductsGetTask(this, "myorder");
        String[] urls = new String[]{ApiUrl, jwt};
        task.execute(urls);
    }

    @Override
    public void onProductAvailable(Product product) {
        products.add(product);
        onTotalChanged(TotalFromAssortment.getPriceTotal(products), TotalFromAssortment.getQuanitity(products));

        mAdapter = new MyOrderListViewAdapter(this, getLayoutInflater(), products, order, jwt, user, this);
        stickyList.setAdapter(mAdapter);

        onTotalChanged(TotalFromAssortment.getPriceTotal(products), TotalFromAssortment.getQuanitity(products));
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onTotalChanged(Double priceTotal, int quantity) {

        DecimalFormat formatter = new DecimalFormat("#0.00");

        TextView textViewTotal = (TextView) findViewById(R.id.textViewTotal);
        TextView textViewQuantity = (TextView) findViewById(R.id.textViewTotalQuantity);
        String sPriceTotal;
        if (priceTotal == 0) {
            sPriceTotal = "€" + formatter.format(0);
        } else {
            sPriceTotal = "€" + formatter.format(priceTotal);
        }
        textViewTotal.setText(sPriceTotal);

        String sQuantity;
        if (quantity == 0) {
            sQuantity = "0";
        } else {
            sQuantity = quantity + "";
        }
        textViewQuantity.setText(sQuantity);

        AccountStorage.SetAccount(this, "" + order.getOrderId(), current_balance, priceTotal, order.getPending());

        if (this.quantity < quantity) {
            setAnimation(getApplicationContext(), (ImageView) findViewById(R.id.imageView_orderdetail_cart));
        }
        this.quantity = quantity;
    }

    public static void setAnimation(Context context, ImageView imageView) {
        Animation animation;
        animation = AnimationUtils.loadAnimation(context, R.anim.wiggle);
        imageView.startAnimation(animation);
    }

    public void putOrderPrice(String apiUrl, Double priceTotal) {
        String[] urls = new String[]{apiUrl, jwt, priceTotal + "", Integer.toString(order.getOrderId())};
        OrdersPutTask task = new OrdersPutTask(this);
        task.execute(urls);
    }

    private void putProduct(String apiUrl, Product p) {
        String[] urls = new String[]{apiUrl, jwt, Integer.toString(order.getOrderId()), p.getProductId() + "", user + "", p.getQuantity() + ""};
        ProductsPutTask task = new ProductsPutTask(this);
        task.execute(urls);
    }

    private void postProduct(String apiUrl, Product p) {
        String[] urls = new String[]{apiUrl, jwt, Integer.toString(order.getOrderId()), p.getProductId() + "", user + "", p.getQuantity() + ""};
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

        new DrawerMenu(getApplicationContext(), id, jwt, user);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private int increase(int quantity) {
        return quantity + 1;
    }

    @Override
    public void isEmpty(Boolean b) {
        TextView textView = (TextView) findViewById(R.id.textView_add_items);
        ImageView imageView = (ImageView) findViewById(R.id.imageViewArrow);
        if (b) {
            textView.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.INVISIBLE);
            imageView.setVisibility(View.INVISIBLE);
        }
    }

    public void mAdapterNotifyDataSetChanged() {

    }

    @Override
    public void onItemSelected(int i) {
        int j = 0;

        for(Product p : products){
            j++;
            Log.i(TAG, "j: " + j);
            Log.i(TAG, "i: " + i);
            if(p.getCategoryId()==i){
                stickyList.setSelection(j);
                break;
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.i(TAG, "prefs.getString(" + key + ", defaultvalue): " + prefs.getString(key, "defaultvalue"));
        if (prefs.getString(PREF_PENDING_NUMBER, "").equals(PENDING_NUMBER_PENDING)) {
            Intent intent = new Intent(getApplicationContext(), PaymentPendingActivity.class);
            startActivity(intent);
            Log.i(TAG, "prefs.getString(" + key + ", defaultvalue): " + prefs.getString(key, "defaultvalue"));
        }
    }
}
