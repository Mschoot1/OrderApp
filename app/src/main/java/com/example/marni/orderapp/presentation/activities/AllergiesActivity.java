package com.example.marni.orderapp.presentation.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.marni.orderapp.dataaccess.account.AccountGetTask;
import com.example.marni.orderapp.domain.Account;
import com.example.marni.orderapp.domain.Allergy;
import com.example.marni.orderapp.dataaccess.allergies.AllergiesGetTask;
import com.example.marni.orderapp.presentation.adapters.AllergiesListViewAdapter;
import com.example.marni.orderapp.presentation.DrawerMenu;
import com.example.marni.orderapp.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

import static com.example.marni.orderapp.presentation.activities.LoginActivity.JWT_STR;
import static com.example.marni.orderapp.presentation.activities.LoginActivity.USER;
import static com.example.marni.orderapp.presentation.activities.MyOrderActivity.setupDrawer;
import static com.example.marni.orderapp.presentation.activities.MyOrderActivity.setupToolbar;

public class AllergiesActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        AllergiesGetTask.OnRandomUserAvailable, AccountGetTask.OnBalanceAvailable {

    private final String tag = getClass().getSimpleName();

    private BaseAdapter allergiesAdapter;
    private TextView textViewBalance;
    private TextView accountEmail;

    private ArrayList<Allergy> allergies = new ArrayList<>();

    private String jwt;
    private int user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allergies);

        setupToolbar(this, "Allergies");
        setupDrawer(this);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        jwt = prefs.getString(JWT_STR, "");
        user = prefs.getInt(USER, 0);

        getAllergies("https://mysql-test-p4.herokuapp.com/product/allergies");
        getBalance("https://mysql-test-p4.herokuapp.com/account/" + user);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        navigationView.setCheckedItem(R.id.nav_allergy_information);
        textViewBalance = (TextView) findViewById(R.id.toolbar_balance);
        accountEmail = (TextView) headerView.findViewById(R.id.nav_email);

        ListView listViewAllergies = (ListView) findViewById(R.id.allergies_listview);
        allergiesAdapter = new AllergiesListViewAdapter(this, getLayoutInflater(), allergies);
        listViewAllergies.setAdapter(allergiesAdapter);
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

    @Override
    public void onRandomUserAvailable(Allergy allergy) {
        allergies.add(allergy);
        Log.i(tag, "Size: " + allergies.size());
        allergiesAdapter.notifyDataSetChanged();
    }

    public void getAllergies(String apiUrl) {

        String[] urls = new String[]{apiUrl, jwt};

        // Connect and pass self for callback
        AllergiesGetTask getRandomUser = new AllergiesGetTask(this);
        getRandomUser.execute(urls);
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
