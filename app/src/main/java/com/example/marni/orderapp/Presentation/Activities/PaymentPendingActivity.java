package com.example.marni.orderapp.Presentation.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.marni.orderapp.DataAccess.Account.ConfirmAsync;
import com.example.marni.orderapp.DataAccess.Orders.OrdersGetTask;
import com.example.marni.orderapp.Domain.Order;
import com.example.marni.orderapp.R;

import static com.example.marni.orderapp.Presentation.Activities.LogInActivity.JWT_STR;
import static com.example.marni.orderapp.Presentation.Activities.LogInActivity.USER;
import static com.example.marni.orderapp.cardemulation.CardService.PENDING_NUMBER_CANCELED;
import static com.example.marni.orderapp.cardemulation.CardService.PENDING_NUMBER_OPEN;
import static com.example.marni.orderapp.cardemulation.CardService.PREF_PENDING_NUMBER;

public class PaymentPendingActivity extends AppCompatActivity implements OrdersGetTask.OnOrderAvailable, ConfirmAsync.SuccessListener {

    private final String TAG = getClass().getSimpleName();

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_pending);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(
                new SharedPreferences.OnSharedPreferenceChangeListener() {
                    public void onSharedPreferenceChanged(
                            SharedPreferences prefs, String key) {
                        if (key.equals(PREF_PENDING_NUMBER)) {
                            getCurrentOrder("https://mysql-test-p4.herokuapp.com/order/current/" + prefs.getInt(USER, 0));
                            Log.i(TAG, "prefs.getString(PREF_PENDING_NUMBER, \"pendingNumber\"): " + prefs.getString(PREF_PENDING_NUMBER, "pendingNumber"));
                        }
                    }
                });

        Button button = (Button) findViewById(R.id.payment_pending_cancel);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MyOrderActivity.class);
                startActivity(intent);
            }
        });
    }

    private void getCurrentOrder(String apiUrl) {
        OrdersGetTask task = new OrdersGetTask(this);
        String[] urls = new String[]{apiUrl, prefs.getString(JWT_STR, "")};
        task.execute(urls);
    }

    @Override
    public void onOrderAvailable(Order order) {
        order.getPending();

        Log.i(TAG, "order.getPending(): " + order.getPending());
        Intent intent;
        switch (order.getPending() + "") {

            case PENDING_NUMBER_OPEN:
                ConfirmAsyncTask("https://mysql-test-p4.herokuapp.com/order/edit/", "0", order.getOrderId(), prefs.getInt(USER, 0));
                intent = new Intent(getApplicationContext(), PaymentSuccessfulActivity.class);
                prefs.edit().putString(JWT_STR, prefs.getString(JWT_STR, "")).apply();
                prefs.edit().putInt(USER, prefs.getInt(USER, 0));
                startActivity(intent);
                break;
            case PENDING_NUMBER_CANCELED:
                intent = new Intent(getApplicationContext(), MyOrderActivity.class);
                prefs.edit().putString(JWT_STR, prefs.getString(JWT_STR, "")).apply();
                prefs.edit().putInt(USER, prefs.getInt(USER, 0));
                startActivity(intent);
                break;
        }
    }

    public void ConfirmAsyncTask(String apiUrl, String status, int orderId, int userId) {
        ConfirmAsync confirmAsync = new ConfirmAsync(this);
        String[] urls = new String[]{ apiUrl, status, orderId + "", userId + "" };
        confirmAsync.execute(urls);
    }

    @Override
    public void successful(Boolean successful) {

    }
}
