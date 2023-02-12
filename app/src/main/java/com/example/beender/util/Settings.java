package com.example.beender.util;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import java.util.HashSet;
import java.util.Set;

public class Settings {
    SharedPreferences sharedPreferences;

    public Settings(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public String getKindOfTrip() {
        return sharedPreferences.getString("kind_of_trip","");
    }

    public String getNumOfPlacesPerDay() {
        return sharedPreferences.getString("numOfPlacesPerDay","");
    }

    public Integer getKmRadius() {
        return sharedPreferences.getInt("kmRadius", 1 );
    }

    public String getNumOfDaysForTravel() {
        return sharedPreferences.getString("numOfDaysForTravel","");
    }

    public Set<String> getHoursOfTravel() {
        return sharedPreferences.getStringSet("hoursOfTravel",new HashSet<>());
    }

    public Boolean getAdaptedForChildren() {
        return sharedPreferences.getBoolean("adaptedForChildren",false);
    }

    public Boolean getAdaptedForElders() {
        return sharedPreferences.getBoolean("adaptedForChildren",false);
    }

    public Boolean getAdaptedForAWheelchair() {
        return sharedPreferences.getBoolean("adaptedForAWheelchair",false);
    }

    public String getRatingStar() {
        return sharedPreferences.getString("ratingStar","");
    }

    public Set<String> getTypeOfPlaces() {
        return sharedPreferences.getStringSet("TypeOfPlaces",new HashSet<>());
    }

    public Set<String> getPriceLevel() {
        return sharedPreferences.getStringSet("priceLevel",new HashSet<>());
    }
}
