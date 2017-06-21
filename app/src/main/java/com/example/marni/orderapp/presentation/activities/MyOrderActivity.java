package com.example.marni.orderapp.presentation.activities;

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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marni.orderapp.dataaccess.MyOrderActivityRequests;
import com.example.marni.orderapp.dataaccess.deviceinfo.DevicePutTask;
import com.example.marni.orderapp.dataaccess.product.ProductsGetTask;
import com.example.marni.orderapp.presentation.DrawerMenu;
import com.example.marni.orderapp.businesslogic.TotalFromAssortment;
import com.example.marni.orderapp.dataaccess.account.AccountGetTask;
import com.example.marni.orderapp.dataaccess.orders.OrdersPutTask;
import com.example.marni.orderapp.dataaccess.product.ProductsPostTask;
import com.example.marni.orderapp.dataaccess.product.ProductsPutTask;
import com.example.marni.orderapp.domain.Account;
import com.example.marni.orderapp.domain.Order;
import com.example.marni.orderapp.domain.Product;
import com.example.marni.orderapp.presentation.adapters.MyOrderListViewAdapter;
import com.example.marni.orderapp.presentation.fragments.CategoryFragment;
import com.example.marni.orderapp.R;
import com.example.marni.orderapp.cardemulation.AccountStorage;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

import static com.example.marni.orderapp.presentation.activities.LoginActivity.JWT_STR;
import static com.example.marni.orderapp.presentation.activities.LoginActivity.USER;
import static com.example.marni.orderapp.presentation.activities.PaymentPendingActivity.CANCELED;
import static com.example.marni.orderapp.cardemulation.CardService.PENDING_NUMBER_OPEN;
import static com.example.marni.orderapp.cardemulation.CardService.PENDING_NUMBER_PENDING;
import static com.example.marni.orderapp.cardemulation.CardService.PREF_PENDING_NUMBER;

public class MyOrderActivity extends AppCompatActivity implements CategoryFragment.OnItemSelected,
        TotalFromAssortment.OnTotalChanged, AccountGetTask.OnBalanceAvailable,
        ProductsPutTask.SuccessListener, ProductsPostTask.SuccessListener,
        OrdersPutTask.PutSuccessListener, NavigationView.OnNavigationItemSelectedListener,
        SharedPreferences.OnSharedPreferenceChangeListener, ProductsGetTask.OnEmptyList, AdapterView.OnItemClickListener,
        MyOrderActivityRequests.MyOrderActivityListener, DevicePutTask.SuccessListener {

    private final String tag = getClass().getSimpleName();

    private StickyListHeadersListView stickyList;

    private ArrayList<Product> products = new ArrayList<>();
    private MyOrderListViewAdapter mAdapter;

    private double currentBalance;

    private TextView textViewBalance;
    private TextView accountEmail;

    private Order mOrder;

    private String jwt;
    private int user;
    private int quantity;

    private SharedPreferences prefs;

    private ProgressBar progressBar;
    private Double priceTotal;

    private TotalFromAssortment tfa;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order);
        tfa = new TotalFromAssortment(this);

        setupToolbar(this, "Home");
        setupDrawer(this);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        navigationView.setCheckedItem(R.id.nav_my_order);
        textViewBalance = (TextView) findViewById(R.id.toolbar_balance);
        accountEmail = (TextView) headerView.findViewById(R.id.nav_email);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        jwt = prefs.getString(JWT_STR, "");
        user = prefs.getInt(USER, 0);
        Log.i(tag, "user: " + user);

        prefs.registerOnSharedPreferenceChangeListener(this);
        prefs.unregisterOnSharedPreferenceChangeListener(this);
        prefs.edit().putString(PREF_PENDING_NUMBER, PENDING_NUMBER_OPEN);
        prefs.registerOnSharedPreferenceChangeListener(this);

        Bundle bundle = new Bundle();
        if (getIntent().getExtras() != null) {
            bundle = getIntent().getExtras();
        }
        if (bundle.getBoolean(CANCELED, false)) {
            Toast.makeText(this, "Your order was canceled", Toast.LENGTH_LONG).show();
        }

        ImageView imageView = (ImageView) findViewById(R.id.additem_orderdetail);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ProductsActivity.class);
                startActivity(intent);
            }
        });

        NfcAdapter mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (mNfcAdapter != null) {
            if (!mNfcAdapter.isEnabled()) {
                Toast.makeText(this, "Please activate NFC and press Back to return to the application!", Toast.LENGTH_LONG).show();
                startActivity(new Intent(android.provider.Settings.ACTION_NFC_SETTINGS));
            }
        } else {
            Log.i(tag, "Nfc adapter isn't working correctly");
        }

        stickyList = (StickyListHeadersListView) findViewById(R.id.listViewProducts);
        stickyList.setAreHeadersSticky(true);
        stickyList.setOnItemClickListener(this);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        getCurrentOrder();
        getBalance("https://mysql-test-p4.herokuapp.com/account/" + user);
        putDeviceInfo("https://mysql-test-p4.herokuapp.com/customer/device");
    }

    private void getCurrentOrder() {
        progressBar.setVisibility(View.VISIBLE);
        MyOrderActivityRequests request = new MyOrderActivityRequests(getApplicationContext(), this);
        request.handleGetCurrentOrder();
    }

    public static void setupToolbar(final AppCompatActivity activity, String title) {
        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.my_toolbar);
        activity.setSupportActionBar(toolbar);

        activity.getSupportActionBar().setTitle(title);
        toolbar.findViewById(R.id.toolbar_balance).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity.getApplicationContext(), TopUpActivity.class);
                activity.startActivity(intent);
            }
        });
    }

    public static void setupDrawer(final AppCompatActivity activity) {
        DrawerLayout drawer = (DrawerLayout) activity.findViewById(R.id.drawer_layout);
        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.my_toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                activity, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) activity.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener((NavigationView.OnNavigationItemSelectedListener) activity);
    }

    public void getBalance(String apiUrl) {
        String[] urls = new String[]{apiUrl, prefs.getString(JWT_STR, "")};
        AccountGetTask getBalance = new AccountGetTask(this);
        getBalance.execute(urls);
    }

    @Override
    public void onBalanceAvailable(Account bal) {
        DecimalFormat formatter = new DecimalFormat("#0.00");
        currentBalance = bal.getBalance();
        String s = "€" + formatter.format(currentBalance);
        textViewBalance.setText(s);
        accountEmail.setText(bal.getEmail());
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
            sQuantity = Integer.toString(quantity);
        }
        textViewQuantity.setText(sQuantity);

        AccountStorage.setAccount(this, Integer.toString(mOrder.getOrderId()), currentBalance, priceTotal);

        if (this.quantity < quantity) {
            setAnimation(getApplicationContext(), (ImageView) findViewById(R.id.imageView_orderdetail_cart));
        }
        this.quantity = quantity;
        this.priceTotal = priceTotal;
    }

    public static void setAnimation(Context context, ImageView imageView) {
        Animation animation;
        animation = AnimationUtils.loadAnimation(context, R.anim.wiggle);
        imageView.startAnimation(animation);
    }

    private void putProduct(String apiUrl, Product p) {
        String[] urls = new String[]{apiUrl, jwt, Integer.toString(mOrder.getOrderId()), Integer.toString(p.getProductId()), Integer.toString(user), Integer.toString(p.getQuantity())};
        ProductsPutTask task = new ProductsPutTask(this);
        task.execute(urls);
    }

    @Override
    public void successful(Boolean successful) {
        if (successful) {
            Log.i(tag, "Product amount changed");
        } else {
            Toast.makeText(this, "Product amount couldn't be changed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void putSuccessful(Boolean successful) {
        if (successful) {
            Log.i(tag, "Total price successfully edited");
        } else {
            Log.i(tag, "Error while updating total price");
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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Log.i(tag, item.toString() + " clicked.");

        int id = item.getItemId();

        new DrawerMenu(getApplicationContext(), id, jwt, user);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private int increase(int quantity) {
        return quantity + 1;
    }

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

    @Override
    public void onItemSelected(int i) {
        int j = 0;
        for (Product p : products) {
            j++;
            Log.i(tag, "j: " + j);
            Log.i(tag, "i: " + i);
            if (p.getCategoryId() == i) {
                stickyList.setSelection(j);
                break;
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.i(tag, "prefs.getString(" + key + ", defaultvalue): " + prefs.getString(key, "defaultvalue"));
        if (prefs.getString(PREF_PENDING_NUMBER, "").equals(PENDING_NUMBER_PENDING)) {
            Intent intent = new Intent(getApplicationContext(), PaymentPendingActivity.class);
            startActivity(intent);
            Log.i(tag, "prefs.getString(" + key + ", defaultvalue): " + prefs.getString(key, "defaultvalue"));
        }
    }


    @Override
    public void onCurrentOrderAvailable(Order order) {
        mOrder = order;
        Log.i(tag, Integer.toString(order.getOrderId()));

        MyOrderActivityRequests request = new MyOrderActivityRequests(getApplicationContext(), this);
        Log.i(tag, "mOrder.getOrderId(): " + mOrder.getOrderId());
        request.handleGetProducts(order.getOrderId());
    }

    @Override
    public void onOrderPutPriceSuccess() {
        // empty
    }

    @Override
    public void onError(String message) {
        // empty
    }

    @Override
    public void onProductsAvailable(List<Product> products) {
        progressBar.setVisibility(View.INVISIBLE);
        Log.i(tag, "products.size(): " + products.size());
        isEmpty(products.isEmpty());
        this.products = (ArrayList<Product>) products;

        mAdapter = new MyOrderListViewAdapter(this, getApplicationContext(), getLayoutInflater(), products, mOrder, this);
        stickyList.setAdapter(mAdapter);

        tfa.getTotals(products);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.i(tag, "Clicked item: " + position);

        Product p = products.get(position);
        p.setQuantity(increase(p.getQuantity()));

        putProduct("https://mysql-test-p4.herokuapp.com/product/quantity/edit", p);

        mAdapter.notifyDataSetChanged();
        tfa.getTotals(products);

        MyOrderActivityRequests request = new MyOrderActivityRequests(getApplicationContext(), this);
        request.handlePutOrder(priceTotal, mOrder.getOrderId());
    }

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    public void putDeviceInfo(String apiUrl) {

        String hardware;
        String type;
        String model;
        String brand;
        String device;
        String manufacturer;
        String buildUser;
        String serial;
        String host;
        String id;
        String bootloader;
        String board;
        String display;

        hardware = Build.HARDWARE;
        type = Build.TYPE;
        model = Build.MODEL;
        brand = Build.BRAND;
        device = Build.DEVICE;
        manufacturer = Build.MANUFACTURER;
        buildUser = Build.USER;
        serial = Build.SERIAL;
        host = Build.HOST;
        id = Build.ID;
        bootloader = Build.BOOTLOADER;
        board = Build.BOARD;
        display = Build.DISPLAY;

        DevicePutTask task = new DevicePutTask(this);
        String[] urls = new String[]{
                apiUrl,
                jwt,
                Integer.toString(this.user),
                hardware,
                type,
                model,
                brand,
                device,
                manufacturer,
                buildUser,
                serial,
                host,
                id,
                bootloader,
                board,
                display
        };
        task.execute(urls);
    }

    @Override
    public void successfulPut(Boolean successful) {
        throw new UnsupportedOperationException();
    }
}
