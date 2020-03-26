package com.example.merna.ui.HangOut;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.asksira.bsimagepicker.BSImagePicker;
import com.bumptech.glide.Glide;
import com.example.merna.GeoSquare;
import com.example.merna.LocationService;
import com.example.merna.ParcelableHangOutModel;
import com.example.merna.R;
import com.example.merna.UploadPhotosService;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
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
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class HangOutActivity extends AppCompatActivity implements BSImagePicker.OnSingleImageSelectedListener,
        BSImagePicker.OnMultiImageSelectedListener,
        BSImagePicker.ImageLoaderDelegate,
        BSImagePicker.OnSelectImageCancelledListener, DatePickerDialog.OnDateSetListener
        , OnMapReadyCallback, ResultCallback<Status> {


    private static final String FINE_LOCATION = ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static float DEFAULT_ZOOM = 17f;

    @BindView(R.id.my_current_location)
    CardView myCurrentLocation;
    @BindView(R.id.info)
    CardView info;
    @BindView(R.id.add_home_geoFence)
    CardView addHomeGeoFence;
    @BindView(R.id.blue_arrow)
    ImageView blueArrow;
    @BindView(R.id.album_name_et)
    TextInputEditText albumNameEt;
    @BindView(R.id.album_name_et_container)
    TextInputLayout albumNameEtContainer;
    @BindView(R.id.place_name_tv)
    TextView placeNameTv;
    @BindView(R.id.place_name_et)
    TextView placeNameEt;
    @BindView(R.id.address_tv)
    TextView addressTv;
    @BindView(R.id.address_et)
    TextView addressEt;
    @BindView(R.id.add_hourse_fence)
    Button addHourseFence;
    @BindView(R.id.album_settings_tv)
    TextView albumSettingsTv;
    @BindView(R.id.add_pics)
    FloatingActionButton addPics;
    @BindView(R.id.pick_date)
    FloatingActionButton pickDate;
    @BindView(R.id.pick_location)
    FloatingActionButton pickLocation;

    CircularProgressButton done;
    private boolean mLocationPermission = false;
    private GoogleMap mMap;
    PlacesClient placesClient;
    private CardView myCurrentLocationCardView;
    //widgets
    AutocompleteSupportFragment autocompleteFragment;
    private LatLng myCurrentPosition;
    PlaceLikelihood location;
    private static final String TAG = "HangOutActivity";
    List<Uri> listOfPics;
    FusedLocationProviderClient fusedLocationProviderClient;
    String date;
    MarkerOptions markerOptions = new MarkerOptions();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hang_out);
        ButterKnife.bind(this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        done = (CircularProgressButton) findViewById(R.id.done);
        pickLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPlaces(getBaseContext());
            }
        });


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
                        Toast.makeText(HangOutActivity.this, "you need internet connection to upload the album and also to get the place"
                                , Toast.LENGTH_LONG).show();
                    } else if (date == null) {
                        Toast.makeText(HangOutActivity.this, " you must pick a date", Toast.LENGTH_SHORT).show();
                    } else {
                       // done.startAnimation();

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
                      if(location.getResult()!=null){
                        moveCamera(new LatLng(location.getResult().getLatitude(), location.getResult().getLongitude()), DEFAULT_ZOOM);
                    }
                      else {
                          Toast.makeText(HangOutActivity.this
                                  , "error occurred while attempting to access your location , check your internet connection or permissions"
                                  , Toast.LENGTH_SHORT)
                                  .show();
                      }
                    }
                });


            }
        });
        // mSearchText = findViewById(R.id.input_search);

        autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        autocompleteFragment.setPlaceFields(Arrays.asList(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.LAT_LNG,
                Place.Field.NAME))
                .setCountries("eg")
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


                    location = getMostLikelyPlace(task.getResult().getPlaceLikelihoods());
                    if (location.getPlace().getName() != null)
                        placeNameEt.setText(location.getPlace().getName());
                    if (location.getPlace().getAddress() != null)
                        addressEt.setText(location.getPlace().getAddress());
                    Log.e(TAG, "getPlaces: " + location.getPlace().getAddress());


                    //  moveCamera(location.getPlace().getLatLng(), DEFAULT_ZOOM, location.getPlace().getName());


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

    private PlaceLikelihood getMostLikelyPlace(List<PlaceLikelihood> placeLikelihoods) {
        PlaceLikelihood mostLikely = placeLikelihoods.get(0);

        for (int i = 1; i < placeLikelihoods.size(); i++) {
            if (mostLikely.getLikelihood() < placeLikelihoods.get(i).getLikelihood()) {
                mostLikely = placeLikelihoods.get(i);
            }

        }


        return mostLikely;
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
                placeNameEt.setText(place.getName() + "");
                addressEt.setText(place.getAddress() + "");
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

                            if (currentLocation != null) {
                                Log.e(TAG, "my current location NOWWW " + currentLocation);
                                moveCamera(
                                        new LatLng(currentLocation.getLatitude()
                                                , currentLocation.getLongitude())
                                        , DEFAULT_ZOOM);
                            } else {
                                Toast.makeText(HangOutActivity.this
                                        , "error occurred while attempting to access your location , check your internet connection or permissions"
                                        , Toast.LENGTH_SHORT)
                                        .show();
                            }
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
        markerOptions.position(latLng).title(title);
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

        getPlaces(this);


    }

    private void markPlace(LatLng latLng) {

        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());


        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();
            moveCamera(latLng, DEFAULT_ZOOM, knownName);
        } catch (IOException e) {
            e.printStackTrace();
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
