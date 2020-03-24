package com.example.merna.ui.home;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.merna.AlbumsRecyclerView;
import com.example.merna.HangOutModel;
import com.example.merna.R;
import com.example.merna.ui.HangOut.HangOutActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import butterknife.BindView;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class HomeFragment extends Fragment {


    private HomeViewModel homeViewModel;
    private FloatingActionButton fab;
    private static final String FINE_LOCATION = ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private AlbumsRecyclerView adapter;

    private RecyclerView homeRecyclerView;
    private ConstraintLayout homeConstraintLayout;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        fab = root.findViewById(R.id.fab);
        homeRecyclerView = root.findViewById(R.id.home_recyclerView);
        homeConstraintLayout = root.findViewById(R.id.home_constraint_layout);
        callData();

        getPermissionAccess();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), HangOutActivity.class);
                startActivity(i);

            }
        });


        return root;
    }

    private void callData() {

        homeViewModel.getListOfHangOuts(FirebaseAuth.getInstance().getUid()).observe(this, new Observer<List<HangOutModel>>() {
            @Override
            public void onChanged(List<HangOutModel> hangOutModels) {
                if (hangOutModels != null) {
                    homeConstraintLayout.setVisibility(View.GONE);
                    adapter = new AlbumsRecyclerView(hangOutModels);
                    homeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    homeRecyclerView.setAdapter(adapter);

                }

            }
        });

    }


    private void getPermissionAccess() {
        String[] permission = {FINE_LOCATION, COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                //   initMap();
            } else {

                ActivityCompat.requestPermissions(getActivity(),
                        permission,
                        LOCATION_PERMISSION_REQUEST_CODE);

            }


        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    permission,
                    LOCATION_PERMISSION_REQUEST_CODE);

        }


    }
}
