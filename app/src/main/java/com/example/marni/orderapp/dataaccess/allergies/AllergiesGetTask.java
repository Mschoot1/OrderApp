package com.example.marni.orderapp.dataaccess.allergies;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import com.example.marni.orderapp.domain.Allergy;
import com.example.marni.orderapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import static com.example.marni.orderapp.dataaccess.account.AccountGetTask.getStringFromInputStream;

public class AllergiesGetTask extends AsyncTask<String, Void, String> {

    private final String tag = getClass().getSimpleName();

    private OnRandomUserAvailable listener = null;

    private ProgressBar progressBar;

    public AllergiesGetTask(Activity activity) {
        this.listener = (OnRandomUserAvailable) activity;
        this.progressBar = (ProgressBar) activity.findViewById(R.id.progress_bar);
    }

    @Override
    public void onPreExecute() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected String doInBackground(String... params) {
        InputStream inputStream;
        int responseCode;
        String balanceUrl = params[0];
        String response = "";

        try {
            URL url = new URL(balanceUrl);
            URLConnection urlConnection = url.openConnection();

            if (!(urlConnection instanceof HttpURLConnection)) {
                return null;
            }

            HttpURLConnection httpConnection = (HttpURLConnection) urlConnection;
            httpConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            httpConnection.setRequestMethod("GET");
            httpConnection.setRequestProperty("Authorization", "Bearer " + params[1]);

            httpConnection.connect();

            responseCode = httpConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                inputStream = httpConnection.getInputStream();
                response = getStringFromInputStream(inputStream);
            } else {
                Log.e("", "Error, invalid response");
            }
        } catch (MalformedURLException e) {
            Log.e("", "doInBackground MalformedURLEx " + e.getLocalizedMessage());
            return null;
        } catch (IOException e) {
            Log.e("", "doInBackground IOException " + e.getLocalizedMessage());
            return null;
        }

        return response;
    }

    @Override
    protected void onPostExecute(String response) {
        progressBar.setVisibility(View.INVISIBLE);
        JSONArray jsonArray;
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(response);
            jsonArray = jsonObject.getJSONArray("results");


            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject allergy = jsonArray.getJSONObject(i);

                String allergieimage = allergy.getString("image");
                String allergieinformatie = allergy.getString("description");

                Allergy a = new Allergy(allergieimage, allergieinformatie);

                listener.onRandomUserAvailable(a);
            }
        } catch (JSONException e) {
            Log.e(tag, "doInBackground JSONException " + e.getLocalizedMessage());
        }
    }

    public interface OnRandomUserAvailable {
        void onRandomUserAvailable(Allergy allergy);
    }
}

