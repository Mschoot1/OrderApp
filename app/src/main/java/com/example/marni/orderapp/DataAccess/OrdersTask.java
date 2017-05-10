package com.example.marni.orderapp.DataAccess;

import android.os.AsyncTask;
import android.util.Log;

import com.example.marni.orderapp.Domain.Order;

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
 * Created by marni on 8-5-2017.
 */

public class OrdersTask extends AsyncTask<String, Void, String> {

    private final String TAG = getClass().getSimpleName();

    private OrdersTask.OnOrderAvailable listener = null;

    public OrdersTask(OrdersTask.OnOrderAvailable listener) {
        this.listener = listener;
    }

    protected String doInBackground(String... params) {

        InputStream inputStream = null;
        int responsCode = -1;
        String personUrl = params[0];
        String response = "";

        Log.i(TAG, "doInBackground - " + personUrl);
        try {

//            URL url = new URL(personUrl);
//            URLConnection urlConnection = url.openConnection();
//
//            if (!(urlConnection instanceof HttpURLConnection)) {
//                return null;
//            }
//
//            HttpURLConnection httpConnection = (HttpURLConnection) urlConnection;
//            httpConnection.setAllowUserInteraction(false);
//            httpConnection.setInstanceFollowRedirects(true);
//            httpConnection.setRequestMethod("GET");
//
//            httpConnection.connect();
//
//            responsCode = httpConnection.getResponseCode();
//            if (responsCode == HttpURLConnection.HTTP_OK) {
//                inputStream = httpConnection.getInputStream();
//                response = getStringFromInputStream(inputStream);
//            } else {
//                Log.e(TAG, "Error, invalid response");
//            }

            // dummy data
            response = getDummyData().toString();
            Log.i(TAG, "Response: " + response);
            //

//        } catch (MalformedURLException e) {
//            Log.e(TAG, "doInBackground MalformedURLEx " + e.getLocalizedMessage());
//            return null;
//        } catch (IOException e) {
//            Log.e(TAG, "doInBackground IOException " + e.getLocalizedMessage());
//            return null;
        } catch (JSONException e) {
            e.printStackTrace();
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
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("results");

            Log.i(TAG, "results.length(): " + jsonArray.length());

            for (int idx = 0; idx < jsonArray.length(); idx++) {
                JSONObject order = jsonArray.getJSONObject(idx);

                Integer orderId = order.getInt("orderId");
                String status = order.getString("status");
                String dateTime = order.getString("dateTime");
                Double totalPrice = order.getDouble("totalPrice");

                Order o = new Order();
                o.setOrderId(orderId);
                o.setStatus(status);
                o.setDateTime(dateTime);
                o.setTotalPrice(totalPrice);

                listener.onOrderAvailable(o);
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

    public interface OnOrderAvailable {
        void onOrderAvailable(Order order);
    }

    private JSONObject getDummyData() throws JSONException {

        Log.i(TAG, "getDummyData() called.");

        JSONObject results = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        results.put("results", jsonArray);

        for (int i = 0; i < 10; i++) {

            JSONObject jsonObject;
            jsonObject = new JSONObject();
            jsonObject.put("orderId", i);
            jsonObject.put("status", "Paid");
            jsonObject.put("dateTime", "8-5-2017 18:56");
            jsonObject.put("totalPrice", 10.00);

            jsonArray.put(jsonObject);
        }

        return results;
    }
}
