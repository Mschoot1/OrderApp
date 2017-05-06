package com.example.marni.orderapp.DataAccess;

import android.os.AsyncTask;
import android.util.Log;

import com.example.marni.orderapp.Domain.Category;
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

    private static final String TAG = ProductsTask.class.getSimpleName();

    public ProductsTask(OnProductAvailable listener){
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
        try {
            // Maak een URL object
            URL url = new URL(personUrl);
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
            Log.e("TAG", "doInBackground IOException " + e.getLocalizedMessage());
            return null;
        }

        // Hier eindigt deze methode.
        // Het resultaat gaat naar de onPostExecute methode.
        return response;
    }

    protected void onPostExecute(String response) {

        Log.i(TAG, "onPostExecute " + response);

        // Check of er een response is
        if(response == null || response == "") {
            Log.e(TAG, "onPostExecute kreeg een lege response!");
            return;
        }

        // Het resultaat is in ons geval een stuk tekst in JSON formaat.
        // Daar moeten we de info die we willen tonen uit filteren (parsen).
        // Dat kan met een JSONObject.
        JSONArray products;

        try {
            // Top level json object
            products = new JSONArray(response);

            // Get all products and start looping
            for (int idx = 0; idx < products.length(); idx++) {
                // array level objects and get user
                JSONObject product = products.getJSONObject(idx);

                Integer id = product.getInt("id");
                String productname = product.getString("productname");

                // Create new SpotifyItem object
                Product p = new Product();
                p.setName(productname);

                Log.d(TAG, "onPostExecute: " + idx + " " + productname);
                //
                // call back with new person data
                //
                listener.onProductAvailable(p);

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

    public interface OnProductAvailable {
        void onProductAvailable(Product product);
    }
}
