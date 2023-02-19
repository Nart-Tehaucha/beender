package com.example.beender.model;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.List;

public class ItemAdditionalData {

    private final String description;
    private List<Bitmap> images;
    private List<Review> reviews;

    public ItemAdditionalData(List<Bitmap> images, List<Review> reviews, String description) {
        this.images = images;
        this.reviews = reviews;
        this.description = description;
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
}