package com.example.beender;

import android.graphics.Bitmap;

public class ItemModel {
    private Bitmap image;
    private String name, city, country, ranking;

    public ItemModel() {
    }

    public ItemModel(Bitmap image, String name, String city, String country, String ranking) {
        this.image = image;
        this.name = name;
        this.city = city;
        this.country = country;
        this.ranking = ranking;
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

    public String getRanking() {
        return ranking;
    }
}