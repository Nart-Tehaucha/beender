package com.example.beender.model;

import com.google.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class CurrentItems {
    // Static variable reference of single_instance
    // of type Singleton
    private static CurrentItems single_instance = null;

    private ArrayList<ItemModel> swipedRight;
    private ArrayList<ItemModel> currSet;

    // Constructor
    // Here we will be creating private constructor
    // restricted to this class itself
    private CurrentItems()
    {
        currSet = new ArrayList<>();
        swipedRight = new ArrayList<>();
    }

    // Static method
    // Static method to create instance of Singleton class
    public static CurrentItems getInstance()
    {
        if (single_instance == null)
            single_instance = new CurrentItems();

        return single_instance;
    }

    public ArrayList<ItemModel> getCurrSet() {
        return currSet;
    }

    public void setCurrSet(ArrayList<ItemModel> currSet) {
        this.currSet = currSet;
    }

    public ArrayList<ItemModel> getSwipedRight() { return swipedRight; }

    public void setSwipedRight(ArrayList<ItemModel> swipedRight) { this.swipedRight = swipedRight; }

    public List<LatLng> getAsLatLng () {
        if(swipedRight != null) {
            List<LatLng> list = new ArrayList<>();
            for(ItemModel item : swipedRight) {
                list.add(new LatLng(item.getLat(), item.getLng()));
            }
            return list;
        }
        return null;
    }
}
