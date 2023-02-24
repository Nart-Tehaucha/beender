package com.example.beender.model;

import android.graphics.Bitmap;

import java.util.List;

public class ItemAdditionalData {

    private final String description;
    private List<Bitmap> images;
    private List<Review> reviews;
    private final String website;


    public ItemAdditionalData(List<Bitmap> images, List<Review> reviews, String description, String website) {
        this.images = images;
        this.reviews = reviews;
        this.description = description;
        this.website = website;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public List<Bitmap> getImages() {
        return images;
    }

    public String getDescription() {
        return description;
    }

    public String getWebsite() {
        return website;
    }
}