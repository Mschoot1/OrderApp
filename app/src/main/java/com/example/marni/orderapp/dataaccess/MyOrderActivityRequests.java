package com.example.marni.orderapp.dataaccess;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.marni.orderapp.domain.Order;
import com.example.marni.orderapp.domain.Product;
import com.example.marni.orderapp.domain.mappers.OrderMapper;
import com.example.marni.orderapp.domain.mappers.ProductMapper;
import com.example.marni.orderapp.presentation.activities.MyOrderActivity;
import com.example.marni.orderapp.service.Config;
import com.example.marni.orderapp.service.VolleyRequestQueue;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.marni.orderapp.presentation.activities.LoginActivity.JWT_STR;
import static com.example.marni.orderapp.presentation.activities.LoginActivity.PREF_NAME;
import static com.example.marni.orderapp.presentation.activities.LoginActivity.USER;

public class MyOrderActivityRequests {

    private Context context;
    public final String tag = this.getClass().getSimpleName();

    private MyOrderActivityListener listener;

    /**
     * Constructor
     *
     * @param context
     * @param listener
     */
    public MyOrderActivityRequests(Context context, MyOrderActivity listener) {
        this.context = context;
        this.listener = listener;
    }

    public void handleGetCurrentOrder() {
        Log.i(tag, "handleGetProducts");
        SharedPreferences sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        final String jwt = sharedPref.getString(JWT_STR, "");
        final int user = sharedPref.getInt(USER, 0);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET,
                        Config.URL_CURRENT_ORDER + user,
                        null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.i(tag, response.toString());
                                Order result = new Order();
                                try {
                                    result = OrderMapper.mapOrder(response);
                                } catch (JSONException e) {
                                    Log.e("MyOrderActivityRequests", "handleGetProducts JSONException " + e.getLocalizedMessage());
                                }
                                listener.onCurrentOrderAvailable(result);
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Error - send back to caller
                        listener.onError(error.toString());
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + jwt);
                Log.i(tag, "headers: " + headers.toString());
                return headers;
            }
        };

        // Access the RequestQueue through your singleton class.
        VolleyRequestQueue.getInstance(context).addToRequestQueue(jsObjRequest);
    }

    public void handlePutOrder(double priceTotal, int orderId) {
        Log.i(tag, "handlePutOrder");
        SharedPreferences sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        final String jwt = sharedPref.getString(JWT_STR, "");

        String body = "{\"price_total\":\"" + priceTotal + "\",\"order_id\":\"" + orderId + "\"}";

        try {
            JSONObject jsonBody = new JSONObject(body);
            Log.i(tag, "handlePutOrder - body = " + jsonBody);
            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.PUT,
                            Config.URL_PUT_PRICE_ORDER,
                            jsonBody,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Log.i(tag, response.toString());
                                    listener.onOrderPutPriceSuccess();
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // Error - send back to caller
                            listener.onError(error.toString());
                        }
                    }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    headers.put("Authorization", "Bearer " + jwt);
                    return headers;
                }
            };

            // Access the RequestQueue through your singleton class.
            VolleyRequestQueue.getInstance(context).addToRequestQueue(jsObjRequest);
        } catch (JSONException e) {
            // Error - send back to caller
            listener.onError(e.toString());
        }
    }

    public void handleGetProducts(int orderId) {
        Log.i(tag, "handleGetProducts");
        SharedPreferences sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        final String jwt = sharedPref.getString(JWT_STR, "");

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, Config.URL_PRODUCTS + orderId, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(tag, response.toString());
                        ArrayList<Product> result = new ArrayList<>();
                        try {
                            result = ProductMapper.mapProductsList(response);
                        } catch (JSONException e) {
                            Log.e("MyOrderActivityRequests", "handleGetProducts JSONException " + e.getLocalizedMessage());
                        }

                        listener.onProductsAvailable(result);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("", "onErrorResponse VolleyError " + error.getLocalizedMessage());
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + jwt);
                Log.i(tag, "headers: " + headers.toString());
                return headers;
            }
        };

        // Access the RequestQueue through your singleton class.
        VolleyRequestQueue.getInstance(context).addToRequestQueue(jsObjRequest);
    }

    public interface MyOrderActivityListener {
        void onCurrentOrderAvailable(Order order);
        void onOrderPutPriceSuccess();
        void onProductsAvailable(ArrayList<Product> products);
        void onError(String message);
    }
}


