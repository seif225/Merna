package com.example.merna;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.List;

public class VisitedPlacesActivity extends AppCompatActivity implements OnMapReadyCallback, ResultCallback<Status> {

    private VisitedPlacesViewModel viewModel;
    private static final String TAG = "VisitedPlacesActivity";
    private GoogleMap mMap;
    private ClusterManager<ClusterMarker> mClusterManager;
    private MyClusterMarkerRenderer mClusterManagerRenderer;
    private List<ClusterItem> mClusterMarkers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visited_places);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        viewModel = ViewModelProviders.of(this).get(VisitedPlacesViewModel.class);


    }

    @Override
    public void onResult(@NonNull Status status) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        boolean success = googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(VisitedPlacesActivity.this, R.raw.map_style));
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);

        FusedLocationProviderClient mFusedLocation = LocationServices.getFusedLocationProviderClient(this);
        Task<Location> location = mFusedLocation.getLastLocation();
        location.addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {

                moveCamera(new LatLng(task.getResult().getLatitude(), task.getResult().getLongitude()), 7f);


            }
        });
        viewModel.getListOfHangOuts(FirebaseAuth.getInstance().getUid()).observe(this, new Observer<ArrayList<HangOutModel>>() {
            @Override
            public void onChanged(ArrayList<HangOutModel> hangOutModels) {

                if (hangOutModels != null)
                    addMapMarkers(hangOutModels);


            }
        });

    }

    public void moveCamera(LatLng latLng, float zoom) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    private void addMapMarkers(ArrayList<HangOutModel> hangOutModels) {

        if (mMap != null) {

            if (mClusterManager == null) {
                mClusterManager = new ClusterManager<ClusterMarker>(this, mMap);
            }
            if (mClusterManagerRenderer == null) {
                mClusterManagerRenderer = new MyClusterMarkerRenderer(
                        this,
                        mMap,
                        mClusterManager
                );
                mClusterManager.setRenderer(mClusterManagerRenderer);
            }

            for (HangOutModel model : hangOutModels) {

                if (model.getLatitude() != null && model.getLongitude() != null) {
                    Log.d(TAG, "addMapMarkers: location: " + model.getLatitude().toString());
                     try {
                    String snippet = "snippt";


                        /*try {
                            avatar = Integer.parseInt(model.getListOfPics().get(0));
                        } catch (NumberFormatException e) {
                            Log.d(TAG, "addMapMarkers: no avatar for " + model.getAlbumName() + ", setting default.");
                        }*/

                    Log.e(TAG, "addMapMarkers: String lat" + model.getLatitude());
                    Log.e(TAG, "addMapMarkers: double lang" + Double.parseDouble(model.getLongitude()));

                    double lat = Double.parseDouble(model.getLatitude());
                    double lng = Double.parseDouble(model.getLongitude());
                    LatLng latlng = new LatLng(lat, lng);
                     //mMap.addMarker(new MarkerOptions().position(new LatLng(lat,lng)));
                    ClusterMarker newClusterMarker = new ClusterMarker(
                            latlng,
                            model.getDate(),
                            model.getAlbumName() + " ",
                            model.getListOfPics().get(0),
                            model
                    );


                    Log.e(TAG, "addMapMarkers:  i have a cluster marker :D "  +newClusterMarker +"\n \n \n  \n" );
                         //eldonia btboz hena ===>

                         mClusterManager.addItem(newClusterMarker);
                         Log.e(TAG, "addMapMarkers: added item \n \n \n  \n" );

                         mClusterMarkers.add(newClusterMarker);

                         Log.e(TAG, "addMapMarkers: added marker to manager \n \n \n  \n" );

                         Log.e(TAG, "addMapMarkers:  i have a cluster marker :D "  +newClusterMarker +"\n \n \n  \n" );



                     }
                     catch (NullPointerException e) {
                        Log.e(TAG, "addMapMarkers: NullPointerException: " + e.getMessage());
                    }

                }
                mClusterManager.cluster();

            }
        }
    }


}
