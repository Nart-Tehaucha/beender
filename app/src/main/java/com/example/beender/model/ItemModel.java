package com.example.beender.model;

import android.graphics.Bitmap;

public class ItemModel {
    private Bitmap image;
    private String name, city, country, rating;
    private double lat,lng;
    private int type; // 0 - Destination, 1 - Hotel

    public ItemModel() {
    }

    public ItemModel(Bitmap image, String name, String city, String country, String rating, double lat, double lng, int type) {
        this.image = image;
        this.name = name;
        this.city = city;
        this.country = country;
        this.rating = rating;
        this.lat = lat;
        this.lng = lng;
        this.type = type;
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

    public String getRating() {
        return rating;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public int getType() {
        return type;
    }

    public String printPosition() {
        return String.valueOf(lat) + "," + String.valueOf(lng);
    }
}