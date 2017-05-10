package com.example.marni.orderapp.DataAccess;

import android.os.AsyncTask;
import android.util.Log;

import com.example.marni.orderapp.Domain.Category;

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

/**
 * Created by MarcdenUil on 6-5-2017.
 */

public class CategoriesTask extends AsyncTask<String, Void, String> {

    private OnCategoryAvailable listener = null;

    private static final String TAG = CategoriesTask.class.getSimpleName();

    public CategoriesTask(OnCategoryAvailable listener) {
        this.listener = listener;
    }

    protected String doInBackground(String... params) {

        InputStream inputStream = null;
        int responsCode = -1;
        // De URL die we via de .execute() meegeleverd krijgen
        String personUrl = params[0];
        // Het resultaat dat we gaan retourneren
        String response = "";

        Log.i(TAG, "doInBackground - " + personUrl);
//        try {
//            // Maak een URL object
//            URL url = new URL(personUrl);
//            // Open een connection op de URL
//            URLConnection urlConnection = url.openConnection();
//
//            if (!(urlConnection instanceof HttpURLConnection)) {
//                return null;
//            }
//
//            // Initialiseer een HTTP connectie
//            HttpURLConnection httpConnection = (HttpURLConnection) urlConnection;
//            httpConnection.setAllowUserInteraction(false);
//            httpConnection.setInstanceFollowRedirects(true);
//            httpConnection.setRequestMethod("GET");
//
//            // Voer het request uit via de HTTP connectie op de URL
//            httpConnection.connect();
//
//            // Kijk of het gelukt is door de response code te checken
//            responsCode = httpConnection.getResponseCode();
//            if (responsCode == HttpURLConnection.HTTP_OK) {
//                inputStream = httpConnection.getInputStream();
//                response = getStringFromInputStream(inputStream);
//                // Log.i(TAG, "doInBackground response = " + response);
//            } else {
//                Log.e(TAG, "Error, invalid response");
//            }
        response = "{\"results\":[{\"categoryId\":0,\"categoryName\":\"Alcohol\"},{\"categoryId\":1,\"categoryName\":\"Non Alcohol\"}]}";


//        } catch (MalformedURLException e) {
//            Log.e(TAG, "doInBackground MalformedURLEx " + e.getLocalizedMessage());
//            return null;
//        } catch (IOException e) {
//            Log.e("TAG", "doInBackground IOException " + e.getLocalizedMessage());
//            return null;

        // Hier eindigt deze methode.
        // Het resultaat gaat naar de onPostExecute methode.
        return response;
    }

    protected void onPostExecute(String response) {

        Log.i(TAG, "onPostExecute " + response);

        if (response == null || response == "") {
            Log.e(TAG, "onPostExecute kreeg een lege response!");
            return;
        }

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("results");

            Log.i(TAG, "results.length(): " + jsonArray.length());

            for (int idx = 0; idx < jsonArray.length(); idx++) {
                JSONObject product = jsonArray.getJSONObject(idx);

                Integer productId = product.getInt("categoryId");
                String categoryName = product.getString("categoryName");

                Category c = new Category();
                c.setCategoryId(productId);
                c.setCategoryName(categoryName);

                listener.onCategoryAvailable(c);
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

    private JSONObject getDummyData() throws JSONException {

        Log.i(TAG, "getDummyData() called.");

        JSONObject results = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        results.put("results", jsonArray);

        JSONObject jsonObject;

        jsonObject = new JSONObject();
        jsonObject.put("categoryId", 1);
        jsonObject.put("categoryName", "Alcohol");
        jsonArray.put(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("categoryId", 2);
        jsonObject.put("categoryName", "Non Alcohol");
        jsonArray.put(jsonObject);

        return results;
    }

    private JSONObject getDummyDataById(int id) throws JSONException {

        Log.i(TAG, "getDummyData() called.");

        JSONObject results = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        results.put("results", jsonArray);

        JSONObject jsonObject;

//        switch (id) {
//
//            case 1:
        jsonObject = new JSONObject();
        jsonObject.put("categoryId", 1);
        jsonObject.put("categoryName", "Alcohol");
        jsonArray.put(jsonObject);
//                break;
//            case 2:
        jsonObject = new JSONObject();
        jsonObject.put("categoryId", 2);
        jsonObject.put("categoryName", "Non Alcohol");
        jsonArray.put(jsonObject);
//                break;
//        }

        return results;
    }


    public interface OnCategoryAvailable {
        void onCategoryAvailable(Category category);
    }
}
