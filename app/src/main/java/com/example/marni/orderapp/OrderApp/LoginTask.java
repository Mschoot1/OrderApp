package com.example.marni.orderapp.OrderApp;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import static android.content.ContentValues.TAG;

/**
 * Created by marni on 4-5-2017.
 */

class LoginTask extends AsyncTask<String, Void, String> {

    private SuccessListener listener;

    LoginTask(SuccessListener listener) {

        this.listener = listener;
    }

    @Override
    protected String doInBackground(String... params) {

        int responseCode;
        String MovieUrl = params[0];
        String response = "";

        Log.i(TAG, "doInBackground - " + MovieUrl);
        try {
            URL url = new URL(MovieUrl);
            URLConnection urlConnection = url.openConnection();

            if (!(urlConnection instanceof HttpURLConnection)) {
                return null;
            }

            HttpURLConnection httpConnection = (HttpURLConnection) urlConnection;
            httpConnection.setAllowUserInteraction(false);
            httpConnection.setInstanceFollowRedirects(true);
            httpConnection.setRequestMethod("POST");

            httpConnection.connect();

            responseCode = httpConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {

                listener.successful(true);
            } else {
                listener.successful(false);
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

    interface SuccessListener {
        void successful(Boolean successful);
    }
}
