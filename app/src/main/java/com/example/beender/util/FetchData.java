package com.example.beender.util;

import android.os.AsyncTask;
import android.util.Log;

import com.example.beender.ui.dashboard.DashboardFragment;
import com.google.android.gms.maps.GoogleMap;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class FetchData extends AsyncTask<Object,String,String> {
    private static final String TAG = FetchData.class.getSimpleName();
    String googleNearbyPlacesData;
    GoogleMap googleMap;
    String url;

    @Override
    protected String doInBackground(Object... objects) {
        try {
            googleMap = (GoogleMap) objects[0];
            url = (String) objects[1];
            DownloadUrl downloadURL = new DownloadUrl();
            googleNearbyPlacesData = downloadURL.retrieveUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return googleNearbyPlacesData;
    }

    @Override
    protected void onPostExecute(String s) {
        List<HashMap<String, String>> listNearbyPlaces = null;
        DataParser dataParser = new DataParser();
        listNearbyPlaces = dataParser.parse(s);

        Log.d(TAG, "FETCH EXECUTED");
        Log.d(TAG, "listNearbyPlaces -" + listNearbyPlaces.toString());
        Log.d(TAG, "googleNearbyPlacesData -" + googleNearbyPlacesData);
        Log.d(TAG, "s -" + s);


        displayNearbyPlaces(listNearbyPlaces);


//        try {
//            JSONObject jsonObject = new JSONObject(s);
//            JSONArray jsonArray = jsonObject.getJSONArray("results");
//
//            for(int i=0; i<jsonArray.length(); i++) {
//                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
//                JSONObject getLocation = jsonObject1.getJSONObject("geometry").getJSONObject("location");
//
//                String lat = getLocation.getString("lat");
//                String lng = getLocation.getString("lng");
//
//                JSONObject getName = jsonArray.getJSONObject(i);
//                String name = getName.getString("name");
//            }
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }

    private void displayNearbyPlaces(List<HashMap<String, String>> listNearbyPlaces) {
        for(int i=0; i<listNearbyPlaces.size(); i++) {
            HashMap<String, String> nearbyPlace = listNearbyPlaces.get(i);
            Log.d(TAG , "INSIDE");
            Log.d(TAG , nearbyPlace.toString());
        }
    }
}
