package com.example.merna;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class HangOutModel implements Parcelable {

    private String AlbumName , address , placeName;
    private List<Uri> listOfPics;

    public HangOutModel(Parcel in) {
        AlbumName = in.readString();
        address = in.readString();
        placeName = in.readString();
        listOfPics = in.createTypedArrayList(Uri.CREATOR);
    }



    public HangOutModel(String albumName, String placeName, List<Uri> listOfPics) {
        AlbumName = albumName;
        this.placeName = placeName;
        this.listOfPics = listOfPics;
    }

    public static final Creator<HangOutModel> CREATOR = new Creator<HangOutModel>() {
        @Override
        public HangOutModel createFromParcel(Parcel in) {
            return new HangOutModel(in);
        }

        @Override
        public HangOutModel[] newArray(int size) {
            return new HangOutModel[size];
        }
    };

    public void setAddress(String address) {
        this.address = address;
    }

    public void setAlbumName(String albumName) {
        AlbumName = albumName;
    }

    public void setListOfPics(List<Uri> listOfPics) {
        this.listOfPics = listOfPics;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public List<Uri> getListOfPics() {
        return listOfPics;
    }

    public String getAddress() {
        return address;
    }

    public String getAlbumName() {
        return AlbumName;
    }

    public String getPlaceName() {
        return placeName;
    }


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
    }
}
