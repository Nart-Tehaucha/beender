package com.example.beender.ui.dashboard;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.Location;
import android.opengl.Visibility;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DiffUtil;

import com.example.beender.BuildConfig;
import com.example.beender.CardStackAdapter;
import com.example.beender.CardStackCallback;
import com.example.beender.MainActivity;
import com.example.beender.model.CurrentItems;
import com.example.beender.model.ItemModel;
import com.example.beender.R;

import com.example.beender.util.FetchData;
import com.example.beender.util.FetchImage;
import com.example.beender.util.SearchNearby;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.CurrentLocationRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
//import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Dash;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.NearbySearchRequest;
import com.google.maps.PlacesApi;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.LatLng;
import com.google.maps.model.PlaceType;
import com.google.maps.model.PlacesSearchResponse;
import com.google.maps.model.PlacesSearchResult;
import com.google.maps.model.RankBy;
import com.google.maps.model.TravelMode;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.StackFrom;
import com.yuyakaido.android.cardstackview.SwipeableMethod;


import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class DashboardFragment extends Fragment {

    private static final String TAG = DashboardFragment.class.getSimpleName();
    private CardStackLayoutManager manager;
    private CardStackAdapter adapter;
    private String currentCardAttractionID;
    private FloatingActionButton btnStartTrip;
    private Button btnFinish;
    private ImageView testIV;



    // The entry point to the Places API.
    private PlacesClient placesClient;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient fusedLocationProviderClient;

    private AutocompleteSupportFragment autocompleteFragment;

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng defaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean locationPermissionGranted;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location lastKnownLocation;

    // Keys for storing activity state.
    private static final String KEY_LOCATION = "location";

    // Used for selecting the current place.
    private static final int M_MAX_ENTRIES = 5;
    private String[] likelyPlaceNames;
    private String[] likelyPlaceAddresses;
    private List[] likelyPlaceAttributions;
    private LatLng[] likelyPlaceLatLngs;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve location from saved instance state.
        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
        }

        // Construct a PlacesClient
        Places.initialize(getContext(), BuildConfig.MAPS_API_KEY);
        placesClient = Places.createClient(getContext());

        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());


    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        init(root);
        return root;
    }

    // Adds listener to the Add Trip button
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        
        //Get user's preferences (from 'SETTINGS' fragment)
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        btnFinish = view.findViewById(R.id.btnFinish);
        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(CurrentItems.getInstance().getSwipedRight().get(CurrentItems.getInstance().getCurrDay()).size() < 2) {
                    Toast.makeText(getContext(), "Pick at least two places!", Toast.LENGTH_SHORT).show();
                } else {
                    // If the trip type is "Star" and we are not at the final day of the trip - Go to the next day.
                    if(sharedPreferences.getString("kind_of_trip", "").equals("Star") && (Integer.parseInt(sharedPreferences.getString("numOfDaysForTravel", "")) > (CurrentItems.getInstance().getCurrDay()+1))) {
                        CurrentItems.getInstance().nextDay();
                        Log.d(TAG, sharedPreferences.getString("numOfDaysForTravel", "") + " " + CurrentItems.getInstance().getCurrDay());

                        // Update the text on the button to the day number.
                        btnFinish.setText("End Day " + (CurrentItems.getInstance().getCurrDay()+1));
                    }

                    // If the trip type is "Journey" OR if we reached the final day, navigate to the map fragment and display the routes.
                    else {
                        Navigation.findNavController(view).navigate(R.id.action_navigation_dashboard_to_navigation_map);
                    }
                }
            }

        });
        // Change button text from "Finish" to "End Day #" if the trip type is "Star"
        if(sharedPreferences.getString("kind_of_trip", "").equals("Star")) {
            btnFinish.setText("End Day " + (CurrentItems.getInstance().getCurrDay()+1));
        }
        if(CurrentItems.getInstance().getCurrStack().get(0).isEmpty()) {
            btnFinish.setVisibility(View.GONE);
        }

        btnStartTrip = view.findViewById(R.id.btnStartTrip);
        btnStartTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Bundle bundle = new Bundle();
//                bundle.putDoubleArray("latlng", new double[]{53.341050, -6.262876});
//                Navigation.findNavController(view).navigate(R.id.action_navigation_dashboard_to_hotelSearchFragment, bundle);
            }

        });

        // Initialize the AutocompleteSupportFragment.
        autocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {

                ((MainActivity)getActivity()).getLoadingDialog().show();
                Toast.makeText(getContext(), "Searching for cool places in  " + place.getName() +"...", Toast.LENGTH_SHORT).show();


                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        double lat = place.getLatLng().latitude;
                        double lng = place.getLatLng().longitude;
                        List<ItemModel> nearbyPlaces = null;
                        try {
                            nearbyPlaces = SearchNearby.getNearbyPlaces(lat, lng);
                        } catch (ExecutionException | InterruptedException e) {
                            e.printStackTrace();
                        }

                        List<ItemModel> finalNearbyPlaces = nearbyPlaces;
                        new Handler(Looper.getMainLooper()).post(() -> {


                            updateList(finalNearbyPlaces);

                            //If the trip type is a star, we first asל the user to choose a hotel.
                            if(sharedPreferences.getString("kind_of_trip", "").equals("Star")) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setMessage("Let's start by finding a Hotel!")
                                        .setPositiveButton(R.string.alertOk, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {

                                                // Navigate to find hotel fragment
                                                Bundle bundle = new Bundle();
                                                bundle.putDoubleArray("latlng", new double[]{place.getLatLng().latitude, place.getLatLng().longitude});
                                                bundle.putInt("markerIndex", 0);
                                                bundle.putString("parentFrag", "dashboard");
                                                Navigation.findNavController(view).navigate(R.id.action_navigation_dashboard_to_hotelSearchFragment, bundle);
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


                            btnFinish.setVisibility(View.VISIBLE);

                            ((MainActivity)getActivity()).getLoadingDialog().dismiss();
                        });
                    }
                }).start();
            }


            @Override
            public void onError(@NonNull Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });
    }

    private void init(View root) {
        CardStackView cardStackView = root.findViewById(R.id.card_stack_view);

        manager = new CardStackLayoutManager(getContext(), new CardStackListener() {

            @Override
            public void onCardDragging(Direction direction, float ratio) {
                //Log.d(TAG, "onCardDragging: d=" + direction.name() + " ratio=" + ratio);
            }

            @Override
            public void onCardSwiped(Direction direction) {
                Log.d(TAG, "onCardSwiped: p=" + manager.getTopPosition() + " d=" + direction + " manager.getItemCount() = " + manager.getItemCount());




                if (direction == Direction.Right){

                    ItemModel swipedItem = adapter.getItems().get(manager.getTopPosition() - 1);

                    Log.d(TAG, "onCardSwiped right: currentItem=" + swipedItem.getName());

                    CurrentItems.getInstance().addToSwipedRight(swipedItem);

                    // Remove the swiped item from CurrStack
                    CurrentItems.getInstance().getCurrStack().get(0).remove(swipedItem);
                }
                if (direction == Direction.Top){
                    //Toast.makeText(getContext(), "Direction Top "+currentCardAttractionID, Toast.LENGTH_SHORT).show();
                }
                if (direction == Direction.Left){
                    ItemModel swipedItem = adapter.getItems().get(manager.getTopPosition() - 1);

                    Log.d(TAG, "onCardSwiped left: currentItem=" + swipedItem.getName());

                    // Remove the swiped item from CurrStack
                    CurrentItems.getInstance().getCurrStack().get(0).remove(swipedItem);
                }
                if (direction == Direction.Bottom){
                    //Toast.makeText(getContext(), "Direction Bottom "+currentCardAttractionID, Toast.LENGTH_SHORT).show();
                }

                // Paginating
                if (manager.getTopPosition() == adapter.getItemCount() - 5){
//                    paginate();
                }

            }

            @Override
            public void onCardRewound() {
                Log.d(TAG, "onCardRewound: " + manager.getTopPosition());
            }

            @Override
            public void onCardCanceled() {
                Log.d(TAG, "onCardRewound: " + manager.getTopPosition());
            }

            @Override
            public void onCardAppeared(View view, int position) {
                TextView tv = view.findViewById(R.id.item_name);
                currentCardAttractionID = tv.getText().toString();
                Log.d(TAG, "onCardAppeared: " + position + ", nama: " + tv.getText());
            }

            @Override
            public void onCardDisappeared(View view, int position) {
                TextView tv = view.findViewById(R.id.item_name);
                Log.d(TAG, "onCardAppeared: " + position + ", nama: " + tv.getText());
            }
        });
        manager.setStackFrom(StackFrom.None);
        manager.setVisibleCount(3);
        manager.setTranslationInterval(8.0f);
        manager.setScaleInterval(0.95f);
        manager.setSwipeThreshold(0.3f);
        manager.setMaxDegree(20.0f);
        manager.setDirections(Direction.HORIZONTAL);
        manager.setCanScrollVertical(false);
        manager.setCanScrollHorizontal(true);
        manager.setSwipeableMethod(SwipeableMethod.Manual);
        manager.setOverlayInterpolator(new LinearInterpolator());
        adapter = new CardStackAdapter(addList());
        cardStackView.setLayoutManager(manager);
        cardStackView.setAdapter(adapter);
        cardStackView.setItemAnimator(new DefaultItemAnimator());


        cardStackView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            GestureDetector mGestureDetector = new GestureDetector(root.getContext(), new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    return super.onDoubleTap(e);
                }
            });

            public void onCardClicked() {
                ((MainActivity)getActivity()).getLoadingDialog().show();

                Log.d(TAG, "onCardClicked: p=" + manager.getTopPosition());

                ItemModel topItem = adapter.getItems().get(manager.getTopPosition() );

                Log.d(TAG, "onCardClicked: currentItem=" + topItem.getName());


                Bundle bundle = new Bundle();
                bundle.putSerializable("attraction", topItem);


                Navigation.findNavController(root).navigate(R.id.action_navigation_dashboard_to_attractionPageFragment, bundle);

            }

            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                if (mGestureDetector.onTouchEvent(e) && adapter != null && adapter.getItemCount() > 0) {
                    onCardClicked();
                }
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            }
        });
    }

//    private void paginate() {
//        List<ItemModel> oldList = adapter.getItems();
//        List<ItemModel> newList = new ArrayList<>(addList());
//        CardStackCallback callback = new CardStackCallback(oldList, newList);
//        DiffUtil.DiffResult results = DiffUtil.calculateDiff(callback);
//        adapter.setItems(newList);
//        results.dispatchUpdatesTo(adapter);
//    }
//
    private List<ItemModel> addList() {
        if(CurrentItems.getInstance().getCurrStack().containsKey(0)) {
            return (List<ItemModel>) CurrentItems.getInstance().getCurrStack().get(0).clone();
        }
        List<ItemModel> items = new ArrayList<>();
        return items;
    }

    private void updateList(List<ItemModel> newList) {
        if(newList == null) { return; }
        List<ItemModel> oldList = adapter.getItems();
        CardStackCallback callback = new CardStackCallback(oldList, newList);
        DiffUtil.DiffResult results = DiffUtil.calculateDiff(callback);
        adapter.setItems(newList);
        results.dispatchUpdatesTo(adapter);
    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    // [START maps_current_place_get_device_location]
    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                @SuppressLint("MissingPermission") Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(getActivity(), new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.getResult();
                            if (lastKnownLocation != null) {
                                try {
                                    updateList(SearchNearby.getNearbyPlaces(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()));
                                    ;
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }
    // [END maps_current_place_get_device_location]

    /**
     * Prompts the user for permission to use the device location.
     */
    // [START maps_current_place_location_permission]
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }
    // [END maps_current_place_location_permission]

    /**
     * Handles the result of the request for location permissions.
     */
    // [START maps_current_place_on_request_permissions_result]
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        if (requestCode
                == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationPermissionGranted = true;
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    // [END maps_current_place_on_request_permissions_result]
}

