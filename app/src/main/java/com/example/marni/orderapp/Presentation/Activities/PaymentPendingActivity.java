package com.example.marni.orderapp.Presentation.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.marni.orderapp.DataAccess.Account.ConfirmAsync;
import com.example.marni.orderapp.DataAccess.Orders.OrdersGetCurrentTask;
import com.example.marni.orderapp.DataAccess.Orders.OrdersGetTask;
import com.example.marni.orderapp.DataAccess.Orders.PendingPutTask;
import com.example.marni.orderapp.Domain.Order;
import com.example.marni.orderapp.R;
import com.example.marni.orderapp.cardemulation.AccountStorage;

import static com.example.marni.orderapp.Presentation.Activities.LogInActivity.JWT_STR;
import static com.example.marni.orderapp.Presentation.Activities.LogInActivity.USER;
import static com.example.marni.orderapp.cardemulation.CardService.PENDING_NUMBER_CANCELED;
import static com.example.marni.orderapp.cardemulation.CardService.PENDING_NUMBER_OPEN;
import static com.example.marni.orderapp.cardemulation.CardService.PENDING_NUMBER_PENDING;
import static com.example.marni.orderapp.cardemulation.CardService.PREF_PENDING_NUMBER;

public class PaymentPendingActivity extends AppCompatActivity implements ConfirmAsync.SuccessListener, SharedPreferences.OnSharedPreferenceChangeListener, PendingPutTask.PutSuccessListener, OrdersGetCurrentTask.OnCurrentOrderAvailable {

    private final String TAG = getClass().getSimpleName();

    public static String CANCELED = "CANCELED";

    private SharedPreferences prefs;

    private String account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_pending);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);

        account = AccountStorage.GetAccount(this);

        Button button = (Button) findViewById(R.id.payment_pending_cancel);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Order canceled", Toast.LENGTH_LONG).show();
                prefs.edit().putString(PREF_PENDING_NUMBER, PENDING_NUMBER_OPEN).apply();
                putOrderPending("https://mysql-test-p4.herokuapp.com/order/pending", PENDING_NUMBER_OPEN, account);
                Intent intent = new Intent(getApplicationContext(), MyOrderActivity.class);
                startActivity(intent);
            }
        });
    }

    private void getCurrentOrder(String apiUrl) {
        OrdersGetCurrentTask task = new OrdersGetCurrentTask(this);
        String[] urls = new String[]{apiUrl, prefs.getString(JWT_STR, "")};
        task.execute(urls);
    }

    public void ConfirmAsyncTask(String apiUrl, String status, int orderId, int userId) {
        ConfirmAsync confirmAsync = new ConfirmAsync(this);
        String[] urls = new String[]{apiUrl, status, orderId + "", userId + ""};
        confirmAsync.execute(urls);
    }

    @Override
    public void successful(Boolean successful) {

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(PREF_PENDING_NUMBER)) {
            getCurrentOrder("https://mysql-test-p4.herokuapp.com/order/current/" + prefs.getInt(USER, 0));
            Log.i(TAG, "prefs.getString(PREF_PENDING_NUMBER, \"pendingNumber\"): " + prefs.getString(PREF_PENDING_NUMBER, "pendingNumber"));
        }
    }

    public void putOrderPending(String apiUrl, String pending, String orderId) {
        String[] urls = new String[]{apiUrl, prefs.getString(JWT_STR, ""), pending, orderId};
        PendingPutTask task = new PendingPutTask(this);
        task.execute(urls);
    }

    @Override
    public void putSuccessful(Boolean successful) {
        if (successful) {
            Log.i(TAG, "pending status successfully edited");
        } else {
            Log.i(TAG, "Error while updating pending status");
        }
    }

    @Override
    public void onCurrentOrderAvailable(Order order) {
        order.getPending();
        Log.i(TAG, "order.getPending(): " + order.getPending());
        Intent intent;
        switch (order.getPending() + "") {
            case PENDING_NUMBER_OPEN:
                ConfirmAsyncTask("https://mysql-test-p4.herokuapp.com/order/edit/", "0", order.getOrderId(), prefs.getInt(USER, 0));
                intent = new Intent(getApplicationContext(), PaymentSuccessfulActivity.class);
                startActivity(intent);
                break;
            case PENDING_NUMBER_CANCELED:
                intent = new Intent(getApplicationContext(), MyOrderActivity.class);
                intent.putExtra(CANCELED, true);
                startActivity(intent);
                break;
        }
    }
}
