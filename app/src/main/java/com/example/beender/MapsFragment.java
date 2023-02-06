package com.example.beender;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RetryPolicy;
import com.example.beender.ui.dashboard.DashboardFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.TravelMode;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class MapsFragment extends Fragment {

    private static final String TAG = DashboardFragment.class.getSimpleName();

    private GoogleMap mMap;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;
            testDirections();
//            LatLng sydney = new LatLng(-34, 151);
//            googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//            googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    private void testDirections() {


        final GeoApiContext context = new GeoApiContext.Builder().apiKey(BuildConfig.MAPS_API_KEY).build();

        PolylineOptions polylineOptions = null;

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

        List<com.google.android.gms.maps.model.LatLng> convertedList = convertCoordType(latlongList);
        polylineOptions.addAll(convertedList);
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
    static List<com.google.android.gms.maps.model.LatLng> convertCoordType(List<com.google.maps.model.LatLng> list) {
        List<com.google.android.gms.maps.model.LatLng> resultList = new ArrayList<>();
        for (com.google.maps.model.LatLng item : list) {
            resultList.add(new com.google.android.gms.maps.model.LatLng(item.lat, item.lng));
        }
        return resultList;
    }
}