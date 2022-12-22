package com.example.beender;

import java.util.Date;

public class TravelMap {

    public static Integer idCounter = 0;

    private Integer id;
    private Integer imageurl;
    private String name;
    private String description;
    private Date date;



    public TravelMap(Integer id, Integer imageurl, String name, String description, Date date) {
        this.id = id;
        this.imageurl = imageurl;
        this.name = name;
        this.description = description;
        this.date = date;
    }

    public static Integer getIdCounter() {
        return idCounter;
    }

    public Integer getId() {
        return id;
    }

    public Integer getImageurl() {
        return imageurl;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public static void setIdCounter(Integer idCounter) {
        TravelMap.idCounter = idCounter;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setImageurl(Integer imageurl) {
        this.imageurl = imageurl;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TravelMap travelMap = (TravelMap) o;
        return id.equals(travelMap.id);
    }

}
