package com.example.marni.orderapp.DataAccess.Product;

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

public class ProductsGetTask extends AsyncTask<String, Void, String> {

    private OnProductAvailable listener = null;
    private String myorder;

    private final String TAG = getClass().getSimpleName();

    public ProductsGetTask(OnProductAvailable listener, String myorder) {
        this.listener = listener;
        this.myorder = myorder;
    }

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
            httpConnection.setAllowUserInteraction(false);
            httpConnection.setInstanceFollowRedirects(true);
            httpConnection.setRequestMethod("GET");

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

                Product p = new Product();

                if(myorder.equals("myorder")){
                    Integer id = product.getInt("product_id");
                    p.setProductId(id);
                } else {
                    Integer id = product.getInt("id");
                    p.setProductId(id);
                }

                String name = product.getString("name");
                Double price = product.getDouble("price");
                int size = product.getInt("size");
                Double alcohol = product.getDouble("alcohol");
                int categoryId = product.getInt("category_id");
                String categoryName = product.getString("category_name");
                int quantity = product.getInt("quantity");

                if(product.has("order_id")) {
                    int orderId = product.getInt("order_id");
                    p.setOrderId(orderId);
                }

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
