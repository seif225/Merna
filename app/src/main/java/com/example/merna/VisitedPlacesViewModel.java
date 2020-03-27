package com.example.merna;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.merna.ui.FirebaseQueryHelper;

import java.util.ArrayList;

public class VisitedPlacesViewModel extends ViewModel {
    private MutableLiveData<ArrayList<HangOutModel>> listOfHangOuts = new MutableLiveData<>();
    private FirebaseQueryHelper queryHelper = FirebaseQueryHelper.getINSTANCE();

    public MutableLiveData<ArrayList<HangOutModel>> getListOfHangOuts(String uId) {
        return queryHelper.getlistOfHangOuts(listOfHangOuts, uId);

    }
}
