package com.example.marni.orderapp.domain.mappers;

import android.util.Log;

import com.example.marni.orderapp.domain.Allergy;
import com.example.marni.orderapp.domain.Product;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.example.marni.orderapp.domain.mappers.OrderMapper.ID;
import static com.example.marni.orderapp.domain.mappers.OrderMapper.RESULTS;

public class ProductMapper {

    public static final String NAME = "name";
    public static final String PRODUCT_ID = "product_id";
    public static final String ALLERGIES = "allergies";
    public static final String PRICE = "price";
    public static final String SIZE = "size";
    public static final String ALCOHOL = "alcohol";
    public static final String CATEGORY_ID = "category_id";
    public static final String CATEGORY_NAME = "category_name";
    public static final String QUANTITY = "quantity";
    public static final String PRODUCT_IMAGE = "product_image";
    public static final String ORDER_ID = "order_id";

    public static final String IMAGE = "image";
    public static final String DESCRIPTION = "description";

    private ProductMapper() {
        // empty constructor
    }
    public static List<Product> mapProductsList(JSONObject response) throws JSONException {

        ArrayList<Product> result = new ArrayList<>();

        JSONArray jsonArray = response.getJSONArray(RESULTS);
        for (int idx = 0; idx < jsonArray.length(); idx++) {
            JSONObject product = jsonArray.getJSONObject(idx);
            result.add(getProductObject(product));
        }

        return result;
    }

    private static Product getProductObject(JSONObject product) {
        try {
            Product p = new Product();

            int id;
            if(product.has(PRODUCT_ID)){
                id = product.getInt(PRODUCT_ID);
            } else {
                id = product.getInt(ID);
            }

            JSONArray allergies = product.getJSONArray(ALLERGIES);
            Log.i("ProductMapper", "allergies.length(): " + allergies.length());

            ArrayList<Allergy> as = new ArrayList<>();
            for (int j = 0; j < allergies.length(); j++) {
                JSONObject allergy = allergies.getJSONObject(j);
                Allergy a = new Allergy(allergy.getString(IMAGE), allergy.getString(DESCRIPTION));
                as.add(a);
            }

            String name = product.getString(NAME);
            Double price = product.getDouble(PRICE);
            int size = product.getInt(SIZE);
            Double alcohol = product.getDouble(ALCOHOL);
            int categoryId = product.getInt(CATEGORY_ID);
            String categoryName = product.getString(CATEGORY_NAME);
            int quantity = product.getInt(QUANTITY);
            String imagesrc = product.getString(PRODUCT_IMAGE);

            if(product.has(ORDER_ID)) {
                int orderId = product.getInt(ORDER_ID);
                p.setOrderId(orderId);
            }

            p.setProductId(id);
            p.setName(name);
            p.setPrice(price);
            p.setSize(size);
            p.setAlcoholPercentage(alcohol);
            p.setCategoryId(categoryId);
            p.setQuantity(quantity);
            p.setCategoryName(categoryName);
            p.setAllergies(as);
            p.setImagesrc(imagesrc);

            return p;
        } catch (JSONException ex) {
            Log.e("OrderMapper", "getProductObject JSONException " + ex.getLocalizedMessage());
        }
        return null;
    }
}
