package com.example.marni.orderapp.DataAccess;

import android.os.AsyncTask;
import android.util.Log;

import com.example.marni.orderapp.Domain.Secret;

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

public class GetSecret2Task extends AsyncTask<String, Void, String> {

    private final String TAG = getClass().getSimpleName();

    private OnSecretAvailable listener = null;

    public GetSecret2Task(OnSecretAvailable listener) {
        this.listener = listener;
    }

    protected String doInBackground(String... params) {

        InputStream inputStream = null;
        int responsCode = -1;
        String personUrl = params[0];
        String response = "";

        Log.i(TAG, "doInBackground - " + personUrl);
        try {
            URL url = new URL(personUrl);
            URLConnection urlConnection = url.openConnection();

            if (!(urlConnection instanceof HttpURLConnection)) {
                return null;
            }

            HttpURLConnection httpConnection = (HttpURLConnection) urlConnection;
            httpConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            httpConnection.setRequestMethod("GET");
            httpConnection.setRequestProperty("Authorization", "Bearer " + params[1]);

            Log.i(TAG, "params[1]: " + params[1]);

            httpConnection.connect();

            responsCode = httpConnection.getResponseCode();
            if (responsCode == HttpURLConnection.HTTP_OK) {
                inputStream = httpConnection.getInputStream();
                response = getStringFromInputStream(inputStream);
            } else {
                Log.e(TAG, "Error, invalid response. ResponseCode: " + responsCode);
            }
        } catch (MalformedURLException e) {
            Log.e(TAG, "doInBackground MalformedURLEx " + e.getLocalizedMessage());
            return null;
        } catch (IOException e) {
            Log.e(TAG, "doInBackground IOException " + e.getLocalizedMessage());
            return null;
        }

        return response;
    }

    protected void onPostExecute(String response) {

        Log.i(TAG, "onPostExecute " + response);

        if (response == null || response == "") {
            Log.e(TAG, "onPostExecute kreeg een lege response!");
            return;
        }

        try {
            JSONArray jsonArray = new JSONArray(response);
            JSONArray jsonArray2 = jsonArray.getJSONArray(0);

            Log.i(TAG, "results.length(): " + jsonArray2.length());

            for (int idx = 0; idx < jsonArray2.length(); idx++) {
                JSONObject secret = jsonArray2.getJSONObject(idx);

                int id = secret.getInt("id");
                String email = secret.getString("email");
                String password = secret.getString("password");

                Secret s = new Secret();
                s.setId(id);
                s.setEmail(email);
                s.setPassword(password);

                listener.onSecretAvailable(s);
            }
        } catch (JSONException ex) {
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

    public interface OnSecretAvailable {
        void onSecretAvailable(Secret secret);
    }
}
