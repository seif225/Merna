package com.example.merna;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.merna.ui.HangOut.HangOutActivity;
import com.example.merna.ui.home.HomeViewModel;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static com.example.merna.App.CHANNEL_ID;

public class LocationService extends Service {
    private static final String TAG = "LocationService";
    private final static long UPDATE_INTERVAL = 5 * 1000;  /* 15 hours  */
    private final static long FASTEST_INTERVAL = 5* 1000; /* 10 hours */
    private static final String CHANNEL_ID_PLACES = "10";
    private FusedLocationProviderClient mFusedLocationClient;
    private boolean[] checks = new boolean[15];
    private int count = 0;
    private GeoSquare square;
    private NotificationManagerCompat notificationManager;
    private GeoSquare houseFence;


    @Override
    public void onCreate() {
        super.onCreate();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        notificationManager = NotificationManagerCompat.from(this);
        createNotificationChannel();
        createNotificationChannelForPlaces();


        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild("houseFence")) {
                                Log.e(TAG, "onDataChange: " + "got house fence in service ");
                                houseFence = dataSnapshot.child("houseFence").getValue(GeoSquare.class);
                                Log.e(TAG, "onDataChange: " + houseFence.getNorthEastLat());
                            } else {
                                Log.e(TAG, "onDataChange: " + "didn't get a house fence");

                                Toast.makeText(LocationService.this, "you need to add your house fence first ^^ ", Toast.LENGTH_SHORT).show();
                                stopSelf();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });




        }


        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Example Service")
                .setContentText("Service is running")
                .setContentIntent(pendingIntent)
                .build();


        startForeground(1, notification);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand: " + " mn bra");
        getLocationUpdates();
        return START_NOT_STICKY;
    }

    boolean isInLocation = false;
    boolean inDaHouse = false;

    private void getLocationUpdates() {

        //---LocationUpdates--


        LocationRequest mLocationRequestHighAccuracy = new LocationRequest();
        mLocationRequestHighAccuracy.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequestHighAccuracy.setInterval(UPDATE_INTERVAL);
        mLocationRequestHighAccuracy.setFastestInterval(FASTEST_INTERVAL);


        mFusedLocationClient.requestLocationUpdates(mLocationRequestHighAccuracy, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {


                        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                            Location location = locationResult.getLastLocation();
                            Log.e(TAG, "onLocationResult: Location: " + location.getLatitude() + " " + location.getLongitude());

                            if (houseFence != null) {
                                if (isInSquare(location, houseFence)) {
                                    Log.e(TAG, "onLocationResult: " + " looks like you are in the house ^^");
                                    if (!inDaHouse) {
                                        notifyUserWelcomeHome();
                                        inDaHouse = true;
                                    }
                                } else {
                                    inDaHouse = false;
                                    if (count == 0) {
                                        Log.e(TAG, "onLocationResult:count should be zero =  " + count);
                                        square = new GeoSquare(new LatLng(location.getLatitude(), location.getLongitude()));
                                    }

                                    if (count >= 15) {
                                        if (isSettled()) {
                                            Log.e(TAG, "onLocationResult: d5lna fe el if bta3t isSettled elhamdullah ");
                                            if (!isInLocation) {
                                                notifyUser(location, LocationService.this);
                                                isInLocation = true;
                                            }
                                        } else {
                                            checks = new boolean[15];
                                            Log.e(TAG, "onLocationResult: " + count + " " + checks[count]);
                                        }
                                        count = 0;
                                    }


                                    boolean temp = isInSquare(location, square);
                                    Log.e(TAG, "inSquare?: " + count + " " + temp);
                                    checks[count] = temp;


                                    Log.e(TAG, "onLocationResult: got location result.");
                                    FirebaseDatabase.getInstance().getReference().child("Users")
                                            .child(FirebaseAuth.getInstance().getUid())
                                            .child("currentLocation").setValue(location);
                                    count++;

                                    if (!checks[count - 1]) {
                                        count = 0;
                                        if (isInLocation) {
                                            isInLocation = false;
                                            NotifyUserIsLeaving();
                                        }
                                    }
                                }
                            } else {
                                Log.e(TAG, "onLocationResult: lel asf HouseFence = " + houseFence);
                            }
                        } else {
                            stopSelf();
                        }
                    }

                }
                , Looper.myLooper()); // Looper.myLooper tells this to repeat forever until thread is destroyed


    }

    private void notifyUserWelcomeHome() {

        //TODO: notify user welcome home


        NotificationCompat.Builder notification = new NotificationCompat.Builder(this, CHANNEL_ID_PLACES)
                .setSmallIcon(R.drawable.flag_egypt)
                .setStyle(new NotificationCompat.BigTextStyle().bigText("Welcome home ! ^^")
                        .setBigContentTitle("safe return ! don't forget to save your memories !")
                )
                .setColor(Color.YELLOW)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        notificationManager.notify(2, notification.build());


    }

    private void NotifyUserIsLeaving() {
        //TODO: notify user to leave a memory when he leaves


        NotificationCompat.Builder notification = new NotificationCompat.Builder(this, CHANNEL_ID_PLACES)
                .setSmallIcon(R.drawable.flag_egypt)
                .setStyle(new NotificationCompat.BigTextStyle().bigText("add your memory ^^")
                        .setBigContentTitle("looks like you're leaving now ! i hope you enjoyed your outing <3")
                )
                .setColor(Color.YELLOW)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        notificationManager.notify(3, notification.build());


    }

    private boolean isSettled() {
        int i = 0;
        for (boolean check : checks) {
            if (!check) {
                return false;
            }
            Log.e(TAG, "isSettled: " + i + " " + check);
            i++;
        }
        return true;
    }

    private void notifyUser(Location location, Context context) {
        Log.e(TAG, "notifyUser: wslna bs lesa msh 3arf el location b nnull wla eh el klam ");
        if (location != null) {
            Log.e(TAG, "notifyUser: WE'RE HEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEREEE !!!! \n \n \n \n !!");

            PlacesClient placesClient = Places.createClient(context);
            // Use fields to define the data types to return.
            List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME
                    , Place.Field.LAT_LNG
                    , Place.Field.ADDRESS
                    , Place.Field.TYPES);
            // Use the builder to create a FindCurrentPlaceRequest.
            FindCurrentPlaceRequest request = FindCurrentPlaceRequest.newInstance(placeFields);
            // Call findCurrentPlace and handle the response (first check that the user has granted permission).
            if (ContextCompat.checkSelfPermission(context, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Task<FindCurrentPlaceResponse> placeResponse = placesClient.findCurrentPlace(request);
                placeResponse.addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        FindCurrentPlaceResponse response = task.getResult();
                        if (response.getPlaceLikelihoods().size() >= 1) {

                            FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                                    .child("currentLocation").setValue(location);

                            Intent i = new Intent(LocationService.this, HangOutActivity.class);
                            Intent[] intents = {i};
                            PendingIntent pendingIntent = PendingIntent.getActivities(LocationService.this, 0, intents, 0);

                            NotificationCompat.Builder notification = new NotificationCompat.Builder(this, CHANNEL_ID_PLACES)
                                    .setSmallIcon(R.drawable.flag_egypt)
                                    .setContentIntent(pendingIntent)
                                    .setStyle(new NotificationCompat.BigTextStyle().bigText("don't forget to save your memories ^^")
                                            .setBigContentTitle("looks like you're hanging out now in " + response.getPlaceLikelihoods().get(0).getPlace().getName() + " ;)")

                                    )
                                    .setColor(Color.YELLOW)
                                    .setPriority(NotificationCompat.PRIORITY_HIGH);

                            notificationManager.notify(4, notification.build());


                            Date currentTime = Calendar.getInstance().getTime();
                            FirebaseDatabase.getInstance().getReference().child("Users").
                                    child(FirebaseAuth.getInstance().getUid()).child("place").setValue(response);
                            FirebaseDatabase.getInstance().getReference().child("Users")
                                    .child(FirebaseAuth.getInstance().getUid()).child("placeTime").setValue(currentTime);


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
    }

    private boolean isInSquare(Location location, GeoSquare square) {
        double lat = location.getLatitude();
        double lng = location.getLongitude();
        Log.e(TAG, "isInSquare: lat" + lat);
        Log.e(TAG, "isInSquare: lng" + lng + "\n");

        Log.e(TAG, "isInSquare: Lat from " + square.getSouthEastLat() + " to " + square.getNorthEastLat());
        Log.e(TAG, "isInSquare: Lng from " + square.getNorthWestLng() + " to " + square.getNorthEastLng() + "\n");

        if (lat < square.getNorthEastLat() && lat > square.getSouthEastLat()) {

            return lng < square.getNorthEastLng() && lng > square.getNorthWestLng();
        }
        return false;
    }


    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Example Service Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }


    private void createNotificationChannelForPlaces() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID_PLACES,
                    "Example Service Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
    @Override
    public void onLowMemory() {
        // TODO Auto-generated method stub
        super.onLowMemory();
        System.gc();
        Intent restartService = new Intent(getApplicationContext(),
                this.getClass());
        restartService.setPackage(getPackageName());
        PendingIntent restartServicePI = PendingIntent.getService(
                getApplicationContext(), 1, restartService,
                PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() +1000, restartServicePI);
      //  this.stopSelf();
    }

}
