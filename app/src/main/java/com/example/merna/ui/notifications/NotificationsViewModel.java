package com.example.merna.ui.notifications;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.merna.ui.FirebaseQueryHelper;

public class NotificationsViewModel extends ViewModel {

    FirebaseQueryHelper firebaseQueryHelper = FirebaseQueryHelper.getINSTANCE();
    MutableLiveData<String> userName = new MutableLiveData<>();
    MutableLiveData<String> userPicture = new MutableLiveData<>();

    public NotificationsViewModel() {

    }

    public MutableLiveData<String> getUserName(String id ) {
        return firebaseQueryHelper.getUserName(id , userName);
    }

    public MutableLiveData<String> getUserPicture(String id) {
        return firebaseQueryHelper.getUserPicture(id , userPicture);
    }
}