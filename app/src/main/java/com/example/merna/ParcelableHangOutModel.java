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


    protected ParcelableHangOutModel(Parcel in) {
        AlbumName = in.readString();
        address = in.readString();
        placeName = in.readString();
        listOfPics = in.createTypedArrayList(Uri.CREATOR);
        date = in.readString();
        unixTime = in.readLong();
        registeredName = in.readString();
    }

    public ParcelableHangOutModel(String albumName, String address, String placeName, List<Uri> listOfPics, String date, long unixTime, String registeredName) {
        AlbumName = albumName;
        this.address = address;
        this.placeName = placeName;
        this.listOfPics = listOfPics;
        this.date = date;
        this.unixTime = unixTime;
        this.registeredName = registeredName;
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
    }

    @Override
    public int describeContents() {
        return 0;
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

    public String getAlbumName() {
        return AlbumName;
    }

    public String getAddress() {
        return address;
    }

    public String getPlaceName() {
        return placeName;
    }

    public List<Uri> getListOfPics() {
        return listOfPics;
    }

    public String getDate() {
        return date;
    }

    public long getUnixTime() {
        return unixTime;
    }

    public String getRegisteredName() {
        return registeredName;
    }

    public static Creator<ParcelableHangOutModel> getCREATOR() {
        return CREATOR;
    }
}
