package com.example.beender;

import android.graphics.Point;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.TravelMode;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


public class MapFragment extends Fragment {

    private GoogleMap mMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Initialize view
        View view=inflater.inflate(R.layout.fragment_map, container, false);

        // Initialize map fragment
        SupportMapFragment supportMapFragment=(SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.google_map);

        // Async map
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                testDirections();
                // When map is loaded
//                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
//                    @Override
//                    public void onMapClick(LatLng latLng) {
//                        // When clicked on map
//                        // Initialize marker options
//                        MarkerOptions markerOptions=new MarkerOptions();
//                        // Set position of marker
//                        markerOptions.position(latLng);
//                        // Set title of marker
//                        markerOptions.title(latLng.latitude+" : "+latLng.longitude);
//                        // Remove all marker
//                        googleMap.clear();
//                        // Animating to zoom the marker
//                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));
//                        // Add marker on map
//                        googleMap.addMarker(markerOptions);
//                    }
//                });


            }
        });
        // Return view
        return view;
    }

    private void testDirections() {


        final GeoApiContext context = new GeoApiContext.Builder().apiKey(BuildConfig.MAPS_API_KEY).build();

        PolylineOptions polylineOptions = new PolylineOptions();

        com.google.maps.model.LatLng origin = new com.google.maps.model.LatLng(32.721514, 35.439374);
        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(32.690598, 35.420513);

        DirectionsApiRequest request =
                DirectionsApi.newRequest(context)
                        .origin(origin)
                        .destination(destination)
                        .mode(TravelMode.DRIVING)
                        .departureTime(Instant.now());
        List<com.google.maps.model.LatLng> latlongList = null;

        try {
            DirectionsResult result = request.await();
            DirectionsRoute[] routes = result.routes;
            for (DirectionsRoute route : routes) {
                latlongList =  route.overviewPolyline.decodePath();
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

        polylineOptions.addAll(convertCoordType(latlongList));
        polylineOptions.width(10);

        mMap.addPolyline(polylineOptions);
        mMap.addMarker(new MarkerOptions().position(new LatLng(32.721514, 35.439374)).title("Kfar Kama"));
        mMap.addMarker(new MarkerOptions().position(new LatLng(32.690598, 35.420513)).title("Kfar Tavor"));

        LatLngBounds bounds = new LatLngBounds.Builder()
                .include(new LatLng(32.721514, 35.439374))
                .include(new LatLng(32.690598, 35.420513)).build();
        Point point = new Point();
        getActivity().getWindowManager().getDefaultDisplay().getSize(point);
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, point.x, 150, 30));
    }

    // Convert google maps LatLng object to android LatLng object
    @NonNull
    static List<com.google.android.gms.maps.model.LatLng> convertCoordType(List<com.google.maps.model.LatLng> list) {
        List<com.google.android.gms.maps.model.LatLng> resultList = new ArrayList<>();
        for (com.google.maps.model.LatLng item : list) {
            resultList.add(new com.google.android.gms.maps.model.LatLng(item.lat, item.lng));
        }
        return resultList;
    }
}