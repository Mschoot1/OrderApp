package com.example.marni.orderapp.DataAccess.Balance;

import android.os.AsyncTask;
import android.util.Log;

import com.example.marni.orderapp.Domain.Balance;

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

public class BalanceGetTask extends AsyncTask<String, Void, String> {

    private OnBalanceAvailable listener = null;

    private final String TAG = getClass().getSimpleName();

    public BalanceGetTask(OnBalanceAvailable listener){
        this.listener = listener;
    }

    protected String doInBackground(String... params) {

        InputStream inputStream = null;
        int responsCode = -1;
        // De URL die we via de .execute() meegeleverd krijgen
        String balanceUrl = params[0];
        // Het resultaat dat we gaan retourneren
        String response = "";

        Log.i(TAG, "doInBackground - " + balanceUrl);
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
                Log.e(TAG, "Error, invalid response");
            }
        } catch (MalformedURLException e) {
            Log.e(TAG, "doInBackground MalformedURLEx " + e.getLocalizedMessage());
            return null;
        } catch (IOException e) {
            Log.e(TAG, "doInBackground IOException " + e.getLocalizedMessage());
            return null;
        }
//        response = "[{\"balance\":231}]";

        return response;
    }

    protected void onPostExecute(String response) {

        // Check of er een response is
        if(response == null || response == "") {
            Log.e(TAG, "onPostExecute kreeg een lege response!");
            return;
        }

        // Het resultaat is in ons geval een stuk tekst in JSON formaat.
        // Daar moeten we de info die we willen tonen uit filteren (parsen).
        // Dat kan met een JSONObject.
        JSONArray balance_array;

        try {
            // Top level json object
            balance_array = new JSONArray(response);

            // Get all products and start looping
            for (int idx = 0; idx < balance_array.length(); idx++) {
                // array level objects and get user
                JSONObject balance_object = balance_array.getJSONObject(idx);

                Double balance = balance_object.getDouble("balance");

                // Create new Balance object
                Balance b = new Balance(balance);

                Log.d(TAG, "onPostExecute: " + " balance: " + balance);
                //
                // call back with new balance data
                //
                listener.onBalanceAvailable(b);

            }
        } catch( JSONException ex) {
            Log.e(TAG, "onPostExecute JSONException " + ex.getLocalizedMessage());
        }
    }

    private static String getStringFromInputStream(InputStream is) {

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {

            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();
    }

    public interface OnBalanceAvailable {
        void onBalanceAvailable(Balance balance);
    }
}
