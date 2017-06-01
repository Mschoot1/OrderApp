package com.example.marni.orderapp.Presentation.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.auth0.android.jwt.Claim;
import com.auth0.android.jwt.JWT;
import com.example.marni.orderapp.DataAccess.AccountAccess.LoginTask;
import com.example.marni.orderapp.DataAccess.DeviceInfo.DevicePutTask;
import com.example.marni.orderapp.R;

import static com.example.marni.orderapp.DataAccess.AccountAccess.LoginTask.UNAUTHORIZED;

public class LogInActivity extends AppCompatActivity implements
        LoginTask.SuccessListener, DevicePutTask.SuccessListener {

    public static final String JWT_STR = "jwt_str";
    public static final String USER = "user";

    private final String TAG = getClass().getSimpleName();

    private EditText editTextEmail, editTextPassword;
    private CheckBox checkBox;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    private static final String PREF_NAME = "prefs";
    private static final String KEY_REMEMBER = "remember";
    private static final String KEY_EMAIL = "email";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        Button buttonSignIn = (Button) findViewById(R.id.buttonSignIn);

        checkBox = (CheckBox) findViewById(R.id.checkBox_login);
        editTextEmail = (EditText) findViewById(R.id.login_editTextEmailaddress);
        editTextPassword = (EditText) findViewById(R.id.login_editTextPassword);

        if (sharedPreferences.getBoolean(KEY_REMEMBER, true)) {
            checkBox.setChecked(true);
            editTextEmail.setText(sharedPreferences.getString(KEY_EMAIL, ""));
            editTextEmail.setSelection(editTextEmail.getText().length());
        } else {
            checkBox.setChecked(false);
        }

        buttonSignIn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (isValidEmail(editTextEmail.getText().toString())) {
                    login("https://mysql-test-p4.herokuapp.com/loginAuth");
                    managePreferences();
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

        LoginTask task = new LoginTask(this);
        String[] urls = new String[]{
                ApiUrl, editTextEmail.getText().toString(), editTextPassword.getText().toString(),
        };
        task.execute(urls);
    }

    @Override
    public void successful(String response) {

        Log.i(TAG, "response: " + response);

        if (response.equals(UNAUTHORIZED)) {
            Toast.makeText(this, "Login failed, please try again.", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Logged in", Toast.LENGTH_LONG).show();

            JWT jwt = new JWT(response);
            Claim user = jwt.getClaim("user");
            Intent intent = new Intent(getApplicationContext(), MyOrderActivity.class);
            intent.putExtra(JWT_STR, jwt);
            intent.putExtra(USER, user.asInt());

            Log.i(TAG, "user.asInt(): " + user.asInt());
            startActivity(intent);
        }
    }

    public static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    private void managePreferences() {
        if (checkBox.isChecked()) {
            editor.putString(KEY_EMAIL, editTextEmail.getText().toString().trim());
            editor.putBoolean(KEY_REMEMBER, true);
            editor.apply();
        } else {
            editor.remove(KEY_EMAIL);
            editor.putBoolean(KEY_REMEMBER, false);
            editor.apply();
        }
    }

    @Override
    public void successfulPut(Boolean successful) {

    }
}
