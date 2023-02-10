package com.example.beender;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.beender.model.CurrentItems;
import com.example.beender.ui.dashboard.DashboardFragment;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
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

    private static final String TAG = DashboardFragment.class.getSimpleName();
    final GeoApiContext context = new GeoApiContext.Builder().apiKey(BuildConfig.MAPS_API_KEY).build();
    private GoogleMap mMap;

    List<com.google.maps.model.LatLng> swipedRight;
    List<com.google.maps.model.LatLng> mWaypoints;
    PolylineOptions polylineOptions;
    ArrayList<MarkerOptions> markers;
    List<com.google.maps.model.LatLng> latlongList;

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
                if(!CurrentItems.getInstance().getSwipedRight().isEmpty()) {
                    getDirections();
                }
            }
        });
        // Return view
        return view;
    }

    private void getDirections() {
        polylineOptions = new PolylineOptions();

        swipedRight = CurrentItems.getInstance().getAsLatLng();

        com.google.maps.model.LatLng origin = swipedRight.get(0);
        com.google.maps.model.LatLng destination = swipedRight.get(swipedRight.size()-1);

        mWaypoints = new ArrayList<>();

        for(int i = 1; i<swipedRight.size()-1; i++) {
            mWaypoints.add(swipedRight.get(i));
        }


        // Create ArrayList containing all markers to add to the map. Origin and destination markers are colored different from the waypoint markers.
        markers = new ArrayList<>();
        markers.add(new MarkerOptions()
                .position(new LatLng(swipedRight.get(0).lat, swipedRight.get(0).lng))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        for(com.google.maps.model.LatLng l : mWaypoints) {
            markers.add(new MarkerOptions().position(new LatLng(l.lat, l.lng)));
        }
        markers.add(new MarkerOptions()
                .position(new LatLng(swipedRight.get(swipedRight.size()-1).lat, swipedRight.get(swipedRight.size()-1).lng))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));

        // Create a request for calculating and returning the final route
        DirectionsApiRequest request =
                DirectionsApi.newRequest(context)
                        .origin(origin)
                        .destination(destination)
                        .waypoints(mWaypoints.toArray(new com.google.maps.model.LatLng[0]))
                        .mode(TravelMode.DRIVING)
                        .departureTime(Instant.now());
        latlongList = null;

        try {
            DirectionsResult result = request.await();
            DirectionsRoute[] routes = result.routes;
            for (DirectionsRoute route : routes) {
                latlongList =  route.overviewPolyline.decodePath();
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

        // Draw the route
        polylineOptions.addAll(convertCoordType(latlongList));
        polylineOptions.width(10);
        polylineOptions.color(Color.BLUE);
        mMap.addPolyline(polylineOptions);

        // Add all the markers to the map
        for(int i = 0; i<markers.size(); i++) {
            mMap.addMarker(markers.get(i)).setTag(i);
        }

//        for(MarkerOptions m : markers) {
//            mMap.addMarker(m);
//        }

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(com.google.android.gms.maps.model.Marker marker) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(R.string.markerDialogMessage)
                .setCancelable(true)
                .setPositiveButton(R.string.markerOk, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                        int markerIndex = (Integer) marker.getTag();

                        if(CurrentItems.getInstance().getSwipedRight().size() <=2) {
                            Toast.makeText(getContext(), "There has to be at least two destinations!", Toast.LENGTH_SHORT).show();
                        } else {
                            CurrentItems.getInstance().getSwipedRight().remove(markerIndex);
                            mMap.clear();
                            getDirections();
                        }
                    }
                })
                .setNegativeButton(R.string.markerCancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        dialog.cancel();
                    }
                });

                // Create the AlertDialog
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            }
        });

        // Set the bounds of the camera on the map to contain the entire route.
        LatLngBounds bounds = new LatLngBounds.Builder()
                .include(new LatLng(origin.lat, origin.lng))
                .include(new LatLng(destination.lat, destination.lng)).build();
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