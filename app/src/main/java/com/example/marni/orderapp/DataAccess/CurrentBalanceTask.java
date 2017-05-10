package com.example.marni.orderapp.DataAccess;

/**
 * Created by MSI-PC on 9-5-2017.
 */

import android.os.AsyncTask;
import android.util.Log;

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

public class CurrentBalanceTask extends AsyncTask<String, Void, String> {

    // Call back
    private CurrentBalanceListener listener = null;

    // Constructor, set listener
    public CurrentBalanceTask(CurrentBalanceListener listener) {
        this.listener = listener;
    }

    /**
     * doInBackground is de methode waarin de aanroep naar een service op het Internet gedaan wordt.
     */

    @Override
    protected String doInBackground(String... params) {

        InputStream inputStream = null;
        int responsCode = -1;

        String BalanceUrl = params[0];

        String response = "";

        try {
            URL url = new URL(BalanceUrl);
            URLConnection urlConnection = url.openConnection();

            if (!(urlConnection instanceof HttpURLConnection)) {
                return null;
            }

            HttpURLConnection httpConnection = (HttpURLConnection) urlConnection;
            httpConnection.setAllowUserInteraction(false);
            httpConnection.setInstanceFollowRedirects(true);
            httpConnection.setRequestMethod("GET");

            httpConnection.connect();

            responsCode = httpConnection.getResponseCode();
            if (responsCode == HttpURLConnection.HTTP_OK) {
                inputStream = httpConnection.getInputStream();
                response = getStringFromInputStream(inputStream);
                 Log.i("", "doInBackground response = " + response);
            } else {
                Log.e("", "Error, invalid response");
            }
        } catch (MalformedURLException e) {
            Log.e("", "doInBackground MalformedURLEx " + e.getLocalizedMessage());
            return null;
        } catch (IOException e) {
            Log.e("TAG", "doInBackground IOException " + e.getLocalizedMessage());
            return null;
        }
        return response;
    }

    protected void onPostExecute(String response) {

        Log.i("", "onPostExecute " + response);

        // Check of er een response is
        if(response == null || response == "") {
            Log.e("", "onPostExecute kreeg een lege response!");
            return;
        }

        JSONObject jsonObject;
        try {
            // Top level json object
            jsonObject = new JSONObject(response);

            JSONArray products = jsonObject.getJSONArray("products");
            for(int idx = 0; idx < products.length(); idx++) {
                // array level objects and get product
                JSONObject product = products.getJSONObject(idx);

                String title;
                String specsTag;
                String summary;
                String longDescription;

                if (product.has("title")) {
                    title = product.getString("title");
                } else {
                    title = "";
                }

                if (product.has("specsTag")) {
                    specsTag = product.getString("specsTag");
                } else {
                    specsTag = "";
                }

                if (product.has("summary")) {
                    summary = product.getString("summary");
                } else {
                    summary = "";
                }

                if (product.has("longDescription")) {
                    longDescription = product.getString("longDescription");
                } else {
                    longDescription = "";
                }



                //
                // call back with new person data
                //
                listener.oncurrentBalanceAvailable("text");

            }
        } catch( JSONException ex) {
            Log.e("", "onPostExecute JSONException " + ex.getLocalizedMessage());
        }
    }


    //
    // convert InputStream to String
    //
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

    // Call back interface
    public interface CurrentBalanceListener {
        void oncurrentBalanceAvailable(String product);
    }
}

