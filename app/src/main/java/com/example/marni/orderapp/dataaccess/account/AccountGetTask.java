package com.example.marni.orderapp.dataaccess.account;

import android.os.AsyncTask;
import android.util.Log;

import com.example.marni.orderapp.domain.Account;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class AccountGetTask extends AsyncTask<String, Void, String> {

    private OnBalanceAvailable listener = null;

    private final String tag = getClass().getSimpleName();

    public AccountGetTask(OnBalanceAvailable listener) {
        this.listener = listener;
    }

    @Override
    protected String doInBackground(String... params) {

        InputStream inputStream = null;
        int responsCode = -1;
        // De URL die we via de .execute() meegeleverd krijgen
        String balanceUrl = params[0];
        // Het resultaat dat we gaan retourneren
        String response = "";

        Log.i(tag, "doInBackground - " + balanceUrl);
        try {
            // Maak een URL object
            URL url = new URL(balanceUrl);
            // Open een connection op de URL
            URLConnection urlConnection = url.openConnection();

            if (!(urlConnection instanceof HttpURLConnection)) {
                return null;
            }

            HttpURLConnection httpConnection = (HttpURLConnection) urlConnection;
            httpConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            httpConnection.setRequestMethod("GET");
            httpConnection.setRequestProperty("Authorization", "Bearer " + params[1]);

            httpConnection.connect();

            responsCode = httpConnection.getResponseCode();
            if (responsCode == HttpURLConnection.HTTP_OK) {
                inputStream = httpConnection.getInputStream();
                response = getStringFromInputStream(inputStream);
            } else {
                Log.e(tag, "Error, invalid response");
            }
        } catch (MalformedURLException e) {
            Log.e(tag, "doInBackground MalformedURLEx " + e.getLocalizedMessage());
            return null;
        } catch (IOException e) {
            Log.e(tag, "doInBackground IOException " + e.getLocalizedMessage());
            return null;
        }

        return response;
    }

    @Override
    protected void onPostExecute(String response) {

        // Check of er een response is
        if (response == null || response == "") {
            Log.e(tag, "onPostExecute kreeg een lege response!");
            return;
        }

        // Het resultaat is in ons geval een stuk tekst in JSON formaat.
        // Daar moeten we de info die we willen tonen uit filteren (parsen).
        // Dat kan met een JSONObject.
        JSONObject jsonObject;

        try {
            // Top level json object
            jsonObject = new JSONObject(response);

            JSONArray jsonArray = jsonObject.getJSONArray("results");

            // Get all products and start looping
            for (int idx = 0; idx < jsonArray.length(); idx++) {
                // array level objects and get user
                JSONObject accountObject = jsonArray.getJSONObject(idx);

                Double balance = accountObject.getDouble("balance");
                String email = accountObject.getString("email");

                // Create new Account object
                Account b = new Account(balance, email);

                Log.d(tag, "onPostExecute: " + " balance: " + balance);
                //
                // call back with new balance data
                //
                listener.onBalanceAvailable(b);

            }
        } catch (JSONException ex) {
            Log.e(tag, "onPostExecute JSONException " + ex.getLocalizedMessage());
        }
    }

    private static String getStringFromInputStream(InputStream is) {

        StringBuilder sb = new StringBuilder();
        String line;
        try (
                BufferedReader br = new BufferedReader(new InputStreamReader(is))
        ) {
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();
        } catch (IOException e) {
            Log.e("", "getStringFromInputStream " + e.getLocalizedMessage());
        }

        return sb.toString();
    }

    public interface OnBalanceAvailable {
        void onBalanceAvailable(Account balance);
    }
}
