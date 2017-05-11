package com.example.marni.orderapp.Presentation.Activities;

import android.content.Intent;
import android.os.Bundle;
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
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.marni.orderapp.DataAccess.Balance.BalanceGetTask;
import com.example.marni.orderapp.Domain.Allergy;
import com.example.marni.orderapp.Domain.Balance;
import com.example.marni.orderapp.DummyGenerator.AllergiesGenerator;
import com.example.marni.orderapp.Presentation.Adapters.AllergiesListviewAdapter;
import com.example.marni.orderapp.R;

import java.util.ArrayList;

/**
 * Created by Wallaard on 4-5-2017.
 */

public class AllergiesActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        AllergiesGenerator.OnRandomUserAvailable, BalanceGetTask.OnBalanceAvailable {

    private final String TAG = getClass().getSimpleName();

    private BaseAdapter allergiesAdapter;
    private TextView textview_balance;
    private double current_balance;

    private ArrayList<Allergy> allergies = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allergies);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        
        // hide title
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        getAllergies();
        getBalance();

        textview_balance = (TextView)findViewById(R.id.toolbar_balance);

        ListView listViewAllergies = (ListView) findViewById(R.id.allergies_listview);
        allergiesAdapter = new AllergiesListviewAdapter(this, getLayoutInflater(), allergies);
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
//            case R.id.action_settings:
//                // User chose the "Settings" item, show the app settings UI...
//                return true;
//
//            case R.id.action_favorite:
//                // User chose the "Favorite" action, mark the current item
//                // as a favorite...
//                return true;

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

        Intent intent;

        switch (id) {
            case R.id.nav_assortment:
                intent = new Intent(getApplicationContext(), ProductsActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_my_order:
//                intent = new Intent(getApplicationContext(), OrderActivity.class);
//                startActivity(intent);
                break;
            case R.id.nav_order_history:
                intent = new Intent(getApplicationContext(), OrderHistoryActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_top_up:
                intent = new Intent(getApplicationContext(), TopUpActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_allergy_information:
                intent = new Intent(getApplicationContext(), AllergiesActivity.class);
                startActivity(intent);
                break;
        }

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

    public void getAllergies() {

        String[] urls = new String[] { "https://mysql-test-p4.herokuapp.com/allergie" };

        // Connect and pass self for callback
        AllergiesGenerator getRandomUser = new AllergiesGenerator(this);
        getRandomUser.execute(urls);
    }

    public void getBalance(){
        String[] urls = new String[] { "https://mysql-test-p4.herokuapp.com/balance/284" };

        BalanceGetTask getBalance = new BalanceGetTask(this);
        getBalance.execute(urls);
    }

    public void onBalanceAvailable(Balance bal){
        current_balance = bal.getBalance();
        textview_balance.setText("€ " + current_balance);
    }
}
