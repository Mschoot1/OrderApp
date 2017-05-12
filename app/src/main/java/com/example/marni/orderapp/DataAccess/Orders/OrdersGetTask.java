package com.example.marni.orderapp.DataAccess.Orders;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by marni on 8-5-2017.
 */

public class OrdersGetTask extends AsyncTask<String, Void, String> {

    private final String TAG = getClass().getSimpleName();

    private OrdersGetTask.OnOrderAvailable listener = null;

    public OrdersGetTask(OrdersGetTask.OnOrderAvailable listener) {
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
            httpConnection.setAllowUserInteraction(false);
            httpConnection.setInstanceFollowRedirects(true);
            httpConnection.setRequestMethod("GET");

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
//        response = "{\"results\":[{\"id\":4,\"status\":1,\"timestamp\":\"2017-05-10T20:23:22.000Z\",\"price_total\":10,\"customer_id\":284},{\"id\":14,\"status\":1,\"timestamp\":\"2017-05-10T22:23:34.000Z\",\"price_total\":30,\"customer_id\":284},{\"id\":24,\"status\":0,\"timestamp\":\"2017-05-10T20:24:33.000Z\",\"price_total\":13,\"customer_id\":284}]}";

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

                int id = order.getInt("id");
                int status = order.getInt("status");
                String timestamp = order.getString("timestamp");
                Double price_total = order.getDouble("price_total");

                Order o = new Order();
                o.setOrderId(id);
                o.setStatus(status);
                o.setTimestamp(getFormattedDate(timestamp));
                o.setPriceTotal(price_total);

                listener.onOrderAvailable(o);
            }
        } catch (JSONException ex) {
            Log.e(TAG, "onPostExecute JSONException " + ex.getLocalizedMessage());
        } catch (ParseException e) {
            e.printStackTrace();
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

    private String getFormattedDate(String s) throws ParseException {

        SimpleDateFormat sdf;
        sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date parsedDate = sdf.parse(s);
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = sdf.format(parsedDate);

        return formattedDate;
    }

    public interface OnOrderAvailable {
        void onOrderAvailable(Order order);
    }
}
