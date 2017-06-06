package com.example.marni.orderapp.Presentation.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
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
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.marni.orderapp.DataAccess.Account.AccountGetTask;
import com.example.marni.orderapp.Domain.Account;
import com.example.marni.orderapp.Domain.Allergy;
import com.example.marni.orderapp.DataAccess.Allergies.AllergiesGetTask;
import com.example.marni.orderapp.Presentation.Adapters.AllergiesListViewAdapter;
import com.example.marni.orderapp.Presentation.DrawerMenu;
import com.example.marni.orderapp.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

import static com.example.marni.orderapp.Presentation.Activities.LogInActivity.JWT_STR;
import static com.example.marni.orderapp.Presentation.Activities.LogInActivity.USER;

public class AllergiesActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        AllergiesGetTask.OnRandomUserAvailable, AccountGetTask.OnBalanceAvailable {

    private final String TAG = getClass().getSimpleName();

    private BaseAdapter allergiesAdapter;
    private TextView textview_balance;
    private TextView account_email;

    private ArrayList<Allergy> allergies = new ArrayList<>();

    private String jwt;
    private int user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allergies);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        jwt = prefs.getString(JWT_STR, "");
        user = prefs.getInt(USER, 0);


        getSupportActionBar().setTitle("Allergy Information");
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

        navigationView.setCheckedItem(R.id.nav_allergy_information);

        getAllergies("https://mysql-test-p4.herokuapp.com/product/allergies");
        getBalance("https://mysql-test-p4.herokuapp.com/account/" + user);

        textview_balance = (TextView)findViewById(R.id.toolbar_balance);
        account_email = (TextView)headerView.findViewById(R.id.nav_email);

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

    @Override
    public void onRandomUserAvailable(Allergy allergy) {
        allergies.add(allergy);
        Log.i(TAG, "Size: " + allergies.size());
        allergiesAdapter.notifyDataSetChanged();
    }

    public void getAllergies(String apiUrl) {

        String[] urls = new String[] { apiUrl, jwt};

        // Connect and pass self for callback
        AllergiesGetTask getRandomUser = new AllergiesGetTask(this);
        getRandomUser.execute(urls);
    }

    public void getBalance(String apiUrl){
        String[] urls = new String[] { apiUrl, jwt };

        AccountGetTask getBalance = new AccountGetTask(this);
        getBalance.execute(urls);
    }

    public void onBalanceAvailable(Account bal){
        DecimalFormat formatter = new DecimalFormat("#0.00");

        double current_balance = bal.getBalance();
        textview_balance.setText("€ " + formatter.format(current_balance));
        account_email.setText(bal.getEmail());
    }
}
