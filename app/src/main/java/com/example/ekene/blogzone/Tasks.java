package com.example.ekene.blogzone;

public class Tasks {

    private int id;
    private String title,desc,timestamp;
    byte[] image;
    private double lat,lng;

    public Tasks(int id, String title, String desc, String timestamp, byte[] image, double lat, double lng) {
        this.id = id;
        this.title = title;
        this.desc = desc;
        this.timestamp = timestamp;
        this.image = image;
        this.lat = lat;
        this.lng = lng;
    }

    public Tasks(){

    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public byte[] getImage() {
        return image;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }
}
