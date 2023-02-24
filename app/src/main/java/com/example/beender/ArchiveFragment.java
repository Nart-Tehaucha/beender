package com.example.beender;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.beender.model.CurrentItems;
import com.example.beender.model.UserTrip;
import com.example.beender.ui.dashboard.DashboardFragment;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ArchiveFragment extends Fragment {

    private FirebaseAuth mAuth;

    private static final String TAG = DashboardFragment.class.getSimpleName();

    private RecyclerView archiveTravelMapRecyclerView;
    public static TravelMapsAdapter travelMapsAdapter;
    public static List<UserTrip> trips;

    private ConstraintLayout constraintLayout;

    private Context context;

    public ArchiveFragment() {
        // Required empty public constructor
    }

    public static ArchiveFragment newInstance(String param1, String param2) {
        ArchiveFragment fragment = new ArchiveFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        trips = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_archive, container, false);

        //Fill the recycler
        context = container.getContext();
        archiveTravelMapRecyclerView = view.findViewById(R.id.productsRecyclerView);
        constraintLayout = view.findViewById(R.id.fragmentScreen);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        archiveTravelMapRecyclerView.setLayoutManager(layoutManager);
        travelMapsAdapter = new TravelMapsAdapter(getActivity(),trips);
        archiveTravelMapRecyclerView.setAdapter(travelMapsAdapter);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("trips")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshot,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        if (snapshot != null && !snapshot.isEmpty()) {

                            // Updated users (Add/Update/Delete) after changes were made in the Firestore DB
                            for (DocumentChange document : snapshot.getDocumentChanges() ){
                                UserTrip ut = document.getDocument().toObject(UserTrip.class);
                                if(document.getType() == DocumentChange.Type.REMOVED) {
                                } else {
                                    // Add all trips that this user has archived
                                    if(ut != null && !trips.contains(ut) && ut.getUserEmail().equals(mAuth.getCurrentUser().getEmail())) {
                                        trips.add(ut);
                                    }
                                }
                            }

                        } else {
                        }
                        travelMapsAdapter.notifyDataSetChanged();
                    }
                });

            return view;
    }





    public static void resetDataList(){
        //Empty array list
        travelMapsAdapter.notifyDataSetChanged();
        return;
    }


}