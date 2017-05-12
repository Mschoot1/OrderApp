package com.example.marni.orderapp.Presentation.Activities;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.marni.orderapp.DataAccess.AccountAccess.RegisterTask;
import com.example.marni.orderapp.R;

public class RegisterActivity extends AppCompatActivity implements
        RegisterTask.SuccessListener {

    private final String TAG = getClass().getSimpleName();

    private EditText editTextEmail;
    private EditText editTextPasswordOne;
    private EditText editTextPasswordTwo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button buttonRegister = (Button) findViewById(R.id.registerButton);

        buttonRegister.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
            @Override
            public void onClick(View v) {

                editTextEmail = (EditText) findViewById(R.id.emailTextfield);
                editTextPasswordOne = (EditText) findViewById(R.id.firstPasswordTextfield);
                editTextPasswordTwo = (EditText) findViewById(R.id.secondPasswordTextfield);

                if (isValidEmail(editTextEmail.getText().toString())) {

                    if (editTextPasswordOne.getText().toString().isEmpty()) {
                        Toast.makeText(RegisterActivity.this, "Please fill in a password.", Toast.LENGTH_SHORT).show();
                    } else if (editTextPasswordOne.getText().toString().equals(editTextPasswordTwo.getText().toString())){
                        register("https://mysql-test-p4.herokuapp.com/register");
                    } else {
                        Toast.makeText(RegisterActivity.this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
                    }
                } else {

                    Toast.makeText(RegisterActivity.this, "Invalid email address", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    void register(String ApiUrl) {

        RegisterTask task = new RegisterTask(this);
        String[] urls = new String[]{ApiUrl, editTextEmail.getText().toString(), editTextPasswordOne.getText().toString()};
        task.execute(urls);
    }

    @Override
    public void successful(Boolean successful) {

        Log.i(TAG, successful.toString());
        if(successful){

            Intent intent = new Intent(getApplicationContext(), LogInActivity.class);

            startActivity(intent);
        } else {

            Toast.makeText(this, "Registration failed.", Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
}
