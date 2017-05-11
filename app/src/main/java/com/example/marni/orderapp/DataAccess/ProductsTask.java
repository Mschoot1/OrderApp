package com.example.marni.orderapp.DataAccess;

import android.os.AsyncTask;
import android.util.Log;

import com.example.marni.orderapp.Domain.Product;

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

public class ProductsTask extends AsyncTask<String, Void, String> {

    private OnProductAvailable listener = null;

    private final String TAG = getClass().getSimpleName();

    public ProductsTask(OnProductAvailable listener) {
        this.listener = listener;
    }

    protected String doInBackground(String... params) {

        InputStream inputStream = null;
        int responsCode = -1;
        // De URL die we via de .execute() meegeleverd krijgen
        String productsUrl = params[0];
        // Het resultaat dat we gaan retourneren
        String response = "";

//        Log.i(TAG, "doInBackground - " + productsUrl);

        Log.i(TAG, "doInBackground - " + productsUrl);
//        try {
//            // Maak een URL object
//            URL url = new URL(productsUrl);
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
//        } catch (MalformedURLException e) {
//            Log.e(TAG, "doInBackground MalformedURLEx " + e.getLocalizedMessage());
//            return null;
//        } catch (IOException e) {
//            Log.e(TAG, "doInBackground IOException " + e.getLocalizedMessage());
//            return null;
//        }

        response = "{\"results\":[{\"id\":14,\"name\":\"Beer\",\"price\":2,\"size\":330,\"alcohol\":5,\"category_id\":1,\"category_name\":\"Alcoholic beverage\",\"quantity\":2},{\"id\":14,\"name\":\"Beer\",\"price\":2,\"size\":330,\"alcohol\":5,\"category_id\":1,\"category_name\":\"Alcoholic beverage\",\"quantity\":3},{\"id\":34,\"name\":\"Wine\",\"price\":3.5,\"size\":150,\"alcohol\":12,\"category_id\":1,\"category_name\":\"Alcoholic beverage\",\"quantity\":2},{\"id\":44,\"name\":\"Cider\",\"price\":3,\"size\":250,\"alcohol\":5,\"category_id\":1,\"category_name\":\"Alcoholic beverage\",\"quantity\":2},{\"id\":54,\"name\":\"Whiskey\",\"price\":4,\"size\":50,\"alcohol\":40,\"category_id\":1,\"category_name\":\"Alcoholic beverage\",\"quantity\":6},{\"id\":64,\"name\":\"Cola\",\"price\":1.5,\"size\":250,\"alcohol\":0,\"category_id\":2,\"category_name\":\"Non-alcoholic beverage\",\"quantity\":4},{\"id\":74,\"name\":\"Fanta\",\"price\":1.5,\"size\":250,\"alcohol\":0,\"category_id\":2,\"category_name\":\"Non-alcoholic beverage\",\"quantity\":0},{\"id\":84,\"name\":\"Sprite\",\"price\":1.5,\"size\":250,\"alcohol\":0,\"category_id\":2,\"category_name\":\"Non-alcoholic beverage\",\"quantity\":0},{\"id\":94,\"name\":\"Water\",\"price\":1.5,\"size\":250,\"alcohol\":0,\"category_id\":2,\"category_name\":\"Non-alcoholic beverage\",\"quantity\":0}]}";

        Log.i(TAG, "response: " + response);

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

        JSONObject jsonObject;

        try {
            jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("results");

            Log.i(TAG, "results.length(): " + jsonArray.length());

            for (int idx = 0; idx < jsonArray.length(); idx++) {
                JSONObject product = jsonArray.getJSONObject(idx);

                Integer id = product.getInt("id");
                String name = product.getString("name");
                Double price = product.getDouble("price");
                int size = product.getInt("size");
                Double alcohol = product.getDouble("alcohol");
                int categoryId = product.getInt("category_id");
                String categoryName = product.getString("category_name");
                int quantity = product.getInt("quantity");

                Product p = new Product();
                p.setProductId(id);
                p.setName(name);
                p.setPrice(price);
                p.setSize(size);
                p.setAlcohol_percentage(alcohol);
                p.setCategoryId(categoryId);
                p.setQuantity(quantity);
                p.setCategoryName(categoryName);

                listener.onProductAvailable(p);

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

    public interface OnProductAvailable {
        void onProductAvailable(Product product);
    }
}
