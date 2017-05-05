package com.example.marni.orderapp.Presentation.Activities;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.marni.orderapp.DataAccess.RegisterTask;
import com.example.marni.orderapp.R;

public class RegisterActivity extends AppCompatActivity implements
        RegisterTask.SuccessListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button buttonRegister = (Button) findViewById(R.id.registerButton);

        buttonRegister.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
            @Override
            public void onClick(View v) {

                register("https://mysql-test-p4.herokuapp.com/customers");
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    void register(String ApiUrl) {

        EditText editTextEmailaddress = (EditText) findViewById(R.id.emailTextfield);
        EditText editTextPassword = (EditText) findViewById(R.id.firstPasswordTextfield);

        RegisterTask task = new RegisterTask(this);
        String[] urls = new String[]{ApiUrl, editTextEmailaddress.getText().toString(), editTextPassword.getText().toString()};
        task.execute(urls);
    }

    @Override
    public void successful(Boolean successful) {

        if(successful){

            Intent intent = new Intent(getApplicationContext(), LogInActivity.class);

            startActivity(intent);
        }
    }
}
