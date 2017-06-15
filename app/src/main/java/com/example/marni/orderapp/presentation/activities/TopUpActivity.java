package com.example.marni.orderapp.presentation.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marni.orderapp.businesslogic.CalculateBalance;
import com.example.marni.orderapp.dataaccess.account.AccountGetTask;
import com.example.marni.orderapp.dataaccess.account.BalancePostTask;
import com.example.marni.orderapp.domain.Account;
import com.example.marni.orderapp.presentation.DrawerMenu;
import com.example.marni.orderapp.R;
import com.example.marni.orderapp.cardemulation.AccountStorage;

import java.text.DecimalFormat;

import static com.example.marni.orderapp.presentation.activities.LoginActivity.JWT_STR;
import static com.example.marni.orderapp.presentation.activities.LoginActivity.USER;

public class TopUpActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        CalculateBalance.OnBalanceChanged, CalculateBalance.OnResetBalance, AccountGetTask.OnBalanceAvailable,
        BalancePostTask.SuccessListener, CalculateBalance.OnCheckPayment {

    private final String tag = getClass().getSimpleName();

    private RadioButton button1;
    private RadioButton button2;
    private RadioButton button3;

    private TextView textViewNewBalance;
    private TextView textViewCurrentBalance;
    private EditText editTextValue;
    private TextView accountEmail;
    private double currentBalance;
    private CalculateBalance calculateBalance;
    private Spinner spinner;
    private TextView payment;

    private String jwt;
    private int user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_up);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        jwt = prefs.getString(JWT_STR, "");
        user = prefs.getInt(USER, 0);

        setupToolbar("Top Up");
        setupDrawer();

        AccountStorage.resetAccount(this);

        getBalance("https://mysql-test-p4.herokuapp.com/account/" + user);

        calculateBalance = new CalculateBalance(this, this, this);

        button1 = (RadioButton) findViewById(R.id.topup_radiobutton1);
        button1.setChecked(true);
        button2 = (RadioButton) findViewById(R.id.topup_radiobutton2);
        button3 = (RadioButton) findViewById(R.id.topup_radiobutton3);

        textViewNewBalance = (TextView) findViewById(R.id.topup_edittext_newbalance);
        textViewCurrentBalance = (TextView) findViewById(R.id.topup_currentbalance);

        editTextValue = (EditText) findViewById(R.id.topup_edittext_value);
        editTextValue.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(3)
        });

        spinner = (Spinner) findViewById(R.id.topup_spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (button2.isChecked()) {
                    addBalance(Integer.parseInt(spinner.getSelectedItem().toString()));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // empty
            }
        });

        editTextValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // empty
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // empty
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    if (button1.isChecked()) {
                        Integer addBalance = Integer.parseInt(editTextValue.getText().toString());
                        addBalance(addBalance);
                    }
                } catch (Exception e) {
                    Log.i(tag, "Empty value");
                    calculateBalance.resetBalance(true);
                }
            }
        });

        Intent intent = getIntent();
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            Uri uri = intent.getData();
            if(uri.getQueryParameter("successful").equals("true")){
                Toast.makeText(this, "Balance succesfully added", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Top up failed", Toast.LENGTH_LONG).show();
            }
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

    private void setupToolbar(String title) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(title);

        payment = (TextView) findViewById(R.id.toolbar_topup);
        payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myWebLink = new Intent(android.content.Intent.ACTION_VIEW);
                String url = "http://nubisonline.nl/orderapp/pay?amount=" + calculateBalance.getAddedBalance() + "&order=" + System.currentTimeMillis() + "&customer_id=" + Integer.toString(user);
                myWebLink.setData(Uri.parse(url));
                startActivity(myWebLink);
            }
        });
    }

    private void setupDrawer() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_top_up);
        View headerView = navigationView.getHeaderView(0);
        accountEmail = (TextView) headerView.findViewById(R.id.nav_email);
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

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.topup_radiobutton1:
                if (checked) {
                    button2.setChecked(false);
                }
                button3.setChecked(false);
                calculateBalance.resetBalance(true);
                editTextValue.setEnabled(true);
                break;
            case R.id.topup_radiobutton2:
                if (checked) {
                    button1.setChecked(false);
                }
                button3.setChecked(false);
                calculateBalance.resetBalance(false);
                editTextValue.setText("");
                editTextValue.setEnabled(false);
                spinner.setFocusable(true);
                String balance = spinner.getSelectedItem().toString();
                Integer addBalance = Integer.parseInt(balance);
                addBalance(addBalance);
                break;
            case R.id.topup_radiobutton3:
                if (checked) {
                    button1.setChecked(false);
                }
                button2.setChecked(false);
                calculateBalance.resetBalance(false);
                editTextValue.setText("");
                editTextValue.setEnabled(false);
                addBalance(calculateBalance.maxBalance(currentBalance));
                break;
            default:
                break;
        }
    }

    public void getBalance(String apiUrl) {
        String[] urls = new String[]{apiUrl, jwt};

        AccountGetTask getBalance = new AccountGetTask(this);
        getBalance.execute(urls);
    }

    @Override
    public void onBalanceChanged(double newBalance) {
        DecimalFormat formatter = new DecimalFormat("#0.00");

        calculateBalance.checkPayment();

        if (currentBalance <= 150) {
            textViewNewBalance.setText("€ " + formatter.format(newBalance));
        }
    }

    @Override
    public void onResetBalance(double balance) {
        textViewNewBalance.setText("");
        payment.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.backgroundcategoryheaders));
        payment.setEnabled(false);
    }

    public void addBalance(double addedBalance) {
        calculateBalance.newBalance(currentBalance, addedBalance);
    }

    public void onBalanceAvailable(Account bal) {
        DecimalFormat formatter = new DecimalFormat("#0.00");

        currentBalance = bal.getBalance();
        textViewCurrentBalance.setText("Balance: €" + formatter.format(currentBalance));
        accountEmail.setText(bal.getEmail());
    }

    public void successfulTopUp() {
        calculateBalance.resetBalance(true);
        editTextValue.setText("");
        editTextValue.setEnabled(true);
        button1.setChecked(true);
        button2.setChecked(false);
        button3.setChecked(false);
        spinner.setSelection(0);
    }

    @Override
    public void successful(Boolean successful) {
        Log.i(tag, successful.toString());
        if (successful) {
            Toast.makeText(this, "Balance succesfully added", Toast.LENGTH_SHORT).show();
            successfulTopUp();
            getBalance("https://mysql-test-p4.herokuapp.com/account/" + user);
        } else {
            Toast.makeText(this, "Top up failed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCheckPayment(String check) {
        if (check.equals("success")) {
            payment.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorWhite));
            payment.setEnabled(true);
        } else if (check.equals("zero")) {
            payment.setEnabled(false);
        } else {
            Toast.makeText(this, "Max account balance is 150", Toast.LENGTH_SHORT).show();
            payment.setEnabled(false);
        }
    }
}
