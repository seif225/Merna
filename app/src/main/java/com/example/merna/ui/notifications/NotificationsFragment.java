package com.example.merna.ui.notifications;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.merna.GeoSquare;
import com.example.merna.LocationService;
import com.example.merna.LoginActivity;
import com.example.merna.R;
import com.example.merna.VisitedPlacesActivity;
import com.example.merna.ui.HangOut.HangOutActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationsFragment extends Fragment {


    TextView userNameTv;
    ImageView imageView;
    LinearLayout houseFenceLinearLayout;
    LinearLayout visitedPlacesLinearLayout;
    LinearLayout logOutLinearLayout;
    NotificationsViewModel notificationsViewModel;
    FusedLocationProviderClient fusedLocationProviderClient;
    CircleImageView userImageView;
    FloatingActionButton uploadImageFab;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                ViewModelProviders.of(this).get(NotificationsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);



        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        userNameTv = root.findViewById(R.id.user_name_tv);
        userImageView = root.findViewById(R.id.user_profile_picture);
        uploadImageFab = root.findViewById(R.id.upload_picture_fab);
        imageView = root.findViewById(R.id.imageView);
        houseFenceLinearLayout = root.findViewById(R.id.house_fence_linear_layout);
        visitedPlacesLinearLayout = root.findViewById(R.id.visited_places_linear_layout);
        logOutLinearLayout = root.findViewById(R.id.log_out_linearLayout);

        notificationsViewModel.getUserPicture(FirebaseAuth.getInstance().getUid()).observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if(s!=null ) Picasso.get().load(s).into(userImageView);
            }
        });


        uploadImageFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CropImage.activity().start(getActivity());


            }
        });


        logOutLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logUserOutAndSendHimToLogin();
            }
        });

        houseFenceLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addHouseFence();
            }
        });

        visitedPlacesLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity() , VisitedPlacesActivity.class);
                startActivity(i);
            }
        });

        notificationsViewModel.getUserName(FirebaseAuth.getInstance().getUid()).observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if(s!=null) userNameTv.setText(s);
            }
        });




        return root;
    }

    private void addHouseFence() {
        Task<Location> task = fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    GeoSquare geoSquare = new GeoSquare(new LatLng(task.getResult().getLatitude(), task.getResult().getLongitude()));
                    FirebaseDatabase.getInstance().getReference().child("Users").
                            child(FirebaseAuth.getInstance().getUid()).child("houseFence").setValue(geoSquare);
                    Toast.makeText(getActivity(), "house fence has been added successfully", Toast.LENGTH_SHORT).show();
                    Intent serviceIntent = new Intent(getActivity(), LocationService.class);
                    ContextCompat.startForegroundService(getActivity(), serviceIntent);
                }
            }
        });
    }

    private void logUserOutAndSendHimToLogin() {
        FirebaseAuth.getInstance().signOut();
        sendUserToLogin();

    }

    private void sendUserToLogin() {
        Intent i = new Intent(getActivity(), LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);

    }
}
