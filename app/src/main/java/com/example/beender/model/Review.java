package com.example.beender.model;

import android.graphics.Bitmap;

import java.io.Serializable;

public class Review {
    private final int rating;
    private final String authorName;
    private final String text;
    private final String relativeTimeDescription;
    private final Bitmap profilePicture;


    public Review(int rating, String authorName, String text, String relativeTimeDescription, Bitmap profilePicture) {
        this.rating = rating;
        this.authorName = authorName;
        this.text = text;
        this.relativeTimeDescription = relativeTimeDescription;
        this.profilePicture = profilePicture;

    }

    public int getRating() {
        return rating;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getText() {
        return text;
    }
    public String getRelativeTimeDescription() {
        return relativeTimeDescription;
    }
    public Bitmap getProfilePicture() {
        return profilePicture;
    }

}