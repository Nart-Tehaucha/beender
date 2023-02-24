package com.example.beender;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
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
import com.example.beender.util.SearchNearby;
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
                    int markerDay = getArguments().getInt("markerDay");

                    ItemModel swipedItem = adapter.getItems().get(manager.getTopPosition() - 1);

                    if(getArguments().getString("parentFrag").equals("archiveMap")) {
                        LatLng latLng = new LatLng(swipedItem.getLat(), swipedItem.getLng());
                        CurrentItems.getInstance().getArchiveMap().get(String.valueOf(markerDay)).add(markerIndex, latLng);
                    } else {
                        CurrentItems.getInstance().getSwipedRight().get(markerDay).add(markerIndex, swipedItem);
                        CurrentItems.getInstance().setChosenHotel(swipedItem);
                    }

                    // If a hotel was swiped right, reset the card stack and navigate back to the map fragment
                    if(swipedItem.getType() == 1) {
                        CurrentItems.getInstance().setCurrStackHotels(new ArrayList<>());
                        updateList(new ArrayList<>());
                        if(getArguments().getString("parentFrag").equals("archiveMap")) {
                            Bundle bundle = new Bundle();
                            bundle.putString("parentFrag", "archive");
                            bundle.putString("type", getArguments().getString("type"));
                            bundle.putBoolean("archiveEdited", true);
                            NavController navController = Navigation.findNavController(root);
                            navController.navigate(R.id.action_hotelSearchFragment_to_navigation_map, bundle);
                        } else if(getArguments().getString("parentFrag").equals("map")) {
                            Navigation.findNavController(root).navigate(R.id.action_hotelSearchFragment_to_navigation_map);
                        } else {
                            Navigation.findNavController(root).navigate(R.id.action_hotelSearchFragment_to_navigation_dashboard);
                        }
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
        updateList(SearchNearby.getNearbyPlaces(location.lat, location.lng, "lodging"));
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

}