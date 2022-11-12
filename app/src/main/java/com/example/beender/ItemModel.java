package com.example.beender;

public class ItemModel {
    private int image, image2, attractionID;
    private String nama, usia, kota;

    public ItemModel() {
    }

    public ItemModel(int image, int image2, String nama, String usia, String kota, int id) {
        this.image = image;
        this.image2 = image2;
        this.nama = nama;
        this.usia = usia;
        this.kota = kota;
        this.attractionID = id;
    }

    public int getImage() { return image; }

    public int getImage2() { return image2; }

    public String getNama() {
        return nama;
    }

    public String getUsia() {
        return usia;
    }

    public String getKota() {
        return kota;
    }
}
