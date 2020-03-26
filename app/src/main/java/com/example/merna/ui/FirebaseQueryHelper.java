package com.example.merna.ui;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.merna.HangOutModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FirebaseQueryHelper {

    private static FirebaseQueryHelper INSTANCE;
    private static final DatabaseReference USER_REF = FirebaseDatabase.getInstance().getReference().child("Users");
    private static final String TAG = "FirebaseQueryHelper";
    public static FirebaseQueryHelper getINSTANCE() {
        if (INSTANCE == null) INSTANCE = new FirebaseQueryHelper();
        return INSTANCE;
    }
    public MutableLiveData<ArrayList<HangOutModel>> getlistOfHangOuts(MutableLiveData<ArrayList<HangOutModel>> listOfHangOuts, String uId) {

        if(FirebaseAuth.getInstance().getCurrentUser()!=null) {
            ArrayList<HangOutModel> arrayListOfHangouts = new ArrayList<>();
            USER_REF.child(uId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild("Hangouts")) {
                        HangOutModel temp;
                        for (DataSnapshot d1 : dataSnapshot.child("Hangouts").getChildren()) {
                            /*for (DataSnapshot d2 : d1.getChildren()) {*/
                            Log.e(TAG, "onDataChange: " + d1);
                            temp = new HangOutModel();
                            temp.setAlbumName(d1.child("albumName").getValue().toString());
                            temp.setDate(d1.child("date").getValue().toString());
                            temp.setPlaceName(d1.child("placeName").getValue().toString());
                            ArrayList<String> listOfPics = new ArrayList<>();
                            for (DataSnapshot d3 : d1.child("listOfPics").getChildren()) {
                                listOfPics.add(d3.getValue().toString());
                            }
                            temp.setListOfPics(listOfPics);
                            arrayListOfHangouts.add(temp);
                        }
                        /* }*/
                        listOfHangOuts.setValue(arrayListOfHangouts);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        return listOfHangOuts;
    }
}
