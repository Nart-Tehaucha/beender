package com.example.beender;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.preference.PreferenceManager;

import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.beender.model.CurrentItems;
import com.example.beender.model.ItemModel;
import com.example.beender.ui.dashboard.DashboardFragment;
import com.example.beender.util.FireStoreUtils;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.TravelMode;

import java.lang.reflect.Array;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;


public class MapFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private static final String TAG = DashboardFragment.class.getSimpleName();
    private GoogleMap mMap;
    private Spinner spinner;
    private FloatingActionButton btnArchive;
    private String parentFrag;
    private boolean tripIsArchived;


    List<com.google.maps.model.LatLng> swipedRight;
    List<com.google.maps.model.LatLng> mWaypoints;
    PolylineOptions polylineOptions;
    ArrayList<MarkerOptions> markers;
    List<com.google.maps.model.LatLng> latlongList;

    HashMap<Integer, ArrayList<Marker>> currentMarkers;
    HashMap<Integer, ArrayList<Polyline>> currentPolylines;

    FragmentManager fm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(getArguments() != null && getArguments().containsKey("parentFrag")) {
            parentFrag = getArguments().getString("parentFrag");
            Log.d(TAG, parentFrag);
        } else {
            parentFrag = "map";
        }
        currentMarkers = new HashMap<>();
        currentPolylines = new HashMap<>();

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

                if(!CurrentItems.getInstance().getSwipedRight().get(0).isEmpty() || (parentFrag.equals("archive") && !CurrentItems.getInstance().getArchiveMap().isEmpty())) {
                    prepareMap(view);
                }
            }
        });


        // Init spinner
        spinner = (Spinner) view.findViewById(R.id.daysSpinner);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if(parentFrag.equals("archive")) {
            if(getArguments().getString("type").equals("Star")) {
                ArrayList<String> spinnerDays = new ArrayList<>();
                spinnerDays.add("Show All");
                for(int i = 0 ; i < CurrentItems.getInstance().getArchiveMap().size(); i++) {
                    spinnerDays.add("Day " + (i+1));
                }

                // Create an ArrayAdapter using the string array and a default spinner layout
                ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, spinnerDays);
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                // Apply the adapter to the spinner
                spinner.setAdapter(adapter);
                spinner.setOnItemSelectedListener(this);
            }
        } else if(!parentFrag.equals("archive") && sharedPreferences.getString("kind_of_trip", "").equals("Star")) {
            ArrayList<String> spinnerDays = new ArrayList<>();
            spinnerDays.add("Show All");
            if(CurrentItems.getInstance().getCurrDay() > 0) {
                for(int i = 0 ; i <= CurrentItems.getInstance().getCurrDay(); i++) {
                    spinnerDays.add("Day " + (i+1));
                }
            }

            // Create an ArrayAdapter using the string array and a default spinner layout
            ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, spinnerDays);
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Apply the adapter to the spinner
            spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(this);

        } else {
            spinner.setVisibility(View.GONE);
        }

        // FloatingActionButton setup
        btnArchive = view.findViewById(R.id.btnArchive);
        if(getArguments() != null && getArguments().containsKey("archiveEdited") && getArguments().getBoolean("archiveEdited")) {
            // If we loaded an archived trip and made edits, replaced the button with a save archive button.
            btnArchive.setVisibility(View.GONE);
            loadActionButton(view, 0);
        } else {
            // If user went to MapFragment before swiping any cards, hide the archive button.
            if(CurrentItems.getInstance().getSwipedRight().get(0).isEmpty()) {
                btnArchive.setVisibility(View.GONE);
            }
            // If we loaded an archived trip after swiping cards, change the archive button to a "back" button. When pressed it takes us back to the trip that we we're planning.
            else if(getArguments() != null && getArguments().containsKey("parentFrag") && getArguments().getString("parentFrag").equals("archive")) {
                Log.d(TAG, "STARTED CASE 1");
                loadActionButton(view, 1);
            }
            // Normal case - the user swiped cards and went to MapFragment. The archive button allows to archive the trip.
            else {
                Log.d(TAG, "STARTED CASE 2");
                loadActionButton(view, 2);
            }
        }
        // Return view
        return view;
    }

    // Draws routes for each seperate day in the trip, and gives them different colors.
    private void prepareMap(View view) {
        if(spinner.getVisibility() != View.GONE) {
            spinner.setSelection(0);
        }

        if(parentFrag.equals("archive")) {
            for(int i = 0 ; i < CurrentItems.getInstance().getArchiveMap().size(); i++) {
                getDirections(view, i);
            }
        } else {
            for(int i = 0 ; i <= CurrentItems.getInstance().getCurrDay(); i++) {
                getDirections(view, i);
            }
        }
    }

    // Draws route for a single day
    private void getDirections(View view, int day) {

        // Prepare a new ArrayList in currentMarkers and current Polylines that will contain this day's markers and polylines.
        currentMarkers.put(day, new ArrayList<>());
        currentPolylines.put(day, new ArrayList<>());

        polylineOptions = new PolylineOptions();

        // If the displayed trip is from the archive, then only work with the archiveMap in CurrentItems, and not swipedRight.
        if(parentFrag.equals("archive")) {
            Log.d(TAG, "DAY " + day);
            swipedRight = CurrentItems.getInstance().getArchiveMap().get(String.valueOf(day));
            Log.d(TAG, swipedRight.toString());
        } else {
            swipedRight = CurrentItems.getInstance().getAsLatLng(day);
        }

        com.google.maps.model.LatLng origin = swipedRight.get(0);
        com.google.maps.model.LatLng destination = swipedRight.get(swipedRight.size()-1);

        mWaypoints = new ArrayList<>();

        for(int i = 1; i<swipedRight.size()-1; i++) {
            mWaypoints.add(swipedRight.get(i));
        }

        // Create ArrayList containing all markers to add to the map. Origin and destination markers are colored different from the waypoint markers.
        markers = new ArrayList<>();

        // Give Hotel icon to the starting hotel
        // TODO disable this for Journey type trips.
        Bitmap hotelIcon = BitmapFactory.decodeResource(getResources(), R.drawable.hotel);

        markers.add(new MarkerOptions()
                .position(new LatLng(swipedRight.get(0).lat, swipedRight.get(0).lng))
                .icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(hotelIcon, 120, 133, false)))
                .zIndex(1.0f));
        for(com.google.maps.model.LatLng l : mWaypoints) {
            markers.add(new MarkerOptions().position(new LatLng(l.lat, l.lng)));
        }

        // Give the last destination a pink color.
        markers.add(new MarkerOptions()
                .position(new LatLng(swipedRight.get(swipedRight.size()-1).lat, swipedRight.get(swipedRight.size()-1).lng))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));

        // Create a request for calculating and returning the final route
        DirectionsApiRequest request =
                DirectionsApi.newRequest(MainActivity.gaContext)
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
        Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        polylineOptions.color(color);
        currentPolylines.get(day).add(mMap.addPolyline(polylineOptions));

        // Add all the markers to the map
        for(int i = 0; i<markers.size(); i++) {
            Marker m = mMap.addMarker(markers.get(i));
            m.setTag(new int[] {day, i});
            currentMarkers.get(day).add(m);
        }

        // Markers
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(com.google.android.gms.maps.model.Marker marker) {

                // Clicking a marker displays a multi-choice dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Choose:")
                        .setItems(R.array.marker_options_array, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                int[] markerInfo = (int[]) marker.getTag();
                                int markerIndex = markerInfo[1];
                                int markerDay = markerInfo[0];

                                com.google.maps.model.LatLng hotelLatLng;
                                String fromFrag = "map";

                                if(parentFrag.equals("archive")) {
                                    hotelLatLng = new com.google.maps.model.LatLng(CurrentItems.getInstance().getArchiveMap().get(String.valueOf(markerDay)).get(markerIndex).lat, CurrentItems.getInstance().getArchiveMap().get(String.valueOf(markerDay)).get(markerIndex).lng);
                                    fromFrag = "archiveMap";
                                } else {
                                    hotelLatLng = new com.google.maps.model.LatLng(CurrentItems.getInstance().getSwipedRight().get(markerDay).get(markerIndex).getLat(), CurrentItems.getInstance().getSwipedRight().get(markerDay).get(markerIndex).getLng());
                                }

                                switch(which) {
                                    // Delete Marker
                                    case 0:
                                        if(getArguments() != null && getArguments().containsKey("type") && getArguments().getString("type").equals("Star") && markerIndex == 0) {
                                            Toast.makeText(getContext(), "You can't remove the starting hotel!", Toast.LENGTH_SHORT).show();
                                            break;
                                        }
                                        if(parentFrag.equals("archive")) {
                                            loadActionButton(view, 0);
                                            CurrentItems.getInstance().getArchiveMap().get(String.valueOf(markerDay)).remove(markerIndex);
                                        } else {
                                            CurrentItems.getInstance().getSwipedRight().get(markerDay).remove(markerIndex);
                                        }

                                        mMap.clear();
                                        prepareMap(view);
                                        break;
                                    // Search nearby hotel
                                    case 1:
                                        Bundle bundle = new Bundle();
                                        bundle.putDoubleArray("latlng", new double[]{hotelLatLng.lat, hotelLatLng.lng});
                                        bundle.putInt("markerIndex", markerIndex);
                                        bundle.putInt("markerDay", markerDay);
                                        bundle.putString("type", getArguments().getString("type"));
                                        bundle.putString("parentFrag", fromFrag);
                                        Navigation.findNavController(view).navigate(R.id.action_navigation_map_to_hotelSearchFragment, bundle);
                                        break;
                                    // Go to attraction page
                                    case 2:
                                        if(parentFrag.equals("archive")) {
                                            Toast.makeText(getContext(), "Cannot view attraction page of an archived trip.", Toast.LENGTH_SHORT).show();
                                            break;
                                        }
                                        ItemModel t = CurrentItems.getInstance().getSwipedRight().get(markerDay).get(markerIndex);

                                        AttractionPage attractionDialog = new AttractionPage(t);
                                        attractionDialog.show(requireActivity().getSupportFragmentManager(), "AttractionPage");

//                                            Bundle bundle2 = new Bundle();
//                                            bundle2.putSerializable("attraction", t);
//                                            Navigation.findNavController(view).navigate(R.id.action_navigation_map_to_attractionPageFragment, bundle2);
                                        break;
                                }

                            }
                        });
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

    // Handle spinner selection
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
        Log.d(TAG, "SPIN SELECTED " + pos);
        if(pos == 0) {
            currentMarkers.forEach((key, value) -> {
                for(Marker m : value) {
                    m.setVisible(true);
                }
            });
            currentPolylines.forEach((key, value) -> {
                for(Polyline p : value) {
                    p.setVisible(true);
                }
            });
        } else {
            currentMarkers.forEach((key, value) -> {
                if(key == pos-1) {
                    for(Marker m : value) {
                        m.setVisible(true);
                    }
                } else {
                    for(Marker m : value) {
                        m.setVisible(false);
                    }
                }
            });

            currentPolylines.forEach((key, value) -> {
                if(key == pos-1) {
                    for(Polyline p : value) {
                        p.setVisible(true);
                    }
                } else {
                    for(Polyline p : value) {
                        p.setVisible(false);
                    }
                }
            });
        }
    }

    private void loadActionButton(View view, int type) {
        btnArchive.setVisibility(View.VISIBLE);

        switch(type) {
            // CASE 0 - Save changes to archived trip
            case 0:
                btnArchive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage("Save Archive Edits?")
                                .setPositiveButton(R.string.alertOk, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        FireStoreUtils.updateArchivedTrip();
                                    }
                                })
                                .setNegativeButton(R.string.alertCancel, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // User cancelled the dialog
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }

                });
                break;
            // CASE 1 - Return to current trip
            case 1:
                btnArchive.setImageResource(R.drawable.ic_baseline_keyboard_tab_24);
                btnArchive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage("Return to current trip?")
                                .setPositiveButton(R.string.alertOk, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        mMap.clear();
                                        parentFrag = "map";
                                        prepareMap(view);
                                        loadActionButton(view, 2);
                                    }
                                })
                                .setNegativeButton(R.string.alertCancel, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // User cancelled the dialog
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                });
                break;
            // CASE 2 - Add current trip to the archive
            case 2:
                Log.d(TAG, "IN CASE 2");
                btnArchive.setImageResource(R.drawable.ic_baseline_archive_24);
                btnArchive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FireStoreUtils.archiveTrip(getContext());
                        CurrentItems.getInstance().reset();
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage("Go to Archives?")
                                .setPositiveButton(R.string.alertOk, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        mMap.clear();
                                        NavController navController = Navigation.findNavController(view);
                                        navController.navigateUp();
                                        navController.navigate(R.id.action_navigation_map_to_navigation_archive);
                                    }
                                })
                                .setNegativeButton(R.string.alertCancel, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // User cancelled the dialog
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                        }


                });
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}