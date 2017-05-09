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
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.marni.orderapp.BusinessLogic.CalculateBalance;
import com.example.marni.orderapp.R;

public class TopUpActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private final String TAG = getClass().getSimpleName();
    private RadioButton button1, button2;
    private TextView textview_balance, textview_newbalance;
    private EditText edittext_value;
    private double current_balance;
    private CalculateBalance calculateBalance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_up);

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

        // set current menu item checked
        navigationView.setCheckedItem(R.id.nav_top_up);

        calculateBalance = new CalculateBalance();


        button1 = (RadioButton)findViewById(R.id.topup_radiobutton1);
        button2 = (RadioButton)findViewById(R.id.topup_radiobutton2);

        current_balance = 14;

        textview_balance = (TextView)findViewById(R.id.toolbar_balance);
        textview_balance.setText("$ " + current_balance);
        textview_newbalance = (TextView)findViewById(R.id.topup_edittext_newbalance);

        edittext_value = (EditText)findViewById(R.id.topup_edittext_value);

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

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.topup_radiobutton1:
                if (checked)
                    button2.setChecked(false);

                    String balance_added = edittext_value.getText().toString();
                    Double added_balance = Double.parseDouble(balance_added);
                    Double newBalance = calculateBalance.newBalance(current_balance, added_balance);

                    textview_newbalance.setText("" + newBalance);

                    break;
            case R.id.topup_radiobutton2:
                if (checked)
                    button1.setChecked(false);

                    break;
        }
    }
}
