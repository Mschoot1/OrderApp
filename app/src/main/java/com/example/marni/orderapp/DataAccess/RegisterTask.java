package com.example.marni.orderapp.DataAccess;

import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import static android.content.ContentValues.TAG;

/**
 * Created by marni on 4-5-2017.
 */

@RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
public class RegisterTask extends AsyncTask<String, Void, Boolean> {

    private SuccessListener listener;

    public RegisterTask(SuccessListener listener) {

        this.listener = listener;
    }

    @Override
    protected Boolean doInBackground(String... params) {

        int responseCode;
        String MovieUrl = params[0];
        String email = params[1];
        String password = params[2];
        Boolean response;

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

            httpConnection.setRequestProperty("email", email);
            httpConnection.setRequestProperty("password", password);

            //httpConnection.connect();

            responseCode = httpConnection.getResponseCode();
            response = (responseCode == HttpURLConnection.HTTP_OK);
        } catch (MalformedURLException e) {
            Log.e(TAG, "doInBackground MalformedURLEx " + e.getLocalizedMessage());
            return null;
        } catch (IOException e) {
            Log.e(TAG, "doInBackground IOException " + e.getLocalizedMessage());
            return null;
        }

        return response;
    }

    protected void onPostExecute(Boolean response) {

        listener.successful(response);
    }

    public interface SuccessListener {
        void successful(Boolean successful);
    }
}
