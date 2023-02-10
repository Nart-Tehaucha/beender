package com.example.beender;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DiffUtil;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import com.example.beender.model.CurrentItems;
import com.example.beender.model.ItemModel;
import com.example.beender.ui.dashboard.DashboardFragment;
import com.example.beender.util.FetchImage;
import com.google.maps.NearbySearchRequest;
import com.google.maps.PlacesApi;
import com.google.maps.model.LatLng;
import com.google.maps.model.PlaceType;
import com.google.maps.model.PlacesSearchResponse;
import com.google.maps.model.PlacesSearchResult;
import com.google.maps.model.RankBy;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.StackFrom;
import com.yuyakaido.android.cardstackview.SwipeableMethod;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class HotelSearchFragment extends Fragment {

    private static final String TAG = DashboardFragment.class.getSimpleName();
    private CardStackLayoutManager manager;
    private CardStackAdapter adapter;
    private String currentCardAttractionID;

    public HotelSearchFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_hotel_search, container, false);
        try {
            init(root);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return root;
    }

    private void init(View root) throws ExecutionException, InterruptedException {
        CardStackView cardStackView = root.findViewById(R.id.hotel_card_stack_view);
        manager = new CardStackLayoutManager(getContext(), new CardStackListener() {
            @Override
            public void onCardDragging(Direction direction, float ratio) {
                //Log.d(TAG, "onCardDragging: d=" + direction.name() + " ratio=" + ratio);
            }

            @Override
            public void onCardSwiped(Direction direction) {
                Log.d(TAG, "onCardSwiped: p=" + manager.getTopPosition() + " d=" + direction);
                if (direction == Direction.Right){

                    int markerIndex = getArguments().getInt("markerIndex");

                    ItemModel swipedItem = adapter.getItems().get(manager.getTopPosition() - 1);

                    if(!CurrentItems.getInstance().getSwipedRight().containsKey(0)) {
                        CurrentItems.getInstance().getSwipedRight().put(0, new ArrayList<>());
                    }
                    CurrentItems.getInstance().getSwipedRight().get(0).add(markerIndex, swipedItem);

                    if(swipedItem.getType() == 1) {
                        CurrentItems.getInstance().setCurrStackHotels(new ArrayList<>());
                        updateList(new ArrayList<>());
                        Navigation.findNavController(root).navigate(R.id.action_hotelSearchFragment_to_navigation_map);
                    }
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
        manager.setDirections(Direction.FREEDOM);
        manager.setCanScrollHorizontal(true);
        manager.setSwipeableMethod(SwipeableMethod.Manual);
        manager.setOverlayInterpolator(new LinearInterpolator());
        adapter = new CardStackAdapter(addList());
        cardStackView.setLayoutManager(manager);
        cardStackView.setAdapter(adapter);
        cardStackView.setItemAnimator(new DefaultItemAnimator());

        com.google.maps.model.LatLng location = new LatLng(getArguments().getDoubleArray("latlng")[0], getArguments().getDoubleArray("latlng")[1]);
        getNearbyHotels(location);
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

    public void getNearbyHotels(LatLng location) throws ExecutionException, InterruptedException {
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

                    items.add(new ItemModel(pImage, pName, pCity, pCountry, pRating, pLat, pLng, 1));
                }

                // Update the singleton CurrentItems to contain our generated list of places
                CurrentItems.getInstance().setCurrStackHotels(new ArrayList<>(items));
                updateList(items);
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
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