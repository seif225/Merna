package com.example.merna;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class GeoSquare {
    private static final String TAG = "GeoSquare";
    private double northWestLat, northEastLat, southEastLat, southWestLat;
    private double northWestLng, northEastLng, southEastLng, southWestLng;
    private LatLng latLng , northWest  , northEast,southEast,southWest;

    public GeoSquare(LatLng latLng) {
        this.latLng = latLng;
        setAll();
    }

    private void setAll() {
        double lat = latLng.latitude;
        double lng = latLng.longitude;
        Log.e(TAG, "setAll: "+ lat + " " + lng +"\n");

        // north west
        northWestLat = lat +  0.0003;
        northWestLng = lng - 0.0003;
         northWest= new LatLng(northWestLat,northWestLng);
        // north East
        northEastLat =lat +  0.0003;
        northEastLng = lng +  0.0003;;
         northEast= new LatLng(northEastLat,northEastLng);

        //south east

        southEastLat =lat-  0.0003;
        southEastLng = lng +  0.0003;;
         southEast= new LatLng(southEastLat,southEastLng);

        // south west
        southWestLat = lat -  0.0003;
        southWestLng =lng - 0.0003;
         southWest= new LatLng(southWestLat,southWestLng);

    }


    public double getNorthWestLat() {
        return northWestLat;
    }

    public LatLng getNorthWest() {
        return northWest;
    }

    public LatLng getNorthEast() {
        return northEast;
    }

    public LatLng getSouthEast() {
        return southEast;
    }

    public LatLng getSouthWest() {
        return southWest;
    }

    public double getNorthEastLat() {
        return northEastLat;
    }

    public double getSouthEastLat() {
        return southEastLat;
    }

    public double getSouthWestLat() {
        return southWestLat;
    }

    public double getNorthWestLng() {
        return northWestLng;
    }

    public double getNorthEastLng() {
        return northEastLng;
    }

    public double getSouthEastLng() {
        return southEastLng;
    }

    public double getSouthWestLng() {
        return southWestLng;
    }
}
