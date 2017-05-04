package com.example.marni.orderapp.Presentation;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.marni.orderapp.DataAccess.LoginTask;
import com.example.marni.orderapp.R;

public class LogInActivity extends AppCompatActivity implements
        LoginTask.SuccessListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        Button buttonSignIn = (Button) findViewById(R.id.buttonSignIn);

        buttonSignIn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //login("https://mysql-test-p4.herokuapp.com/customers");
            }
        });

        TextView textViewNoAccountYet = (TextView) findViewById(R.id.textViewNoAccountYet);

        textViewNoAccountYet.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);

                startActivity(intent);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    void login(String ApiUrl) {

        EditText editTextEmailaddress = (EditText) findViewById(R.id.editTextEmailaddress);
        EditText editTextPassword = (EditText) findViewById(R.id.editTextPassword);

        LoginTask task = new LoginTask(this);
        String[] urls = new String[]{ApiUrl, editTextEmailaddress.getText().toString(), editTextPassword.getText().toString()};
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
