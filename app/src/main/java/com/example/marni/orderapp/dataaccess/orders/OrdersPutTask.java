package com.example.marni.orderapp.dataaccess.orders;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class OrdersPutTask extends AsyncTask<String, Void, Boolean> {

    private final String TAG = getClass().getSimpleName();

    private PutSuccessListener listener;

    public OrdersPutTask(PutSuccessListener listener) {
        this.listener = listener;
    }

    @Override
    protected Boolean doInBackground(String... params) {

        int responseCode;
        String balanceUrl = params[0];

        Boolean response = null;

        Log.i(TAG, "doInBackground - " + balanceUrl);
        try {
            URL url = new URL(balanceUrl);
            URLConnection urlConnection = url.openConnection();

            if (!(urlConnection instanceof HttpURLConnection)) {
                return null;
            }

            HttpURLConnection httpConnection = (HttpURLConnection) urlConnection;

            httpConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            httpConnection.setRequestMethod("PUT");
            httpConnection.setRequestProperty("Authorization", "Bearer " + params[1]);

            JSONObject jsonParam = new JSONObject();
            jsonParam.put("price_total", params[2]);
            jsonParam.put("order_id", params[3]);

            Log.i(TAG, String.valueOf(jsonParam));

            DataOutputStream localDataOutputStream = new DataOutputStream(httpConnection.getOutputStream());
            localDataOutputStream.writeBytes(jsonParam.toString());
            localDataOutputStream.flush();
            localDataOutputStream.close();
            httpConnection.connect();

            responseCode = httpConnection.getResponseCode();
            response = (responseCode == HttpURLConnection.HTTP_OK);
        } catch (MalformedURLException e) {
            Log.e(TAG, "doInBackground MalformedURLEx " + e.getLocalizedMessage());
            return null;
        } catch (IOException e) {
            Log.e(TAG, "doInBackground IOException " + e.getLocalizedMessage());
            return null;
        } catch (JSONException e) {
            Log.e(TAG, "doInBackground JSONException " + e.getLocalizedMessage());
        }

        return response;
    }

    protected void onPostExecute(Boolean response) {
        listener.putSuccessful(response);
    }

    public interface PutSuccessListener {
        void putSuccessful(Boolean successful);
    }
}
