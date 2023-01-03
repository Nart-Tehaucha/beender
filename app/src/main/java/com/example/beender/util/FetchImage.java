package com.example.beender.util;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class FetchImage extends AsyncTask<String, String, Bitmap> {
    private static final String TAG = FetchData.class.getSimpleName();
    private Bitmap placePhoto;

    @Override
    protected Bitmap doInBackground(String... src) {
        try {
            DownloadUrl downloadURL = new DownloadUrl();
            placePhoto = downloadURL.retrievePhoto(src[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return placePhoto;
    }

    @Override
    protected void onPostExecute(Bitmap b) {
        Log.d(TAG, "Fetched Image");
    }
}
