package com.example.marni.orderapp.dataaccess.product;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.example.marni.orderapp.domain.Allergy;
import com.example.marni.orderapp.domain.Product;
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
import java.util.ArrayList;

import static com.example.marni.orderapp.dataaccess.account.AccountGetTask.getStringFromInputStream;

public class ProductsGetTask extends AsyncTask<String, Void, String> {

    private OnProductAvailable opa = null;
    private OnEmptyList oel = null;
    private String myorder;

    private ProgressBar progressBar;

    private final String tag = getClass().getSimpleName();

    public ProductsGetTask(Activity activity, String myorder) {
        this.opa = (OnProductAvailable) activity;
        this.oel = (OnEmptyList) activity;
        this.myorder = myorder;
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
                JSONObject product = jsonArray.getJSONObject(idx);

                Product p = new Product();

                if(myorder.equals("myorder")){
                    Integer id = product.getInt("product_id");
                    p.setProductId(id);
                } else {
                    Integer id = product.getInt("id");
                    p.setProductId(id);
                }

                JSONArray allergies = product.getJSONArray("allergies");

                Log.i(tag, "allergies.length(): " + allergies.length());

                ArrayList<Allergy> as = new ArrayList<>();
                for (int j = 0; j < allergies.length(); j++) {

                    JSONObject allergy = allergies.getJSONObject(j);
                    Allergy a = new Allergy(allergy.getString("image"), allergy.getString("description"));
                    as.add(a);
                }

                String name = product.getString("name");
                Double price = product.getDouble("price");
                int size = product.getInt("size");
                Double alcohol = product.getDouble("alcohol");
                int categoryId = product.getInt("category_id");
                String categoryName = product.getString("category_name");
                int quantity = product.getInt("quantity");
                String imagesrc = product.getString("product_image");

                if(product.has("order_id")) {
                    int orderId = product.getInt("order_id");
                    p.setOrderId(orderId);
                }

                p.setName(name);
                p.setPrice(price);
                p.setSize(size);
                p.setAlcoholPercentage(alcohol);
                p.setCategoryId(categoryId);
                p.setQuantity(quantity);
                p.setCategoryName(categoryName);
                p.setAllergies(as);
                p.setImagesrc(imagesrc);

                opa.onProductAvailable(p);
            }
            if (jsonArray.length() == 0) {
                oel.isEmpty(true);
            }
        } catch (JSONException ex) {
            Log.e(tag, "onPostExecute JSONException " + ex.getLocalizedMessage());
        }
    }

    public interface OnProductAvailable {
        void onProductAvailable(Product product);
    }

    public interface OnEmptyList {
        void isEmpty(Boolean b);
    }
}
