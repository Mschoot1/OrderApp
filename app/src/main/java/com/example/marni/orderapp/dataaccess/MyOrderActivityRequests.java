package com.example.marni.orderapp.dataaccess;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
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
import java.util.List;
import java.util.Map;

import static com.example.marni.orderapp.presentation.activities.LoginActivity.JWT_STR;
import static com.example.marni.orderapp.presentation.activities.LoginActivity.PREF_NAME;
import static com.example.marni.orderapp.presentation.activities.LoginActivity.USER;

public class MyOrderActivityRequests {

    private Context context;
    public final String tag = this.getClass().getSimpleName();

    private SharedPreferences prefs;

    private String jwt;
    private int user;

    public static final String AUTHORIZATION = "Authorization";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String APPLICATION_JSON = "application/json";
    public static final String BEARER = "Bearer ";

    private MyOrderActivityListener listener;

    /**
     * Constructor
     *
     * @param context  a description
     * @param listener a description
     */
    public MyOrderActivityRequests(Context context, MyOrderActivity listener) {
        this.context = context;
        this.listener = listener;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        jwt = prefs.getString(JWT_STR, "");
        user = prefs.getInt(USER, 0);
    }

    public void handleGetCurrentOrder() {
        Log.i(tag, "handleGetProducts");
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
                headers.put(CONTENT_TYPE, APPLICATION_JSON);
                headers.put(AUTHORIZATION, BEARER + jwt);
                Log.i(tag, "headers: " + headers.toString());
                return headers;
            }
        };

        // Access the RequestQueue through your singleton class.
        VolleyRequestQueue.getInstance(context).addToRequestQueue(jsObjRequest);
    }

    public void handlePutOrder(double priceTotal, int orderId) {
        Log.i(tag, "handlePutOrder");
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
                    headers.put(CONTENT_TYPE, APPLICATION_JSON);
                    headers.put(AUTHORIZATION, BEARER + jwt);
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
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, Config.URL_PRODUCTS + orderId, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(tag, response.toString());
                        List<Product> result = new ArrayList<>();
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
                headers.put(CONTENT_TYPE, APPLICATION_JSON);
                headers.put(AUTHORIZATION, BEARER + jwt);
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

        void onProductsAvailable(List<Product> products);

        void onError(String message);
    }
}


