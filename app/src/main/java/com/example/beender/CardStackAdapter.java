package com.example.beender;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.beender.model.ItemModel;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CardStackAdapter extends RecyclerView.Adapter<CardStackAdapter.ViewHolder> {

    private List<ItemModel> items;

    public CardStackAdapter(List<ItemModel> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        TextView name, city, country, rating;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.item_image);
            name = itemView.findViewById(R.id.item_name);
//            city = itemView.findViewById(R.id.item_city);
            country = itemView.findViewById(R.id.item_country);
            rating = itemView.findViewById(R.id.item_rating);
        }

        void setData(ItemModel data) {
            image.setImageBitmap(data.getImage());

            data.setMainImageLoadedListener(new Runnable() {
                @Override
                public void run() {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        image.setImageBitmap(data.getImage());
                    });
                }
            });

            name.setText(data.getName());
//            city.setText(data.getCity());
            country.setText(data.getCountry());
            rating.setText(data.getRating());
        }
    }

    public List<ItemModel> getItems() {
        return items;
    }

    public void setItems(List<ItemModel> items) {
        this.items = items;
    }
}
