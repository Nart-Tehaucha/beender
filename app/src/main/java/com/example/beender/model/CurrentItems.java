package com.example.beender.model;

import java.util.ArrayList;
import java.util.HashSet;

public class CurrentItems {
    // Static variable reference of single_instance
    // of type Singleton
    private static CurrentItems single_instance = null;

    // Declaring a variable of type String
    public ArrayList<ItemModel> currSet;

    // Constructor
    // Here we will be creating private constructor
    // restricted to this class itself
    private CurrentItems()
    {
        currSet = new ArrayList<>();
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
}
