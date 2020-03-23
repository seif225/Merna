package com.example.merna;

import android.net.Uri;

import java.util.List;

public class HangOutModel {
    private String AlbumName, address, placeName;
    private List<String> listOfPics;
    private String date;
    private long unixTime;
    private String registeredName;

    public void setRegisteredName(String registeredName) {
        this.registeredName = registeredName;
    }

    public void setUnixTime(long unixTime) {
        this.unixTime = unixTime;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public void setListOfPics(List<String> listOfPics) {
        this.listOfPics = listOfPics;
    }

    public void setAlbumName(String albumName) {
        AlbumName = albumName;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPlaceName() {
        return placeName;
    }

    public String getAlbumName() {
        return AlbumName;
    }

    public String getRegisteredName() {
        return registeredName;
    }

    public long getUnixTime() {
        return unixTime;
    }

    public String getAddress() {
        return address;
    }

    public List<String> getListOfPics() {
        return listOfPics;
    }

    public String getDate() {
        return date;
    }

}
