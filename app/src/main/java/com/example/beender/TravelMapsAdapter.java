package com.example.beender;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.navigation.NavController;
import androidx.navigation.NavDeepLinkBuilder;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beender.model.CurrentItems;
import com.example.beender.model.UserTrip;
import com.example.beender.util.FireStoreUtils;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.List;

public class TravelMapsAdapter extends RecyclerView.Adapter<TravelMapsAdapter.TravelMapViewHolder>{
    private Context context;
    private List<UserTrip> travelMapstList;

    public TravelMapsAdapter(Context context, List<UserTrip> userTripList) {
        this.context = context;
        this.travelMapstList = userTripList;
    }

    public void setUserTripList(List<UserTrip> userTripList){
        this.travelMapstList = userTripList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public com.example.beender.TravelMapsAdapter.TravelMapViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.archive_travelmap_item, parent, false);
        return new com.example.beender.TravelMapsAdapter.TravelMapViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull com.example.beender.TravelMapsAdapter.TravelMapViewHolder holder, int position) {
        UserTrip ut = travelMapstList.get(position);

        String imgPath = "thumbnails/" + ut.getId() + ".jpg";
        FireStoreUtils.downloadImage(holder.tripImage, imgPath, context);

        //holder.productImage.setImageResource(travelMapstList.get(position).getImageurl());

        holder.tripName.setText(ut.getTitle());
        holder.tripDate.setText(ut.getDateTime());


        //Set on click for remove & insert buttons
        holder.deleteIV.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                deleteTravelMap(travelMapstList.get(position), v);
            }
        });

        holder.tripImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                loadToMap(travelMapstList.get(position), v);
            }
        });


    }

    @Override
    public int getItemCount() {
        return travelMapstList.size();
    }

    public  static class TravelMapViewHolder extends RecyclerView.ViewHolder{

        ImageView tripImage;
        TextView tripName;
        TextView tripDate;

        //Delete button
        ImageView deleteIV;
        //Edit button
        ImageView editIV;


        public TravelMapViewHolder(@NonNull View itemView) {
            super(itemView);
            tripImage = itemView.findViewById(R.id.productInCartImageView);
            tripName = itemView.findViewById(R.id.product_in_cart_name);
            tripDate = itemView.findViewById(R.id.date);
            deleteIV = itemView.findViewById(R.id.deleteIV);
        }
    }

    public void loadToMap(UserTrip t, View view) {
        CurrentItems.getInstance().setArchiveMap(t.getSwipedRight());
        CurrentItems.getInstance().setCurrArchive(t);
        Bundle bundle = new Bundle();
        bundle.putString("parentFrag", "archive");
        bundle.putString("type", t.getType());
        NavController navController = Navigation.findNavController(view);
//        navController.navigate(R.id.action_navigation_archive_to_navigation_map, bundle);
        navController.navigate(R.id.action_navigation_archive_to_itinerary, bundle);

    }

    //This function remove open a dialog box that asks the user if he sure that
    //he want to delete the item. if no- nothing happens.
    //if yes- item is deleted.
    public boolean deleteTravelMap(UserTrip tm, View view){
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle("Wait").setMessage("Do you want to delete " + tm.getTitle() + "?")
                .setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
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
}


