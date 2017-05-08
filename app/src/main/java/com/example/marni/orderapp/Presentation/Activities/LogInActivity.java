package com.example.marni.orderapp.Presentation.Activities;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marni.orderapp.DataAccess.LoginTask;
import com.example.marni.orderapp.R;

import static com.example.marni.orderapp.Presentation.Activities.RegisterActivity.isValidEmail;

public class LogInActivity extends AppCompatActivity implements
        LoginTask.SuccessListener {

    private EditText editTextEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        Button buttonSignIn = (Button) findViewById(R.id.buttonSignIn);

        buttonSignIn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                editTextEmail = (EditText) findViewById(R.id.emailTextfield);

                if (isValidEmail(editTextEmail.getText().toString())) {

                    login("https://mysql-test-p4.herokuapp.com/login");
                } else {

                    Toast.makeText(LogInActivity.this, "Invalid email address", Toast.LENGTH_SHORT).show();
                }
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

        editTextEmail = (EditText) findViewById(R.id.editTextEmailaddress);
        EditText editTextPassword = (EditText) findViewById(R.id.editTextPassword);

        LoginTask task = new LoginTask(this);
        String[] urls = new String[]{ApiUrl, editTextEmail.getText().toString(), editTextPassword.getText().toString()};
        task.execute(urls);
    }

    @Override
    public void successful(Boolean successful) {

        if (successful) {

            Intent intent = new Intent(getApplicationContext(), OrderHistoryActivity.class);

            startActivity(intent);
        } else {

            Toast.makeText(this, "Login failed, please try again.", Toast.LENGTH_SHORT).show();
        }
    }
}
