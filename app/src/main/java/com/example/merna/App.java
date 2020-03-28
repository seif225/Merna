package com.example.merna;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.google.android.libraries.places.api.Places;
import com.google.firebase.FirebaseApp;

import leakcanary.AppWatcher;
import leakcanary.LeakCanary;
import shark.Leak;

public class App extends Application {

    public static final String CHANNEL_ID = "exampleServiceChannel";

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(getApplicationContext());
       /* if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);
*/
        AppWatcher.Config config = AppWatcher.getConfig().newBuilder()
                .watchFragmentViews(false)
                .build();
        AppWatcher.setConfig(config);

    }


}
