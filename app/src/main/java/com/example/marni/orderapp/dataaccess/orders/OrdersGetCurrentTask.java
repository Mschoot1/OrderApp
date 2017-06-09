package com.example.marni.orderapp.dataaccess.orders;

import android.os.AsyncTask;
import android.util.Log;

import com.example.marni.orderapp.domain.Order;

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

public class OrdersGetCurrentTask extends AsyncTask<String, Void, String> {

    private final String TAG = getClass().getSimpleName();

    private OnCurrentOrderAvailable listener = null;

    public OrdersGetCurrentTask(OnCurrentOrderAvailable listener) {
        this.listener = listener;
    }

    @Override
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
                int pending = order.getInt("pending");
                String timestamp = order.getString("timestamp");
                Double price_total = order.getDouble("price_total");

                Order o = new Order();
                o.setOrderId(id);
                o.setStatus(status);
                o.setTimestamp(getFormattedDate(timestamp));
                o.setPriceTotal(price_total);
                o.setPending(pending);

                listener.onCurrentOrderAvailable(o);
            }
        } catch (JSONException ex) {
            Log.e(TAG, "onPostExecute JSONException " + ex.getLocalizedMessage());
        } catch (ParseException e) {
            Log.e(TAG, "onPostExecute ParseException " + e.getLocalizedMessage());
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

    private String getFormattedDate(String s) throws ParseException {

        SimpleDateFormat sdf;
        sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date parsedDate = sdf.parse(s);
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = sdf.format(parsedDate);

        return formattedDate;
    }

    public interface OnCurrentOrderAvailable {
        void onCurrentOrderAvailable(Order order);
    }
}
