package com.example.marni.orderapp.OrderApp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.marni.orderapp.R;

public class LogInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        Button buttonSignIn = (Button) findViewById(R.id.buttonSignIn);

        buttonSignIn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


            }
        });
    }
}
