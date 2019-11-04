package com.example.ekene.blogzone;


public class Lists {

    private int id;
    private String title,desc;
    private byte[] image;
    private boolean hasLocation, hasTimestamp;

    public Lists(int id, String title, String desc, byte[] image, boolean hasLocation, boolean hasTimestamp) {
        this.id = id;
        this.title = title;
        this.desc = desc;
        this.image = image;
        this.hasLocation = hasLocation;
        this.hasTimestamp = hasTimestamp;
    }

    public Lists() {
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDesc(String desc) {this.desc = desc;}

    public void setId(int id) {this.id = id;}

    public void setHasLocation(boolean hasLocation) {this.hasLocation = hasLocation;}

    public void setHasTimestamp(boolean hasTimestamp) {this.hasTimestamp = hasTimestamp;}

    public int getId() {return id;}

    public String getDesc() {return desc;}

    public byte[] getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }

    public boolean isHasLocation() {return hasLocation;}

    public boolean isHasTimestamp() {return hasTimestamp;}

}
