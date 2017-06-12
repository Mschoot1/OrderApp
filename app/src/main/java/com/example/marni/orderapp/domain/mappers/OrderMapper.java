package com.example.marni.orderapp.domain.mappers;

import android.util.Log;

import com.example.marni.orderapp.domain.Order;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class OrderMapper {

    public static final String RESULTS = "results";
    public static final String ID = "id";
    public static final String STATUS = "status";
    public static final String PENDING = "pending";
    public static final String TIMESTAMP = "timestamp";
    public static final String PRICE_TOTAL = "price_total";

    public static ArrayList<Order> mapOrderList(JSONObject response) throws JSONException {

        ArrayList<Order> result = new ArrayList<>();

        JSONArray jsonArray = response.getJSONArray(RESULTS);
        for (int idx = 0; idx < jsonArray.length(); idx++) {
            JSONObject order = jsonArray.getJSONObject(idx);
            result.add(getOrderObject(order));
        }

        return result;
    }

    public static Order mapOrder(JSONObject response) throws JSONException {

        Order result = new Order();

        JSONArray jsonArray = response.getJSONArray(RESULTS);
        if (jsonArray.length() > 0) {
            JSONObject order = jsonArray.getJSONObject(0);
            result = getOrderObject(order);
        }
        return result;
    }

    private static Order getOrderObject(JSONObject order) {
        try {
            int id = order.getInt(ID);
            int status = order.getInt(STATUS);
            int pending = order.getInt(PENDING);
            String timestamp = order.getString(TIMESTAMP);
            Double priceTotal = order.getDouble(PRICE_TOTAL);

            Order o = new Order();
            o.setOrderId(id);
            o.setStatus(status);
            o.setTimestamp(getFormattedDate(timestamp));
            o.setPriceTotal(priceTotal);
            o.setPending(pending);

            return o;
        } catch (JSONException ex) {
            Log.e("OrderMapper", "getOrderObject JSONException " + ex.getLocalizedMessage());
        } catch (ParseException e) {
            Log.e("OrderMapper", "getOrderObject ParseException " + e.getLocalizedMessage());
        }
        return null;
    }

    public static String getFormattedDate(String s) throws ParseException {

        SimpleDateFormat sdf;
        sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date parsedDate = sdf.parse(s);
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        return sdf.format(parsedDate);
    }
}
