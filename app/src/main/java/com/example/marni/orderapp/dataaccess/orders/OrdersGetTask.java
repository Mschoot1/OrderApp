package com.example.marni.orderapp.dataaccess.orders;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.example.marni.orderapp.domain.Order;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.example.marni.orderapp.dataaccess.account.AccountGetTask.getStringFromInputStream;

public class OrdersGetTask extends AsyncTask<String, Void, String> {

    private final String tag = getClass().getSimpleName();

    private OnOrderAvailable listener = null;

    public OrdersGetTask(Activity activity) {
        this.listener = (OnOrderAvailable) activity;
    }


    @Override
    protected String doInBackground(String... params) {

        InputStream inputStream = null;
        int responsCode = -1;
        String personUrl = params[0];
        String response = "";

        Log.i(tag, "doInBackground - " + personUrl);
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
                Log.e(tag, "Error, invalid response");
            }
        } catch (MalformedURLException e) {
            Log.e(tag, "doInBackground MalformedURLEx " + e.getLocalizedMessage());
            return null;
        } catch (IOException e) {
            Log.e(tag, "doInBackground IOException " + e.getLocalizedMessage());
            return null;
        }

        return response;
    }

    @Override
    protected void onPostExecute(String response) {
        Log.i(tag, "onPostExecute " + response);

        if (response == null || response == "") {
            Log.e(tag, "onPostExecute kreeg een lege response!");
            return;
        }

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("results");

            Log.i(tag, "results.length(): " + jsonArray.length());

            for (int idx = 0; idx < jsonArray.length(); idx++) {
                JSONObject order = jsonArray.getJSONObject(idx);

                int id = order.getInt("id");
                int status = order.getInt("status");
                String timestamp = order.getString("timestamp");
                Double priceTotal = order.getDouble("price_total");
                int pending = order.getInt("pending");

                Order o = new Order();
                o.setOrderId(id);
                o.setStatus(status);
                o.setTimestamp(getFormattedDate(timestamp));
                o.setPriceTotal(priceTotal);
                o.setPending(pending);

                listener.onOrderAvailable(o);
            }
        } catch (JSONException ex) {
            Log.e(tag, "onPostExecute JSONException " + ex.getLocalizedMessage());
        } catch (ParseException e) {
            Log.e(tag, "onPostExecute ParseException " + e.getLocalizedMessage());
        }
    }

    private String getFormattedDate(String s) throws ParseException {

        SimpleDateFormat sdf;
        sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date parsedDate = sdf.parse(s);
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        return sdf.format(parsedDate);
    }

    public interface OnOrderAvailable {
        void onOrderAvailable(Order order);
    }
}
