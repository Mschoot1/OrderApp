package com.example.marni.orderapp.dataaccess.category;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.example.marni.orderapp.domain.Category;
import com.example.marni.orderapp.R;

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

public class CategoriesGetTask extends AsyncTask<String, Void, String> {

    private ProgressBar progressBar;

    private OnCategoryAvailable oca;

    private final String TAG = getClass().getSimpleName();

    public CategoriesGetTask(View view, OnCategoryAvailable oca) {
        this.progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        this.oca = oca;
    }

    @Override
    public void onPreExecute() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected String doInBackground(String... params) {

        InputStream inputStream = null;
        int responsCode = -1;
        // De URL die we via de .execute() meegeleverd krijgen
        String productsUrl = params[0];
        // Het resultaat dat we gaan retourneren
        String response = "";

        Log.i(TAG, "doInBackground - " + productsUrl);
        try {
            // Maak een URL object
            URL url = new URL(productsUrl);
            // Open een connection op de URL
            URLConnection urlConnection = url.openConnection();

            if (!(urlConnection instanceof HttpURLConnection)) {
                return null;
            }

            // Initialiseer een HTTP connectie
            HttpURLConnection httpConnection = (HttpURLConnection) urlConnection;
            httpConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            httpConnection.setRequestMethod("GET");
            httpConnection.setRequestProperty("Authorization", "Bearer " + params[1]);

            // Voer het request uit via de HTTP connectie op de URL
            httpConnection.connect();

            // Kijk of het gelukt is door de response code te checken
            responsCode = httpConnection.getResponseCode();
            if (responsCode == HttpURLConnection.HTTP_OK) {
                inputStream = httpConnection.getInputStream();
                response = getStringFromInputStream(inputStream);
                // Log.i(TAG, "doInBackground response = " + response);
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

        return response;
    }

    protected void onPostExecute(String response) {
        progressBar.setVisibility(View.INVISIBLE);
        Log.i(TAG, "onPostExecute " + response);

        if (response == null || response == "") {
            Log.e(TAG, "onPostExecute kreeg een lege response!");
            return;
        }

        JSONObject jsonObject;

        try {
            jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("results");

            Log.i(TAG, "results.length(): " + jsonArray.length());

            for (int idx = 0; idx < jsonArray.length(); idx++) {
                JSONObject category = jsonArray.getJSONObject(idx);

                Category c = new Category();

                String name = category.getString("name");
                int id = category.getInt("id");

                c.setCategoryName(name);
                c.setCategoryId(id);

                oca.onCategoryAvailable(c);
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
            Log.e("", "getStringFromInputStream " + e.getLocalizedMessage());
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    Log.e("", "getStringFromInputStream " + e.getLocalizedMessage());
                }
            }
        }

        return sb.toString();
    }

    public interface OnCategoryAvailable {
        void onCategoryAvailable(Category category);
    }
}
