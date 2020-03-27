package com.example.merna;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;


import java.util.List;

public class ParcelableHangOutModel implements Parcelable {

    private String AlbumName, address, placeName;
    private List<Uri> listOfPics;
    private String date;
    private long unixTime;
    private String registeredName;
    private String latitude , longitude;

    public ParcelableHangOutModel(String albumName, String address, String placeName, List<Uri> listOfPics, String date
            , long unixTime, String registeredName, String latitude, String longitude) {
        AlbumName = albumName;
        this.address = address;
        this.placeName = placeName;
        this.listOfPics = listOfPics;
        this.date = date;
        this.unixTime = unixTime;
        this.registeredName = registeredName;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getDate() {
        return date;
    }

    public List<Uri> getListOfPics() {
        return listOfPics;
    }

    public String getAddress() {
        return address;
    }

    public long getUnixTime() {
        return unixTime;
    }

    public String getRegisteredName() {
        return registeredName;
    }

    public String getAlbumName() {
        return AlbumName;
    }

    public String getPlaceName() {
        return placeName;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setListOfPics(List<Uri> listOfPics) {
        this.listOfPics = listOfPics;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setAlbumName(String albumName) {
        AlbumName = albumName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setUnixTime(long unixTime) {
        this.unixTime = unixTime;
    }

    public void setRegisteredName(String registeredName) {
        this.registeredName = registeredName;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }


    protected ParcelableHangOutModel(Parcel in) {
        AlbumName = in.readString();
        address = in.readString();
        placeName = in.readString();
        listOfPics = in.createTypedArrayList(Uri.CREATOR);
        date = in.readString();
        unixTime = in.readLong();
        registeredName = in.readString();
        latitude = in.readString();
        longitude = in.readString();
    }

    public static final Creator<ParcelableHangOutModel> CREATOR = new Creator<ParcelableHangOutModel>() {
        @Override
        public ParcelableHangOutModel createFromParcel(Parcel in) {
            return new ParcelableHangOutModel(in);
        }

        @Override
        public ParcelableHangOutModel[] newArray(int size) {
            return new ParcelableHangOutModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(AlbumName);
        dest.writeString(address);
        dest.writeString(placeName);
        dest.writeTypedList(listOfPics);
        dest.writeString(date);
        dest.writeLong(unixTime);
        dest.writeString(registeredName);
        dest.writeString(latitude);
        dest.writeString(longitude);
    }
}
