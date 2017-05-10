package com.example.marni.orderapp.Domain;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by MarcdenUil on 5-5-2017.
 */

public class Product implements Serializable {

    private int productId;
    private String name;
    private double price;
    private int size;
    private double alcohol_percentage;
    private ArrayList<Allergy> allergies = new ArrayList<>();
    private int categoryId;
    private int quantity;

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public double getAlcohol_percentage() {
        return alcohol_percentage;
    }

    public void setAlcohol_percentage(double alcohol_percentage) {
        this.alcohol_percentage = alcohol_percentage;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public ArrayList getAllergies() {
        return allergies;
    }

    public void setAllergies(ArrayList allergies) {
        this.allergies = allergies;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
