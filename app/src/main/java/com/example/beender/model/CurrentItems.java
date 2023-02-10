package com.example.beender.model;

import com.google.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class CurrentItems {
    // Static variable reference of single_instance
    // of type Singleton
    private static CurrentItems single_instance = null;

    private HashMap<Integer, ArrayList<ItemModel>> currStack;
    private ArrayList<ItemModel> currStackHotels;
    private HashMap<Integer, ArrayList<ItemModel>> swipedRight;

    // Constructor
    // Here we will be creating private constructor
    // restricted to this class itself
    private CurrentItems()
    {
        currStack = new HashMap<>();
        currStackHotels = new ArrayList<>();
        swipedRight = new HashMap<>();
    }

    // Static method
    // Static method to create instance of Singleton class
    public static CurrentItems getInstance()
    {
        if (single_instance == null)
            single_instance = new CurrentItems();

        return single_instance;
    }

//    public ArrayList<ItemModel> getSwipedRight() {
//        return swipedRight;
//    }
//
//    public void setSwipedRight(ArrayList<ItemModel> swipedRight) {
//        this.swipedRight = swipedRight;
//    }


    public HashMap<Integer, ArrayList<ItemModel>> getSwipedRight() {
        return swipedRight;
    }

    public void setSwipedRight(HashMap<Integer, ArrayList<ItemModel>> swipedRight) {
        this.swipedRight = swipedRight;
    }

    public HashMap<Integer, ArrayList<ItemModel>> getCurrStack() {
        return currStack;
    }

    public ArrayList<ItemModel> getCurrStackHotels() {
        return currStackHotels;
    }

    public void setCurrStackHotels(ArrayList<ItemModel> currStackHotels) {
        this.currStackHotels = currStackHotels;
    }

    public List<LatLng> getAsLatLng (int day) {
        if(swipedRight != null) {
            List<LatLng> list = new ArrayList<>();
            for(ItemModel item : swipedRight.get(day)) {
                list.add(new LatLng(item.getLat(), item.getLng()));
            }
            return list;
        }
        return null;
    }
}
