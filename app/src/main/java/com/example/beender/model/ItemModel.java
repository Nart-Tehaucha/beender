package com.example.beender.model;

import android.graphics.Bitmap;

import com.example.beender.BuildConfig;
import com.example.beender.util.FetchData;
import com.example.beender.util.FetchImage;
import com.example.beender.util.SearchNearby;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class ItemModel implements Serializable {
    private String placeId;
    private Bitmap image;
    private String name, city, country, rating;
    private double lat,lng;
    private int type; // 0 - Destination, 1 - Hotel

    private ItemAdditionalData additionalData;

    public ItemModel() {
    }

    public ItemModel(String placeId, Bitmap image, String name, String city, String country, String rating, double lat, double lng, int type) {
        this.placeId = placeId;
        this.image = image;
        this.name = name;
        this.city = city;
        this.country = country;
        this.rating = rating;
        this.lat = lat;
        this.lng = lng;
        this.type = type;


    }

    public ItemAdditionalData fetchAdditionalData() {
        if (additionalData != null) {
            return additionalData;
        }

        StringBuilder stringBuilder = new StringBuilder("https://maps.googleapis.com/maps/api/place/details/json?");
        stringBuilder.append("place_id=" + placeId);
        stringBuilder.append("&fields=name,formatted_address,formatted_phone_number,website,rating,review,photo");
        stringBuilder.append("&key=" + BuildConfig.MAPS_API_KEY);

        String url = stringBuilder.toString();
        Object dataFetch[] = new Object[2];
        dataFetch[0] = null;
        dataFetch[1] = url;

        FetchData fetchData = new FetchData();
        fetchData.execute(dataFetch);

        try {
            String taskResult = fetchData.get();

            JsonObject locationDetails = new Gson().fromJson(taskResult, JsonObject.class);

            ArrayList<Review> reviews = getReviewsList(locationDetails);

            ArrayList<Bitmap> images = new ArrayList<>();
            for (JsonElement imageJson : locationDetails.get("result").getAsJsonObject().get("photos").getAsJsonArray()) {
                String imageRef = imageJson.getAsJsonObject().get("photo_reference").getAsString();
                images.add(SearchNearby.getPlacePhoto(imageRef));
            }

            additionalData = new ItemAdditionalData(images, reviews);

            return additionalData;
        } catch (ExecutionException | InterruptedException | IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private ArrayList<Review> getReviewsList(JsonObject locationDetails) throws ExecutionException, InterruptedException {
        ArrayList<Review> reviews = new ArrayList<>();
        for (JsonElement reviewJson : locationDetails.get("result").getAsJsonObject().get("reviews").getAsJsonArray()) {
            JsonObject reviewJsonObject = reviewJson.getAsJsonObject();

            String authorName = reviewJsonObject.get("author_name").getAsString();
            int rating = reviewJsonObject.get("rating").getAsInt();
            String text = reviewJsonObject.get("text").getAsString();
            String relativeTimeDescription = reviewJsonObject.get("relative_time_description").getAsString();
            String profilePhotoUrl = reviewJsonObject.get("profile_photo_url").getAsString();

            FetchImage fetchProfileImage = new FetchImage();
            fetchProfileImage.execute(profilePhotoUrl);
            Bitmap profilePicture = fetchProfileImage.get();

            reviews.add(new Review(rating, authorName, text, relativeTimeDescription, profilePicture));
        }

        return reviews;
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

    public Double getRatingAsDouble() {
        return Double.parseDouble(rating);
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