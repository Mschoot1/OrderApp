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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marni.orderapp.BusinessLogic.CalculateBalance;
import com.example.marni.orderapp.DataAccess.Balance.BalanceGetTask;
import com.example.marni.orderapp.DataAccess.Balance.BalancePostTask;
import com.example.marni.orderapp.Domain.Balance;
import com.example.marni.orderapp.R;

public class TopUpActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        CalculateBalance.OnBalanceChanged, CalculateBalance.OnResetBalance, BalanceGetTask.OnBalanceAvailable, BalancePostTask.SuccessListener {

    private final String TAG = getClass().getSimpleName();
    public final static  String TOPUP_EXTRA = "topup_extra";
    private RadioButton button1, button2;
    private TextView textview_balance, textview_newbalance;
    private EditText edittext_value;
    private double current_balance;
    private CalculateBalance calculateBalance;
    private Spinner spinner;
    private Button payment;

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

        getBalance();

        calculateBalance = new CalculateBalance(this, this);

        button1 = (RadioButton)findViewById(R.id.topup_radiobutton1);
        button1.setChecked(true);
        button2 = (RadioButton)findViewById(R.id.topup_radiobutton2);

        payment = (Button)findViewById(R.id.topup_button_topayment);
        payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(calculateBalance.getAdded_balance() != 0){
                    postBalance("https://mysql-test-p4.herokuapp.com/topup");
                    getBalance();
                } else {
                    Toast.makeText(TopUpActivity.this, "No amount selected", Toast.LENGTH_SHORT).show();
                }
            }
        });

        textview_balance = (TextView)findViewById(R.id.toolbar_balance);
        textview_newbalance = (TextView)findViewById(R.id.topup_edittext_newbalance);

        edittext_value = (EditText)findViewById(R.id.topup_edittext_value);

        spinner = (Spinner)findViewById(R.id.topup_spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(button2.isChecked()) {
                    switch (position) {
                        case 0:
                            addBalance(Double.parseDouble(spinner.getSelectedItem().toString()));
                            break;
                        case 1:
                            addBalance(Double.parseDouble(spinner.getSelectedItem().toString()));
                            break;
                        case 2:
                            addBalance(Double.parseDouble(spinner.getSelectedItem().toString()));
                            break;
                        case 3:
                            addBalance(Double.parseDouble(spinner.getSelectedItem().toString()));
                            break;
                        case 4:
                            addBalance(Double.parseDouble(spinner.getSelectedItem().toString()));
                            break;
                    }

                }
            }
            
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });



        edittext_value.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    if(button1.isChecked()){
                        Double add_balance = Double.parseDouble(edittext_value.getText().toString());
                        addBalance(add_balance);
                    }
                } catch (Exception e){
                    Log.i(TAG, "Empty value");
                    calculateBalance.resetBalance();
                    calculateBalance.resetAddedBalance();
                }
            }
        });


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
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.topup_radiobutton1:
                if (checked)
                    button2.setChecked(false);
                    calculateBalance.resetBalance();
                    calculateBalance.resetAddedBalance();
                    edittext_value.setEnabled(true);

                    break;
            case R.id.topup_radiobutton2:
                if (checked)
                    button1.setChecked(false);
                    calculateBalance.resetBalance();
                    edittext_value.setText("");
                    edittext_value.setEnabled(false);
                    spinner.setFocusable(true);

                    String balance = spinner.getSelectedItem().toString();
                    Double add_balance = Double.parseDouble(balance);
                    addBalance(add_balance);

                    break;
        }
    }

    public void getBalance(){
        String[] urls = new String[] { "https://mysql-test-p4.herokuapp.com/balance/284" };

        BalanceGetTask getBalance = new BalanceGetTask(this);
        getBalance.execute(urls);
    }

    @Override
    public void onBalanceChanged(double newBalance) {
        textview_newbalance.setText("€ " + newBalance);
    }

    @Override
    public void onResetBalance(double balance) {
        textview_newbalance.setText("");
    }

    public void addBalance(double balance){
        calculateBalance.newBalance(current_balance, balance);
        calculateBalance.setAdded_balance(balance);
    }

    public void onBalanceAvailable(Balance bal){
        current_balance = bal.getBalance();
        textview_balance.setText("€ " + current_balance);
    }

    public void postBalance(String ApiUrl){
        BalancePostTask task = new BalancePostTask(this);
        String[] urls = new String[]{ApiUrl, Double.toString(calculateBalance.getAdded_balance()), "topup", "284"};
        task.execute(urls);
    }

    @Override
    public void successful(Boolean successful) {

        Log.i(TAG, successful.toString());
        if(successful){

            Toast.makeText(this, "Balance succesfully added", Toast.LENGTH_SHORT).show();
        } else {

            Toast.makeText(this, "Balance top up failed", Toast.LENGTH_SHORT).show();
        }
    }
}