package com.example.beender;

import android.graphics.Bitmap;

public class ItemModel {
    private Bitmap image;
    private String name, city, country;

    public ItemModel() {
    }

    public ItemModel(Bitmap image, String name, String city, String country) {
        this.image = image;
        this.name = name;
        this.city = city;
        this.country = country;
    }

    public Bitmap getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }
}