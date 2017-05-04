package com.example.marni.orderapp.OrderApp;

import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

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
import java.util.Objects;

import static android.content.ContentValues.TAG;

/**
 * Created by marni on 4-5-2017.
 */

public class LoginTask extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... params) {

        InputStream inputStream = null;
        int responsCode = -1;
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

        return response;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected void onPostExecute(String response) {

        Log.i(TAG, "onPostExecute " + response);

        if(response == null || Objects.equals(response, "")) {
            Log.e(TAG, "onPostExecute kreeg een lege response!");
            return;
        }

        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(response);

            JSONArray movies = jsonObject.getJSONArray("results");
            for(int idx = 0; idx < movies.length(); idx++) {
                JSONObject movie = movies.getJSONObject(idx);

                String movieId;
                String title;
                String date;
                String summary;
                String longDescription;
                String posterImageUrl;
                String backdropImageUrl;
                Float movieRating;

                if (movie.has("release_date")) {
                    date = movie.getString("release_date");
                } else {
                    date = "";
                }

                if (movie.has("longDescription")) {
                    longDescription = movie.getString("longDescription");
                } else {
                    longDescription = "";
                }

                if (movie.has("title")) {
                    title = movie.getString("title");
                } else {
                    title = "";
                }

                if (movie.has("overview")) {
                    summary = movie.getString("overview");
                } else {
                    summary = "";
                }

                if (!Objects.equals(movie.getString("poster_path"), "null")) {
                    posterImageUrl = "https://image.tmdb.org/t/p/w500/" + movie.getString("poster_path");
                } else {
                    posterImageUrl = "http://nubisonline.nl/cinecenter/cinecenter_poster.png";
                }

                if (!Objects.equals(movie.getString("backdrop_path"), "null")) {
                    backdropImageUrl = "https://image.tmdb.org/t/p/w500/" + movie.getString("backdrop_path");
                } else {
                    backdropImageUrl = "http://nubisonline.nl/cinecenter/cinecenter_back.png";
                }

                if (movie.has("vote_average")) {
                    movieRating = Float.parseFloat(movie.getString("vote_average"));
                } else {
                    movieRating = 0.0f;
                }

                if (movie.has("id")) {
                    movieId = movie.getString("id");
                } else {
                    movieId = "";
                }

                // Create new movie object
//                Movie p = new Movie();
//                p.setMovieId(movieId);
//                p.setTitle(title);
//                p.setDate(date);
//                p.setSummary(summary);
//                p.setLongDescription(longDescription);
//                p.setPosterImageUrl(posterImageUrl);
//                p.setBackdropImageUrl(backdropImageUrl);
//                p.setMovieRating(movieRating);
//
//                //
//                // call back with new person data
//                //
//                listener.onMovieItemAvailable(p);

            }
        } catch( JSONException ex) {
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

    public interface SuccessListener {
        void on();
    }
}
