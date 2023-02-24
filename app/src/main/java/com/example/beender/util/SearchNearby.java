package com.example.beender.util;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.example.beender.BuildConfig;
import com.example.beender.MainActivity;
import com.example.beender.model.CurrentItems;
import com.example.beender.model.ItemModel;
import com.example.beender.ui.dashboard.DashboardFragment;
import com.google.maps.NearbySearchRequest;
import com.google.maps.PlacesApi;
import com.google.maps.model.LatLng;
import com.google.maps.model.PlaceType;
import com.google.maps.model.PlacesSearchResponse;
import com.google.maps.model.PlacesSearchResult;
import com.google.maps.model.RankBy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class SearchNearby {

    private static final String TAG = DashboardFragment.class.getSimpleName();

    /**
     * Sends our current location through an HTTP request to Places API and receives a list of nearby places.
     * @param lat lng
     */
    public static List<ItemModel> getNearbyPlaces(double lat, double lng) throws ExecutionException, InterruptedException {
        StringBuilder stringBuilder = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        stringBuilder.append("location=" + lat + "," + lng);
        stringBuilder.append("&radius=3500");
        stringBuilder.append("&type=tourist_attraction");
        stringBuilder.append("&key=" + BuildConfig.MAPS_API_KEY);

        String url = stringBuilder.toString();
        Object dataFetch[] = new Object[2];
        dataFetch[0] = null;
        dataFetch[1] = url;

        FetchData fetchData = new FetchData();
        fetchData.execute(dataFetch);

        String taskResult = "";
        String photoReference = "";
        taskResult = fetchData.get();

        if(fetchData.getStatus() != AsyncTask.Status.PENDING) {
            try {
                Log.d(TAG, "INSIDE IF");
                JSONObject jobj = new JSONObject(taskResult);
                JSONArray jarr = jobj.getJSONArray("results");
                Log.d(TAG, "OBJ - " + jobj.toString());
                Log.d(TAG, "ARR - " + jarr.toString());
                Log.d(TAG, "ARR - " + jarr.get(0).toString());

                int imagesToLoadFirst = 3;

                // Create a list of ItemModel that contains all info of each Place we generated
                List<ItemModel> items = new ArrayList<>();
                for (int i=0; i < jarr.length(); i++) {
                    Log.d(TAG, "Item NUMBER " + i + "- " + jarr.get(i).toString());
                    if(((JSONObject) jarr.get(i)).has("photos")) {
                        JSONObject temp = jarr.getJSONObject(i);
                        String pName = temp.get("name").toString();
                        String pCity = temp.get("vicinity").toString();
                        String pCountry = temp.get("vicinity").toString();
                        String pRating = "No Rating";
                        String pId = temp.get("place_id").toString();
                        if(temp.has("rating")) {
                            pRating = temp.get("rating").toString();
                        }
                        double pLat = (Double) ((JSONObject) ((JSONObject) temp.get("geometry")).get("location")).get("lat");
                        double pLng = (Double) ((JSONObject) ((JSONObject) temp.get("geometry")).get("location")).get("lng");

                        ItemModel attraction = new ItemModel(pId, null, pName, pCity, pCountry, pRating, pLat, pLng, 0);

                        if (i < imagesToLoadFirst) {
                            Bitmap pImage = getPlacePhoto(((JSONObject) ((JSONArray) ((JSONObject) jarr.get(i)).get("photos")).get(0)).get("photo_reference").toString());
                            attraction.setImage(pImage);

                        } else {
                            int finalI = i;
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Bitmap pImage = null;
                                    try {
                                        pImage = getPlacePhoto(((JSONObject) ((JSONArray) ((JSONObject) jarr.get(finalI)).get("photos")).get(0)).get("photo_reference").toString());
                                    } catch (IOException | ExecutionException | InterruptedException |
                                             JSONException e) {
                                        e.printStackTrace();
                                    }
                                    attraction.setImage(pImage);
                                }
                            }).start();
                        }

                        items.add(attraction);

                    }
                }

                // Update the singleton CurrentItems to contain our generated list of places
                //CurrentItems.getInstance().getCurrStack().put(0, new ArrayList<>(items));
                CurrentItems.getInstance().getCurrStack().get(0).addAll(new ArrayList<>(items));
                return items;

            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static List<ItemModel> getNearbyHotels(LatLng location) throws ExecutionException, InterruptedException {
        NearbySearchRequest request = PlacesApi.nearbySearchQuery(MainActivity.gaContext, location)
                .radius(3500)
                .rankby(RankBy.PROMINENCE)
                .type(PlaceType.LODGING);
        try {
            PlacesSearchResponse response = request.await();
            for(PlacesSearchResult r : response.results) {
                Log.d(TAG, "Vicinity: " + r.vicinity + " Geometry: " + r.geometry.location);
            }

            // Create a list of ItemModel that contains all info of each Place we generated
            List<ItemModel> items = new ArrayList<>();

            Log.d(TAG, "Hotel results size" + response.results.length);
            for (PlacesSearchResult r : response.results) {
                if(r.photos.length != 0) {
                    String pName = r.name;
                    String pCity = r.vicinity;
                    String pCountry = r.vicinity;
                    String pRating = "No Rating";
                    if(r.userRatingsTotal != 0) {
                        pRating = String.valueOf(r.rating);
                    }
                    Bitmap pImage = getPlacePhoto(r.photos[0].photoReference);
                    double pLat = r.geometry.location.lat;
                    double pLng = r.geometry.location.lng;

                    items.add(new ItemModel("todo: get actual ID", pImage, pName, pCity, pCountry, pRating, pLat, pLng, 1));
                }

                // Update the singleton CurrentItems to contain our generated list of places
                CurrentItems.getInstance().setCurrStackHotels(new ArrayList<>(items));
                Log.d(TAG, "Number of Hotels Found: " + CurrentItems.getInstance().getCurrStackHotels().size());
                return items;
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        return null;
    }

    // Recieves a photo_reference of a place, sends an HTTP request to Places API, and converts the result to a Bitmap photo.
    public static Bitmap getPlacePhoto(String photoReference) throws IOException, ExecutionException, InterruptedException {
        Bitmap placePhoto;

        StringBuilder stringBuilder = new StringBuilder("https://maps.googleapis.com/maps/api/place/photo?");
        stringBuilder.append("maxwidth=750");
        stringBuilder.append("&maxheight=1125");
        stringBuilder.append("&photo_reference=" + photoReference);
        stringBuilder.append("&key=" + BuildConfig.MAPS_API_KEY);

        String url = stringBuilder.toString();

        FetchImage fetchImage = new FetchImage();
        fetchImage.execute(url);

        return fetchImage.get();

        //testIV.setImageBitmap(fetchImage.get());
    }
}
