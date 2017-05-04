package com.example.marni.orderapp.OrderApp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.marni.orderapp.R;

public class LogInActivity extends AppCompatActivity implements
        TextView.OnClickListener,
        LoginTask.SuccessListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        Button buttonSignIn = (Button) findViewById(R.id.buttonSignIn);

        buttonSignIn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                login("");
            }
        });

        TextView textViewNoAccountYet = (TextView) findViewById(R.id.textViewNoAccountYet);

        textViewNoAccountYet.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);

        startActivity(intent);
    }

    void login(String ApiUrl) {

        LoginTask task = new LoginTask(this);
        String[] urls = new String[] {ApiUrl};
        task.execute(urls);
    }

    @Override
    public void successful(Boolean successful) {

        if (successful) {

            Intent intent = new Intent(getApplicationContext(), OrderHistoryActivity.class);

            startActivity(intent);
        }
    }
}
