package com.example.beender;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;

public class TravelMapsAdapter extends RecyclerView.Adapter<TravelMapsAdapter.TravelMapViewHolder>{
    private Context context;
    private List<TravelMap> travelMapstList;

    private com.example.beender.TravelMapsAdapter.ItemClickListener clickListenerRemove;
    private com.example.beender.TravelMapsAdapter.ItemClickListener clickListenerEdit;

    public TravelMapsAdapter(Context context, List<TravelMap> travelMapstList, com.example.beender.TravelMapsAdapter.ItemClickListener clickListenerRemove, com.example.beender.TravelMapsAdapter.ItemClickListener clickListenerEdit ) {
        this.context = context;
        this.travelMapstList = travelMapstList;
        this.clickListenerRemove = clickListenerRemove;
        this.clickListenerEdit = clickListenerEdit;
    }

    @NonNull
    @Override
    public com.example.beender.TravelMapsAdapter.TravelMapViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.archive_travelmap_item, parent, false);
        return new com.example.beender.TravelMapsAdapter.TravelMapViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull com.example.beender.TravelMapsAdapter.TravelMapViewHolder holder, int position) {
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");


        holder.productImage.setImageResource(travelMapstList.get(position).getImageurl());
        holder.productDescription.setText(travelMapstList.get(position).getDescription().toString());
        holder.productName.setText(travelMapstList.get(position).getName());
        holder.date.setText(formatter.format(travelMapstList.get(position).getDate()));
        //TO-DO: change from price to ID
        //holder.productPrice.setText(travelMapstList.get(position).getId());

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

        ImageView productImage;
        TextView productName;
        TextView date;
        TextView productDescription;

        //Delete button
        ImageView deleteIV;
        //Edit button
        ImageView editIV;


        public TravelMapViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productInCartImageView);
            productName = itemView.findViewById(R.id.product_in_cart_name);
            date = itemView.findViewById(R.id.date);
            productDescription = itemView.findViewById(R.id.descTextview);
            deleteIV = itemView.findViewById(R.id.deleteIV);
            editIV = itemView.findViewById(R.id.editIV);
        }
    }

    public interface ItemClickListener {
        public void onItemClick(TravelMap travelMap);
    }
}


