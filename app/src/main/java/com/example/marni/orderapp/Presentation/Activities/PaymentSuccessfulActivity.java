package com.example.marni.orderapp.Presentation.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.marni.orderapp.R;

public class PaymentSuccessfulActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_successful);

        Thread closeActivity = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    // Do some stuff
                    Intent intent = new Intent(getApplicationContext(), MyOrderActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    e.getLocalizedMessage();
                } finally {
                    finish();
                }
            }
        });
    }
}