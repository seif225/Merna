package com.example.merna.ui;

import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.merna.HangOutModel;
import com.example.merna.UploadPhotosService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FirebaseQueryHelper {

    private static FirebaseQueryHelper INSTANCE;
    private static final DatabaseReference USER_REF = FirebaseDatabase.getInstance().getReference().child("Users");
    private static final String TAG = "FirebaseQueryHelper";

    public static FirebaseQueryHelper getINSTANCE() {
        if (INSTANCE == null) INSTANCE = new FirebaseQueryHelper();
        return INSTANCE;
    }

    public MutableLiveData<ArrayList<HangOutModel>> getlistOfHangOuts(MutableLiveData<ArrayList<HangOutModel>> listOfHangOuts, String uId) {

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
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

                            temp.setUnixTime(Long.parseLong(d1.child("unixTime").getValue().toString()));

                            if (d1.hasChild("latitude"))
                                temp.setLatitude(d1.child("latitude").getValue().toString());
                            if (d1.hasChild("longitude"))
                                temp.setLongitude(d1.child("longitude").getValue().toString());


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

    public MutableLiveData<String> getUserName(String id, MutableLiveData<String> userName) {

        FirebaseDatabase.getInstance().getReference().child("Users").child(id).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userName.setValue(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        return userName;
    }


    public MutableLiveData<String> getUserPicture(String id, MutableLiveData<String> userPicture) {

        FirebaseDatabase.getInstance().getReference().child("Users").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild("profilePicture")) {
                    userPicture.setValue(dataSnapshot.child("profilePicture").getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return userPicture;
    }

    public void uploadUserImage(String userId, Uri resultUri) {


        FirebaseStorage.getInstance().getReference().child(userId)
                .child(userId).putFile(resultUri)
                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                    }

                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                FirebaseStorage.getInstance().getReference()
                        .child(userId)
                        .child(userId).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {


                        Log.e(TAG, "onSuccess: " + " de el log elly bt3ml upload :)");
                        FirebaseDatabase.getInstance().getReference().child("Users")
                                .child(userId).child("profilePicture").setValue(uri+"");



                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });


    }
}
