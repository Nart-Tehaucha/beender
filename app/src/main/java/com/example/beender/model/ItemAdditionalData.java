package com.example.beender.model;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.List;

public class ItemAdditionalData implements Serializable {

    private List<Bitmap> images;
    private List<Review> reviews;

    public ItemAdditionalData(List<Bitmap> images, List<Review> reviews) {
        this.images = images;
        this.reviews = reviews;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public List<Bitmap> getImages() {
        return images;
    }
}