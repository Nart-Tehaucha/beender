package com.example.beender.model;

import com.google.maps.model.LatLng;
import com.google.type.DateTime;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class UserTrip {
    private HashMap<String, List<LatLng>> swipedRight;
    private String userEmail;
    private String dateTime;
    private String title;
    private String id;
    private String type; // Journey / Star

    public UserTrip() {};

    public UserTrip(HashMap<String, List<LatLng>> swipedRight, String userEmail, String dateTime, String title, String id, String type) {
        this.swipedRight = swipedRight;
        this.userEmail = userEmail;
        this.dateTime = dateTime;
        this.title = title;
        this.id = id;
        this.type = type;
    }

    public HashMap<String, List<LatLng>> getSwipedRight() {
        return swipedRight;
    }

    public void setSwipedRight(HashMap<String, List<LatLng>> swipedRight) {
        this.swipedRight = swipedRight;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserTrip userTrip = (UserTrip) o;
        return id.equals(userTrip.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
