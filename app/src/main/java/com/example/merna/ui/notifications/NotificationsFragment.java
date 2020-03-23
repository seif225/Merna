package com.example.merna.ui.notifications;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.merna.LoginActivity;
import com.example.merna.R;
import com.google.firebase.auth.FirebaseAuth;

public class NotificationsFragment extends Fragment {

    private NotificationsViewModel notificationsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                ViewModelProviders.of(this).get(NotificationsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);
        final Button logOutButton = root.findViewById(R.id.log_out_btn);
        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logUserOutAndSendHimToLogin();
            }
        });
        return root;
    }

    private void logUserOutAndSendHimToLogin() {
        FirebaseAuth.getInstance().signOut();
        sendUserToLogin();

    }

    private void sendUserToLogin() {
    Intent i = new Intent(getActivity() , LoginActivity.class);
    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
    startActivity(i);

    }
}
