package com.example.merna;

import android.net.Uri;

import androidx.lifecycle.ViewModel;

import com.example.merna.ui.FirebaseQueryHelper;

public class MainViewModel extends ViewModel {
    FirebaseQueryHelper  firebaseQueryHelper = FirebaseQueryHelper.getINSTANCE();

    public void uploadUserImage(String userId, Uri resultUri) {
        firebaseQueryHelper.uploadUserImage(userId,resultUri);
    }
}
