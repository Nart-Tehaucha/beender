package com.example.beender;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.beender.model.UserTrip;
import com.example.beender.util.FireStoreUtils;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.List;

public class TravelMapsAdapter extends RecyclerView.Adapter<TravelMapsAdapter.TravelMapViewHolder>{
    private Context context;
    private List<UserTrip> travelMapstList;

    private com.example.beender.TravelMapsAdapter.ItemClickListener clickListenerRemove;
    private com.example.beender.TravelMapsAdapter.ItemClickListener clickListenerEdit;

    public TravelMapsAdapter(Context context, List<UserTrip> travelMapstList, com.example.beender.TravelMapsAdapter.ItemClickListener clickListenerRemove, com.example.beender.TravelMapsAdapter.ItemClickListener clickListenerEdit ) {
        this.context = context;
        this.travelMapstList = travelMapstList;
        this.clickListenerRemove = clickListenerRemove;
        this.clickListenerEdit = clickListenerEdit;
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
                clickListenerRemove.onItemClick(travelMapstList.get(position));
            }
        });

        holder.editIV.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                clickListenerEdit.onItemClick(travelMapstList.get(position));
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
            editIV = itemView.findViewById(R.id.editIV);
        }
    }

    public interface ItemClickListener {
        public void onItemClick(UserTrip travelMap);
    }
}


