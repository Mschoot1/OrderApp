package com.example.marni.orderapp.presentation.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.marni.orderapp.R;

public class PaymentSuccessfulActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_successful);

        Handler mHandler = new Handler();
        mHandler.postDelayed(mUpdateTimeTask, 3500);
    }

    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            Intent intent = new Intent(getApplicationContext(), OrderHistoryActivity.class);
            startActivity(intent);
        }
    };
}