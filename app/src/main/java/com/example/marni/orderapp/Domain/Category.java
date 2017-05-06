package com.example.marni.orderapp.Domain;

/**
 * Created by MarcdenUil on 6-5-2017.
 */

public class Category {
    private int id;
    private String categoryname;

    public Category(int id, String categoryname) {
        this.id = id;
        this.categoryname = categoryname;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategoryname() {
        return categoryname;
    }

    public void setCategoryname(String categoryname) {
        this.categoryname = categoryname;
    }
}
