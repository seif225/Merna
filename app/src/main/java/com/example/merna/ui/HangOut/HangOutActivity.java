package com.example.merna.ui.HangOut;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.asksira.bsimagepicker.BSImagePicker;
import com.bumptech.glide.Glide;
import com.example.merna.GeoSquare;
import com.example.merna.ParcelableHangOutModel;
import com.example.merna.LocationService;
import com.example.merna.R;
import com.example.merna.UploadPhotosService;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class HangOutActivity extends AppCompatActivity implements BSImagePicker.OnSingleImageSelectedListener,
        BSImagePicker.OnMultiImageSelectedListener,
        BSImagePicker.ImageLoaderDelegate,
        BSImagePicker.OnSelectImageCancelledListener, DatePickerDialog.OnDateSetListener
        , OnMapReadyCallback, ResultCallback<Status> {


    @BindView(R.id.pick_date)
    Button pickDate;
    private static final String TAG = "HangOutActivity";
    List<Uri> listOfPics;
    FusedLocationProviderClient fusedLocationProviderClient;
    @BindView(R.id.album_name_tv)
    TextView albumNameTv;
    @BindView(R.id.album_name_et)
    TextInputEditText albumNameEt;
    @BindView(R.id.album_name_et_container)
    TextInputLayout albumNameEtContainer;
    @BindView(R.id.place_name_tv)
    TextView placeNameTv;
    @BindView(R.id.place_name_et)
    EditText placeNameEt;
    @BindView(R.id.address_tv)
    TextView addressTv;
    @BindView(R.id.address_et)
    EditText addressEt;
    @BindView(R.id.add_pics)
    Button addPics;
    @BindView(R.id.pick_location)
    Button pickLocation;
    @BindView(R.id.done)
    Button done;
    @BindView(R.id.add_hourse_fence)
    Button addHourseFence;
    String date;


    private static final String FINE_LOCATION = ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static float DEFAULT_ZOOM = 17f;
    private boolean mLocationPermission = false;
    private GoogleMap mMap;
    PlacesClient placesClient;
    private CardView myCurrentLocationCardView;
    //widgets
    AutocompleteSupportFragment autocompleteFragment;
    private LatLng myCurrentPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hang_out);
        ButterKnife.bind(this);
        getPlaces(this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);


        pickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });


        addHourseFence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Task<Location> task = fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            GeoSquare geoSquare = new GeoSquare(new LatLng(task.getResult().getLatitude(), task.getResult().getLongitude()));
                            FirebaseDatabase.getInstance().getReference().child("Users").
                                    child(FirebaseAuth.getInstance().getUid()).child("houseFence").setValue(geoSquare);
                            Toast.makeText(HangOutActivity.this, "house fence has been added successfully", Toast.LENGTH_SHORT).show();
                            Intent serviceIntent = new Intent(HangOutActivity.this, LocationService.class);
                            ContextCompat.startForegroundService(HangOutActivity.this, serviceIntent);
                        }
                    }
                });

            }
        });


        addPics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BSImagePicker multiSelectionPicker = new BSImagePicker.Builder("com.yourdomain.yourpackage.fileprovider")
                        .isMultiSelect() //Set this if you want to use multi selection mode.
                        .setMinimumMultiSelectCount(1) //Default: 1.
                        .setMaximumMultiSelectCount(100) //Default: Integer.MAX_VALUE (i.e. User can select as many images as he/she wants)
                        .setMultiSelectBarBgColor(android.R.color.white) //Default: #FFFFFF. You can also set it to a translucent color.
                        .setMultiSelectTextColor(R.color.primary_text) //Default: #212121(Dark grey). This is the message in the multi-select bottom bar.
                        .setMultiSelectDoneTextColor(R.color.colorAccent) //Default: #388e3c(Green). This is the color of the "Done" TextView.
                        .setOverSelectTextColor(R.color.error_text) //Default: #b71c1c. This is the color of the message shown when user tries to select more than maximum select count.
                        .disableOverSelectionMessage() //You can also decide not to show this over select message.
                        .build();
                if (listOfPics != null) {
                    HangOutActivity.this.onMultiImageSelected(listOfPics, "");
                }
                multiSelectionPicker.show(getSupportFragmentManager(), "picker");
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (listOfPics != null) {
                    if (albumNameEt.getText().toString().isEmpty()) {
                        albumNameEtContainer.requestFocus();
                        albumNameEtContainer.setError("you must insert an album name");
                    } else if (placeNameEt.getText().toString().isEmpty()) {
                        placeNameEt.requestFocus();
                        placeNameEt.setError("you must insert an place name");
                    } else if (date == null) {
                        Toast.makeText(HangOutActivity.this, " you must pick a date", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(HangOutActivity.this, "tmam", Toast.LENGTH_SHORT).show();
                        long unixTime = System.currentTimeMillis();
                        String registeredName = UUID.randomUUID().toString();
                        Log.e(TAG, "onClick: " + registeredName);

                        ParcelableHangOutModel model = new ParcelableHangOutModel(
                                albumNameEt.getText().toString(), addressEt.getText().toString(), placeNameEt.getText().toString(),
                                listOfPics, date, unixTime, registeredName);
                        Intent intent = new Intent(getBaseContext(), UploadPhotosService.class);
                        intent.putExtra("parcel", model);
                        ContextCompat.startForegroundService(getBaseContext(), intent);
                        finish();
                    }


                } else {
                    Toast.makeText(HangOutActivity.this, "msh tmam", Toast.LENGTH_SHORT).show();

                }

            }
        });


        placesClient = Places.createClient(this);

        getCurrentLocation();


        myCurrentLocationCardView = findViewById(R.id.my_current_location);
        myCurrentLocationCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Task<Location> location = fusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        moveCamera(new LatLng(location.getResult().getLatitude(), location.getResult().getLongitude()), DEFAULT_ZOOM);

                    }
                });


            }
        });
        // mSearchText = findViewById(R.id.input_search);

        autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.NAME)).setCountries("eg")
                .setHint("search place , city or zipcode");
        getPermissionAccess();


    }


    @Override
    public void loadImage(Uri imageUri, ImageView ivImage) {
        Glide.with(HangOutActivity.this).load(imageUri).into(ivImage);
    }

    @Override
    public void onMultiImageSelected(List<Uri> uriList, String tag) {

        listOfPics = uriList;
    }

    @Override
    public void onCancelled(boolean isMultiSelecting, String tag) {

    }

    @Override
    public void onSingleImageSelected(Uri uri, String tag) {

    }

    void getPlaces(Context context) {

        PlacesClient placesClient = Places.createClient(context);
        // Use fields to define the data types to return.
        List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME
                , Place.Field.LAT_LNG
                , Place.Field.ADDRESS
                , Place.Field.NAME
                , Place.Field.TYPES);
        // Use the builder to create a FindCurrentPlaceRequest.
        FindCurrentPlaceRequest request = FindCurrentPlaceRequest.newInstance(placeFields);
        // Call findCurrentPlace and handle the response (first check that the user has granted permission).
        if (ContextCompat.checkSelfPermission(context, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Task<FindCurrentPlaceResponse> placeResponse = placesClient.findCurrentPlace(request);
            placeResponse.addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    PlaceLikelihood location = task.getResult().getPlaceLikelihoods().get(0);
                    if (location.getPlace().getName() != null)
                        placeNameEt.setText(location.getPlace().getName());
                    if (location.getPlace().getAddress() != null)
                        addressEt.setText(location.getPlace().getAddress());
                    Log.e(TAG, "getPlaces: " + location.getPlace().getAddress());
                } else {
                    Exception exception = task.getException();
                    if (exception instanceof ApiException) {
                        ApiException apiException = (ApiException) exception;
                        Log.e(TAG, "Place not found: " + apiException.getStatusCode());
                    }
                }
            });
        } else {
            // A local method to request required permissions;
            // See https://developer.android.com/training/permissions/requesting
        }

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        date = dayOfMonth + "/" + month + "/" + year;
        Log.e(TAG, "onDateSet: " + date);
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this, this, Calendar.getInstance().get(Calendar.YEAR)
                , Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();

    }


    private void getCurrentLocation() {

        // Use fields to define the data types to return.
        List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME
                , Place.Field.LAT_LNG
                , Place.Field.ADDRESS
                , Place.Field.TYPES);
        // Use the builder to create a FindCurrentPlaceRequest.
        FindCurrentPlaceRequest request = FindCurrentPlaceRequest.newInstance(placeFields);
        // Call findCurrentPlace and handle the response (first check that the user has granted permission).
        if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Task<FindCurrentPlaceResponse> placeResponse = placesClient.findCurrentPlace(request);
            placeResponse.addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    StringBuilder sb = new StringBuilder();
                    FindCurrentPlaceResponse response = task.getResult();
                    double sum = 0;
                    for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
                        Log.e(TAG, String.format("Place '%s' has likelihood: %f",
                                placeLikelihood.getPlace().getName(),
                                placeLikelihood.getLikelihood()));
                        sb.append(placeLikelihood.getPlace() + " " + placeLikelihood.getLikelihood() + "\n \n");
                        Log.e(TAG, "getCurrentLocation: " + placeLikelihood.getPlace().getLatLng());
                        sum = sum + placeLikelihood.getLikelihood();
                        Log.e(TAG, "getCurrentLocation: " + sum);
                    }

                } else {
                    Exception exception = task.getException();
                    if (exception instanceof ApiException) {
                        ApiException apiException = (ApiException) exception;
                        Log.e(TAG, "Place not found: " + apiException.getStatusCode());
                    }
                }
            });
        } else {

            // A local method to request required permissions;
            // See https://developer.android.com/training/permissions/requesting
        }

    }

    private void init() {

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                // TODO: Get info about the selected place.
                Log.e(TAG, "Place: " + place.getName() + ", " + place.getId());
                geoLocate(place);
            }

            @Override
            public void onError(@NonNull Status status) {
                // TODO: Handle the error.
                Log.e(TAG, "An error occurred: " + status);
            }
        });

        hideSoftKeyboard();

    }

    private void geoLocate(Place place) {


        Log.e(TAG, "geoLocate: " + place.getLatLng());
        moveCamera(place.getLatLng(), DEFAULT_ZOOM, place.getName());
        hideSoftKeyboard();

    }

    private void getDeviceLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if (mLocationPermission) {
                final Task location = fusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Location currentLocation = (Location) task.getResult();
                            Log.e(TAG, "my current location NOWWW " + currentLocation);

                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM);
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: " + e.getMessage());
        }


    }

    public void moveCamera(LatLng latLng, float zoom, String title) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(title);
        mMap.addMarker(markerOptions);
        hideSoftKeyboard();

    }

    public void moveCamera(LatLng latLng, float zoom) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        hideSoftKeyboard();

    }


    private void getPermissionAccess() {
        String[] permission = {FINE_LOCATION, COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermission = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this,
                        permission,
                        LOCATION_PERMISSION_REQUEST_CODE);

            }


        } else {
            ActivityCompat.requestPermissions(this,
                    permission,
                    LOCATION_PERMISSION_REQUEST_CODE);

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermission = false;
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermission = false;
                            return;
                        }

                    }

                    mLocationPermission = true;
                    initMap();

                }
        }


    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.e(TAG, "onMapReady: map is ready");
        mMap = googleMap;
        Toast.makeText(this, "Map Is Ready", Toast.LENGTH_SHORT).show();

        if (mLocationPermission) {
            getDeviceLocation();
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);

            init();


        }

    }

    private void hideSoftKeyboard() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }

    @Override
    public void onResult(@NonNull Status status) {
        drawGeoFence();
    }

    Circle geoFenceLimits;

    private void drawGeoFence() {
        if (geoFenceLimits != null) {
            geoFenceLimits.remove();
        }

        Location location = fusedLocationProviderClient.getLastLocation().getResult();
        CircleOptions circleOptions = new CircleOptions()
                .center(new LatLng(location.getLatitude(), location.getLongitude()))
                .strokeColor(Color.argb(50, 70, 70, 70))
                .fillColor(Color.argb(100, 150, 150, 150))
                .radius(400f);


        mMap.addCircle(circleOptions);

    }


}
