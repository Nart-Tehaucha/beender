package com.example.beender;


import android.app.Activity;
import android.content.Context;
import android.media.Image;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Context;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.denzcoskun.imageslider.ImageSlider;
//import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.denzcoskun.imageslider.models.SlideModel;
//import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

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
        ImageSlider imageSlider;
        //ImageView image, image2;
        int image, getImage2;
        TextView nama, usia, kota;
        //Shahar
        List <SlideModel> imageList  = new ArrayList<>();
        LinearLayout linear_layout;


        ViewHolder(@NonNull View itemView) {
            super(itemView);
            linear_layout = itemView.findViewById(R.id.linear_layout);
            imageSlider = itemView.findViewById(R.id.image_slider);


//            image2 = itemView.findViewById(R.id.item_image2);
//            image = itemView.findViewById(R.id.item_image);
            nama = itemView.findViewById(R.id.item_name);


            usia = itemView.findViewById(R.id.item_age);
            kota = itemView.findViewById(R.id.item_city);
            imageList.add(new SlideModel(R.drawable.sample1 , ScaleTypes.CENTER_CROP)); // for one image
            imageList.add(new SlideModel(R.drawable.sample2 , ScaleTypes.CENTER_CROP)); // for one image
            imageSlider.setImageList(imageList, ScaleTypes.FIT); // for all images
        }


        void setData(ItemModel data) {
//            Picasso.get()
//                    .load(data.getImage())
//                    .fit()
//                    .centerCrop()
//                    .into(image);
//            Picasso.get()
//                    .load(data.getImage2())
//                    .fit()
//                    .centerCrop()
//                    .into(image2);

//            imageList.add(SlideModel("https://bit.ly/2YoJ77H", "The animal population decreased by 58 percent in 42 years."));
//            imageList.add(SlideModel("https://bit.ly/2BteuF2", "Elephants and tigers may become extinct."));
//            imageList.add(SlideModel("https://bit.ly/3fLJf72", "And people do that."));

            nama.setText(data.getNama());
            usia.setText(data.getUsia());
            kota.setText(data.getKota());
            linear_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //When user is clicking on card
                    Log.i("Shahar", data.getNama());
                    Toast.makeText(imageSlider.getContext(), data.getNama() +" card just clicked", Toast.LENGTH_SHORT).show();
//                    Activity activity = (Activity) view.getContext();
//                    activity.getFragmentManager().beginTransaction().replace(R.id.FrameLayout, fragment).commit();
//                }
//
//                    AttractionPage fragment1 = new AttractionPage();
//                    FragmentManager fm = fragment1.getFragmentManager();
//                    Bundle args = new Bundle();
//                    FragmentTransaction ft = fm.beginTransaction();
//                    ft.replace(R.id.mainCardLayout,fragment1);
//                    ft.addToBackStack(null);
//                    ft.commit();
//                    androidx.fragment.app.Fragment fragment = new AttractionPage();
//                    FragmentTransaction ft = this.getActivity().getSupportFragmentManager();
//                    ft.replace(R.id.mainFrame, fragment);
//                    ft.commit();
//                    ((FragmentActivity) view.getContext()).getFragmentManager().beginTransaction()
//                            .replace(R.id.mainCardLayout, new AttractionPage())
//                            .commit();
                }
            });
        }
    }

    public List<ItemModel> getItems() {
        return items;
    }

    public void setItems(List<ItemModel> items) {
        this.items = items;
    }
}
