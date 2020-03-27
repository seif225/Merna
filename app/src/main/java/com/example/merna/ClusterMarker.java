package com.example.merna;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class ClusterMarker implements ClusterItem {

     LatLng position;
     String snippet , title;
     String iconPicture;
     HangOutModel hangOutModel;
    private static final String TAG = "ClusterMarker";

     ClusterMarker(LatLng position, String snippet, String title, String iconPicture, HangOutModel hangOutModel) {
        this.position = position;
        this.snippet = snippet;
        this.title = title;
        this.iconPicture = iconPicture;
        this.hangOutModel = hangOutModel;
         Log.e(TAG, "ClusterMarker: " + "now you have an object of cluster maker" );
    }


    public HangOutModel getHangOutModel() {
        return hangOutModel;
    }

     String getIconPicture() {
        return iconPicture;
    }

    @Override
    public LatLng getPosition() {
        return position;
    }

    @Override
    public String getSnippet() {
        return snippet;
    }

    public static String getTAG() {
        return TAG;
    }

    @Override
    public String getTitle() {
        return title;
    }
}
