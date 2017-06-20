package com.example.marni.orderapp.dataaccess.category;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.example.marni.orderapp.domain.Category;
import com.example.marni.orderapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import static com.example.marni.orderapp.dataaccess.account.AccountGetTask.getStringFromInputStream;

public class CategoriesGetTask extends AsyncTask<String, Void, String> {

    private ProgressBar progressBar;

    private OnCategoryAvailable oca;

    private final String tag = getClass().getSimpleName();

    public CategoriesGetTask(View view, OnCategoryAvailable oca) {
        this.progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        this.oca = oca;
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
        progressBar.setVisibility(View.GONE);
        Log.i(tag, "onPostExecute " + response);

        if (response == null || response == "") {
            Log.e(tag, "onPostExecute kreeg een lege response!");
            return;
        }

        JSONObject jsonObject;

        try {
            jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("results");

            Log.i(tag, "results.length(): " + jsonArray.length());

            for (int idx = 0; idx < jsonArray.length(); idx++) {
                JSONObject category = jsonArray.getJSONObject(idx);

                Category c = new Category();

                String name = category.getString("name");
                int id = category.getInt("id");

                c.setCategoryName(name);
                c.setCategoryId(id);

                oca.onCategoryAvailable(c);
            }
        } catch (JSONException ex) {
            Log.e(tag, "onPostExecute JSONException " + ex.getLocalizedMessage());
        }
    }

    public interface OnCategoryAvailable {
        void onCategoryAvailable(Category category);
    }
}
