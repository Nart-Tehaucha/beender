package com.example.beender.ui.dashboard;

import android.graphics.Bitmap;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DiffUtil;

import com.example.beender.BuildConfig;
import com.example.beender.CardStackAdapter;
import com.example.beender.CardStackCallback;
import com.example.beender.ItemModel;
import com.example.beender.R;

import com.example.beender.util.FetchData;
import com.example.beender.util.FetchImage;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.StackFrom;
import com.yuyakaido.android.cardstackview.SwipeableMethod;


import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class DashboardFragment extends Fragment {

    private static final String TAG = DashboardFragment.class.getSimpleName();
    private CardStackLayoutManager manager;
    private CardStackAdapter adapter;
    private String currentCardAttractionID;
    private FloatingActionButton btnStartTrip;
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

        btnStartTrip = view.findViewById(R.id.btnStartTrip);
        btnStartTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Initialize Places API request
                // Prompt the user for permission.
                getLocationPermission();
                // [END_EXCLUDE]

                Toast.makeText(getContext(), "Searching for cool places nearby...", Toast.LENGTH_SHORT).show();
                // Get the current location of the device and set the position of the map.
                getDeviceLocation();
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

                Toast.makeText(getContext(), "Searching for cool places in  " + place.getName() +"...", Toast.LENGTH_SHORT).show();

                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
                double lat = place.getLatLng().latitude;
                double lng = place.getLatLng().longitude;
                try {
                    getNearbyPlaces(lat,lng);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
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
                Log.d(TAG, "onCardDragging: d=" + direction.name() + " ratio=" + ratio);
            }

            @Override
            public void onCardSwiped(Direction direction) {
                Log.d(TAG, "onCardSwiped: p=" + manager.getTopPosition() + " d=" + direction);
                if (direction == Direction.Right){
                    //Toast.makeText(getContext(), "Direction Right " +currentCardAttractionID , Toast.LENGTH_SHORT).show();
                }
                if (direction == Direction.Top){
                    //Toast.makeText(getContext(), "Direction Top "+currentCardAttractionID, Toast.LENGTH_SHORT).show();
                }
                if (direction == Direction.Left){
                    //Toast.makeText(getContext(), "Direction Left "+currentCardAttractionID, Toast.LENGTH_SHORT).show();
                }
                if (direction == Direction.Bottom){
                    //Toast.makeText(getContext(), "Direction Bottom "+currentCardAttractionID, Toast.LENGTH_SHORT).show();
                }

                // Paginating
                if (manager.getTopPosition() == adapter.getItemCount() - 5){
                    paginate();
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
        manager.setDirections(Direction.FREEDOM);
        manager.setCanScrollHorizontal(true);
        manager.setSwipeableMethod(SwipeableMethod.Manual);
        manager.setOverlayInterpolator(new LinearInterpolator());
        adapter = new CardStackAdapter(addList());
        cardStackView.setLayoutManager(manager);
        cardStackView.setAdapter(adapter);
        cardStackView.setItemAnimator(new DefaultItemAnimator());
    }

    private void paginate() {
        List<ItemModel> oldList = adapter.getItems();
        List<ItemModel> newList = new ArrayList<>(addList());
        CardStackCallback callback = new CardStackCallback(oldList, newList);
        DiffUtil.DiffResult results = DiffUtil.calculateDiff(callback);
        adapter.setItems(newList);
        results.dispatchUpdatesTo(adapter);
    }

    private List<ItemModel> addList() {
        List<ItemModel> items = new ArrayList<>();
        return items;
    }

    private void updateList(List<ItemModel> newList) {
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
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(getActivity(), new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.getResult();
                            if (lastKnownLocation != null) {
                                try {
                                    getNearbyPlaces(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
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

    /**
     * Sends our current location through an HTTP request to Places API and receives a list of nearby places.
     * @param lat lng
     */
    public void getNearbyPlaces(double lat, double lng) throws ExecutionException, InterruptedException {
        StringBuilder stringBuilder = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        //stringBuilder.append("location=" + "32.804653, 34.999608"); // Test location for London.
        stringBuilder.append("location=" + lat + "," + lng);
        stringBuilder.append("&radius=3500");
        stringBuilder.append("&type=tourist_attraction");
        stringBuilder.append("&key=" + BuildConfig.MAPS_API_KEY);

        //String tstUrl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=-33.8670522%2C151.1957362&radius=1500&type=restaurant&keyword=cruise&key=" + BuildConfig.MAPS_API_KEY;

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


                List<ItemModel> items = new ArrayList<>();
                for (int i=0; i < jarr.length(); i++) {
                    //Log.d(TAG, "Item NUMBER " + i + "- " + jarr.get(i).toString());
                    if(((JSONObject) jarr.get(i)).has("photos")) {
                        JSONObject temp = jarr.getJSONObject(i);
                        String pName = temp.get("name").toString();
                        String pCity = temp.get("vicinity").toString();
                        String pCountry = temp.get("vicinity").toString();
                        Bitmap pImage = getPlacePhoto(((JSONObject) ((JSONArray) ((JSONObject) jarr.get(i)).get("photos")).get(0)).get("photo_reference").toString());
                        items.add(new ItemModel(pImage, pName, pCity, pCountry));
                    }
                }

                updateList(items);

//                photoReference = ((JSONObject) ((JSONArray) ((JSONObject) jarr.get(0)).get("photos")).get(0)).get("photo_reference").toString();
//
//                getPlacePhoto(photoReference);
//                Log.d(TAG, "REF - " + photoReference);
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
        }


        //Toast.makeText(getContext(), "HTTP SUCCESS ", Toast.LENGTH_SHORT).show() ;
    }

    // Recieves a photo_reference of a place, sends an HTTP request to Places API, and converts the result to a Bitmap photo.
    private Bitmap getPlacePhoto(String photoReference) throws IOException, ExecutionException, InterruptedException {
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

