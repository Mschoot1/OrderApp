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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class AllergiesGetTask extends AsyncTask<String, Void, String> {

    private final String TAG = getClass().getSimpleName();

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
            httpConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            httpConnection.setRequestMethod("GET");
            httpConnection.setRequestProperty("Authorization", "Bearer " + params[1]);

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
            Log.e(TAG, "doInBackground JSONException " + e.getLocalizedMessage());
        }
    }

    public interface OnRandomUserAvailable {
        void onRandomUserAvailable(Allergy allergy);
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

}

