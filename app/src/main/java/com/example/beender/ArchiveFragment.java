package com.example.beender;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ArchiveFragment extends Fragment implements TravelMapsAdapter.ItemClickListener{
        private String currentUserID;
        public static ArrayList<TravelMap> allTravelMapsOfUser = new ArrayList<>();

        //Recycler view
        private RecyclerView archiveTravelMapRecyclerView;
        //This is the adapter for the recycler
        public static TravelMapsAdapter travelMapsAdapter;
        //This list contain the products that will appear in recycler view
        public static List<TravelMap> travelMapsList = new ArrayList<>();
        private TextView categoryNameTV;
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
            if (getArguments() != null) {
//                productCategoryInput = getArguments().getString("productCategory");
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            if (getArguments() != null) {
//                currentUserID = getArguments().getString("currentUserID");
            }
            // Inflate the layout for this fragment
            View view = inflater.inflate(R.layout.fragment_archive, container, false);

            //Fill the recycler
            context = container.getContext();
            archiveTravelMapRecyclerView = view.findViewById(R.id.productsRecyclerView);
            constraintLayout = view.findViewById(R.id.fragmentScreen);
            categoryNameTV = view.findViewById(R.id.categoryNameTV);

            // HERE WE WILL INSERT ALL USER'S OLD MAPS INTO RECYCLER VIEW (THIS ONE IS FROM MY OLD EXERCISE FOR EXAMPLE- HOW TO GET OBJECTS INTO VIEW FROM FIRESTORE)

            // Get all existing products
//            FirebaseFirestore.getInstance().collection("Product").addSnapshotListener(new EventListener<QuerySnapshot>() {
//                @Override
//                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
//                    allTravelMapsOfUser = new ArrayList<>();
//                    travelMapsList = new ArrayList<>();
//                    if (e != null){
//                        Log.e("ERROR ON DATA EVENT", "onEvent:" , e);
//                        return;
//                    }
//                    if(queryDocumentSnapshots != null){
//                        List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();
//                        for(DocumentSnapshot snapshot : snapshotList){
//                            for(Object object : snapshot.getData().values()){
//                                ProductInDB prodInDB = new ProductInDB((Map<String, String>) object);
//                                allTravelMapsOfUser.add(prodInDB);
//                            }
//                        }
//                    }
//                    for (ProductInDB prod : allTravelMapsOfUser){
//                        if(prod != null && prod.getCategoryName() != null && productCategoryInput != null) {
//                            if (prod.getCategoryName().equals(productCategoryInput)) {
//                                if (!travelMapsList.contains(new Product(prod)))
//                                    travelMapsList
//
//
//                                            .add(new Product(prod));
//                            }
//                        }
//                    }
//
//                    // FOR NOW I'LL ADD HARD CODED
//                    TravelMap tm = new TravelMap(123, 2131230892, "NAME","description");
//                    TravelMap tm2 = new TravelMap(123, 2131230892, "NAME","description");
//                    TravelMap tm3= new TravelMap(123, 2131230892, "NAME","description");
//                    TravelMap tm4 = new TravelMap(123, 2131230892, "NAME","description");
//                    TravelMap tm5= new TravelMap(123, 2131230892, "NAME","description");
//                    TravelMap tm6 = new TravelMap(123, 2131230892, "NAME","description");
//                    TravelMap tm7 = new TravelMap(123, 2131230892, "NAME","description");
//
//                    allTravelMapsOfUser = new ArrayList<>();
//                    allTravelMapsOfUser.add(tm);
//                    allTravelMapsOfUser.add(tm2);
//                    allTravelMapsOfUser.add(tm3);
//                    allTravelMapsOfUser.add(tm4);
//                    allTravelMapsOfUser.add(tm5);
//                    allTravelMapsOfUser.add(tm6);
//                    allTravelMapsOfUser.add(tm7);
//
//                    //setProductRecycler(allProductsList);
//                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
//                    archiveTravelMapRecyclerView.setLayoutManager(layoutManager);
//                    travelMapsAdapter = new TravelMapsAdapter(getActivity(),allTravelMapsOfUser, this::onItemClickRemove,this::onItemClickEdit);
//                    archiveTravelMapRecyclerView.setAdapter(travelMapsAdapter);
////                    resetDataList();
//                }
//                public void onItemClickRemove(TravelMap tm) {
////                    deleteTravelMap(tm);
//                }
//
//                //This function open a dialog box that asks the user if he sure that
//                //he want to edit the item . if no- nothing happens.
//                //if yes- the user is going to edit item screen.
//                public void onItemClickEdit(TravelMap tm) {
////                    editTravelMap(product);
//                }
//            });





            // FOR NOW I'LL ADD HARD CODED
                    TravelMap tm = new TravelMap(123, R.drawable.map_icon, "MAP 1","description", new Date());
                    TravelMap tm2 = new TravelMap(123, R.drawable.map_icon, "MAP 2","description", new Date());
                    TravelMap tm3= new TravelMap(123, R.drawable.map_icon, "MAP 3","description",new Date() );
                    TravelMap tm4 = new TravelMap(123, R.drawable.map_icon, "MAP 4","description",new Date());
                    TravelMap tm5= new TravelMap(123, R.drawable.map_icon, "MAP 5","description", new Date());
                    TravelMap tm6 = new TravelMap(123, R.drawable.map_icon, "MAP 6","description" , new Date());
                    TravelMap tm7 = new TravelMap(123, R.drawable.map_icon, "MAP 7","description" , new Date());

                    allTravelMapsOfUser = new ArrayList<>();
                    allTravelMapsOfUser.add(tm);
                    allTravelMapsOfUser.add(tm2);
                    allTravelMapsOfUser.add(tm3);
                    allTravelMapsOfUser.add(tm4);
                    allTravelMapsOfUser.add(tm5);
                    allTravelMapsOfUser.add(tm6);
                    allTravelMapsOfUser.add(tm7);

                    //setProductRecycler(allProductsList);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                    archiveTravelMapRecyclerView.setLayoutManager(layoutManager);
                    travelMapsAdapter = new TravelMapsAdapter(getActivity(),allTravelMapsOfUser, this::onItemClickRemove,this::onItemClickEdit);
                    archiveTravelMapRecyclerView.setAdapter(travelMapsAdapter);
                    //This function open a dialog box that asks the user if he sure that
                    //he want to edit the item . if no- nothing happens.
                    //if yes- the user is going to edit item screen.

                return view;
        }

    private void onItemClickEdit(TravelMap travelMap) {
        editTravelMap(travelMap);
    }

    private void onItemClickRemove(TravelMap travelMap) {
            deleteTravelMap(travelMap);
    }

    @Override
        public void onItemClick(TravelMap tm) {
            //Not necessery because not in use.
        }

        //This function remove open a dialog box that asks the user if he sure that
        //he want to delete the item. if no- nothing happens.
        //if yes- item is deleted.
        public boolean deleteTravelMap(TravelMap tm){
            AlertDialog.Builder builder;
            builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Wait").setMessage("Do you want to delete " + tm.getName() + "?")
                    .setCancelable(true)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //Delete Item from DB
                            Snackbar.make(constraintLayout, tm.getName() + " removed", Snackbar.LENGTH_LONG).show();
                            //Toast.makeText(getActivity(), product.getName() + " removed", Toast.LENGTH_SHORT).show();
                            dialogInterface.cancel();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    })
                    .show();
            return true;
        }



        public boolean editTravelMap(TravelMap tm){
            AlertDialog.Builder builder;
            builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Wait").setMessage("Do you want to edit " + tm.getName() + "?")
                    .setCancelable(true)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //Switch to edit map details fragment
                            Toast.makeText(getActivity(), "You want to edit" + tm.getName(), Toast.LENGTH_SHORT).show();
                            dialogInterface.cancel();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    })
                    .show();
            return true;
        }

        public static void resetDataList(){
            //Empty array list
            travelMapsAdapter.notifyDataSetChanged();
            return;
        }


    }