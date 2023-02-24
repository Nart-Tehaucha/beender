package com.example.beender.model;

import android.graphics.Bitmap;

import com.example.beender.BuildConfig;
import com.example.beender.util.FetchData;
import com.example.beender.util.FetchImage;
import com.example.beender.util.SearchNearby;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class ItemModel implements Serializable {
    private static final String NO_DESCRIPTION = "No description available";

    private String placeId;
    private transient Bitmap image;
    private String name, city, country, rating;
    private double lat,lng;
    private int type; // 0 - Destination, 1 - Hotel

    private transient ItemAdditionalData additionalData;
    private transient Runnable onImageLoadedListener;

    private boolean isDoneLoadingImages = false;

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

    public void setImageLoadedListener(Runnable onImageLoadedListener) {
        this.onImageLoadedListener = onImageLoadedListener;
    }

    private void onImageLoaded() {
        onImageLoadedListener.run();
    }

    public ItemAdditionalData fetchAdditionalData() {
        if (additionalData != null) {
            return additionalData;
        }

        StringBuilder stringBuilder = new StringBuilder("https://maps.googleapis.com/maps/api/place/details/json?");
        stringBuilder.append("place_id=" + placeId);
        stringBuilder.append("&fields=editorial_summary,name,formatted_address,formatted_phone_number,website,rating,review,photo");
        stringBuilder.append("&key=" + BuildConfig.MAPS_API_KEY);

        String url = stringBuilder.toString();
        Object dataFetch[] = new Object[2];
        dataFetch[0] = null;
        dataFetch[1] = url;

        FetchData fetchData = new FetchData();
        fetchData.execute(dataFetch);

        String taskResult = null;
        try {
            taskResult = fetchData.get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        JsonObject locationDetails = new Gson().fromJson(taskResult, JsonObject.class);

        ArrayList<Review> reviews = null;
        try {
            reviews = buildReviewsList(locationDetails);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            reviews = new ArrayList<>(); // Return empty reviews
        }

        ArrayList<Bitmap> images = null;
        images = buildImagesList(locationDetails);


        String description;
        try {
            description = fetchDescription();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            description = NO_DESCRIPTION;
        }

        if (description.equals(NO_DESCRIPTION)) {
            if (locationDetails.get("result").getAsJsonObject().get("editorial_summary") != null) {
                description = locationDetails.get("result").getAsJsonObject().get("editorial_summary").getAsJsonObject().get("overview").getAsString();
            }
        }

        String website;
        try {
            website = locationDetails.get("result").getAsJsonObject().get("website").getAsString();
        } catch (Exception e) {
            website = null;
        }

        additionalData = new ItemAdditionalData(images, reviews, description, website);

        return additionalData;
    }

    private String fetchDescription() throws ExecutionException, InterruptedException {
        String wikiUrl = "https://en.wikipedia.org/w/api.php?action=query&format=json&prop=extracts&exintro&explaintext&redirects=1&titles=" + name;
        FetchData fetchData = new FetchData();

        Object[] dataFetch = new Object[2];
        dataFetch[0] = null;
        dataFetch[1] = wikiUrl;

        fetchData.execute(dataFetch);

        String resultStr = fetchData.get();

        JsonObject resultJson = new Gson().fromJson(resultStr, JsonObject.class);
        JsonObject pagesJson = resultJson.get("query").getAsJsonObject().get("pages").getAsJsonObject();
        for (Map.Entry<String, JsonElement> pageEntry : pagesJson.entrySet()) {
            if (pageEntry.getKey().equals("-1")) {
                return NO_DESCRIPTION;
            }
            JsonObject page = pageEntry.getValue().getAsJsonObject();
            if (!page.has("extract")) {
                continue;
            }

            String fullDescription = page.get("extract").getAsString();

            return fullDescription;
        }

        return resultStr;
    }

    private ArrayList<Bitmap> buildImagesList(JsonObject locationDetails) {
        ArrayList<Bitmap> images = new ArrayList<>();
        JsonArray imageJsons = locationDetails.get("result").getAsJsonObject().get("photos").getAsJsonArray();

        for (JsonElement imageJson : imageJsons) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    synchronized (images) {
                        String imageRef = imageJson.getAsJsonObject().get("photo_reference").getAsString();
                        try {
                            images.add(SearchNearby.getPlacePhoto(imageRef));

                            if (images.size() == imageJsons.size()) {
                                isDoneLoadingImages = true;
                            }

                            onImageLoaded();

                        } catch (IOException | InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();

        }

        return images;
    }

    private ArrayList<Review> buildReviewsList(JsonObject locationDetails) throws ExecutionException, InterruptedException {
        try {
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
        } catch (NullPointerException e) {
            return null;
        }
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
        try {
            return Double.parseDouble(rating);
        } catch (Exception e) {
            return 0.0;
        }
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

    public boolean isDoneLoadingImages() {
        return isDoneLoadingImages;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }
}