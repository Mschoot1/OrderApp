package com.example.marni.orderapp.presentation.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.auth0.android.jwt.Claim;
import com.auth0.android.jwt.JWT;
import com.example.marni.orderapp.dataaccess.LoginActivityRequests;
import com.example.marni.orderapp.dataaccess.deviceinfo.DevicePutTask;
import com.example.marni.orderapp.R;

public class LoginActivity extends AppCompatActivity implements
        DevicePutTask.SuccessListener,
        LoginActivityRequests.LoginActivityListener {

    public static final String JWT_STR = "jwt_str";
    public static final String USER = "user";

    private final String tag = getClass().getSimpleName();

    private EditText editTextEmail;
    private EditText editTextPassword;

    private CheckBox checkBox;

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public static final String PREF_NAME = "prefs";
    private static final String KEY_REMEMBER = "remember";
    private static final String KEY_EMAIL = "email";

    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        editor = prefs.edit();

        Button buttonSignIn = (Button) findViewById(R.id.buttonSignIn);

        checkBox = (CheckBox) findViewById(R.id.checkBox_login);
        editTextEmail = (EditText) findViewById(R.id.login_editTextEmailaddress);
        editTextPassword = (EditText) findViewById(R.id.login_editTextPassword);

        if (prefs.getBoolean(KEY_REMEMBER, true)) {
            checkBox.setChecked(true);
            editTextEmail.setText(prefs.getString(KEY_EMAIL, ""));
            editTextEmail.setSelection(editTextEmail.getText().length());
        } else {
            checkBox.setChecked(false);
        }

        buttonSignIn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (isValidEmail(editTextEmail.getText().toString())) {
                    handleLogin(editTextEmail.getText().toString(), editTextPassword.getText().toString());
                    managePreferences();
                } else {
                    Toast.makeText(LoginActivity.this, "Invalid email address", Toast.LENGTH_SHORT).show();
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

    private void handleLogin(String email, String password) {
        dialog = new ProgressDialog(this);
        setupDialog(dialog);
        LoginActivityRequests request = new LoginActivityRequests(getApplicationContext(), this);
        request.handleLogin(email, password);
    }

    private void setupDialog(ProgressDialog dialog) {
        dialog.setMessage("Authenticating. Please wait..");
        dialog.show();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
    }

    public void displayMessage(String toastString) {
        Toast.makeText(getApplicationContext(), toastString, Toast.LENGTH_LONG).show();
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
        // Do nothing.
    }

    @Override
    public void onLoginSuccessful(String response) {
        dialog.dismiss();
        Toast.makeText(getApplicationContext(), "Logged in", Toast.LENGTH_LONG).show();
        JWT jwt = new JWT(response);
        Claim user = jwt.getClaim(USER);
        Intent intent = new Intent(getApplicationContext(), MyOrderActivity.class);

        Log.i(tag, "user.asInt(): " + user.asInt());
        editor.putString(JWT_STR, jwt.toString().replace("\"", ""));
        editor.putInt(USER, user.asInt());
        editor.apply();

        startActivity(intent);
        finish();
    }

    @Override
    public void onError(String message) {
        dialog.dismiss();
        Log.e(tag, "onError " + message);
        Toast.makeText(getApplicationContext(), "Log in failed. Please try again", Toast.LENGTH_LONG).show();
    }
}
