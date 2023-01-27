package com.example.beender.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// Takes a JSON file of a Place, and converts it into a HashMap Object.
public class DataParser {
    private HashMap<String, String> getSingleNearbyPlace(JSONObject googlePlaceJSON) {
        HashMap<String, String> googlePlaceMap = new HashMap<>();
        String nameOfPlace = "-NA-";
        String vicinity = "-NA-";
        String latitude = "";
        String longitude = "";
        String reference = "";



        try {
            if (!googlePlaceJSON.isNull("name")) {
                nameOfPlace = googlePlaceJSON.getString("name");
            }
            if (!googlePlaceJSON.isNull("vicinity")) {
                vicinity = googlePlaceJSON.getString("vicinity");
            }
            latitude = googlePlaceJSON.getJSONObject("geometry").getJSONObject("location").getString("lat");
            longitude = googlePlaceJSON.getJSONObject("geometry").getJSONObject("location").getString("lng");
            reference = googlePlaceJSON.getString("reference");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        googlePlaceMap.put("place_name", nameOfPlace);
        googlePlaceMap.put("place_vicinity", vicinity);
        googlePlaceMap.put("place_lat", latitude);
        googlePlaceMap.put("place_lng", longitude);
        googlePlaceMap.put("place_ref", reference);

        return googlePlaceMap;
    }

    private List<HashMap<String, String>> getAllNearbyPlaces(JSONArray jsonArray) {
        int counter = jsonArray.length();

        List<HashMap<String, String>> listNearbyPlaces = new ArrayList<>();

        HashMap<String, String> nearbyPlaceMap = null;

        for(int i = 0; i<counter; i++) {
            try {
                nearbyPlaceMap = getSingleNearbyPlace((JSONObject) jsonArray.get(i));
                listNearbyPlaces.add(nearbyPlaceMap);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return listNearbyPlaces;
    }


    // Convert a given JSONArray string to a JSONArray object and pass it to the getAllNearbyPlaces method.
    public List<HashMap<String, String>> parse(String JSONData) {
        JSONArray jsonArray = null;
        JSONObject jsonObject;

        try {
            jsonObject = new JSONObject(JSONData);
            jsonArray = jsonObject.getJSONArray("results");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return getAllNearbyPlaces(jsonArray);
    }
}
