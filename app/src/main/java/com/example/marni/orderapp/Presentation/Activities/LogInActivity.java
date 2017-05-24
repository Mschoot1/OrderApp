package com.example.marni.registerapp.Presentation.Presentation.Activities;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.marni.registerapp.Presentation.AsyncKlassen.LoginTask;
import com.example.marni.registerapp.R;

public class LogInActivity extends AppCompatActivity implements LoginTask.SuccessListener {
    private EditText editTextEmail,editTextPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        Button button = (Button) findViewById(R.id.buttonSignIn);
        editTextEmail = (EditText) findViewById(R.id.editTextEmailaddress);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);

        button.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
            public void onClick(View V) {
                //EditText ed = (EditText) findViewById(R.id.editTextEmailaddress);
                //EditText ed2 = (EditText) findViewById(R.id.editTextPassword);

                if(isValidEmail(editTextEmail.getText().toString()))
                {
                    login("https://mysql-test-p4.herokuapp.com/loginRegister");
                } else {
                    Toast.makeText(LogInActivity.this,"Invalid email adress",Toast.LENGTH_SHORT).show();
                }

                //if(ed2_text.isEmpty() || ed2_text.length() == 0 || ed2_text.equals("") || ed2_text == null)
                if(isValidEmail(editTextPassword.getText().toString()))
                {
                    editTextPassword.setError("Enter password");
                }
            }
        });
    }

    public void successful(Boolean successful) {
        if (successful) {
            Toast.makeText(this, "Logged in", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getApplicationContext(), OrderHistoryActivity.class);

            startActivity(intent);
        } else {
            Toast.makeText(this, "Login failed, please try again.", Toast.LENGTH_LONG).show();
        }
    }


    void login(String ApiUrl) {

        LoginTask task = new LoginTask(this);
        String[] urls = new String[]{
                ApiUrl, editTextEmail.getText().toString(), editTextPassword.getText().toString(),
        };
        task.execute(urls);
    }

    public static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
}
