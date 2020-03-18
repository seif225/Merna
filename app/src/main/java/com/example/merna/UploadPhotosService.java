package com.example.merna;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import static com.example.merna.App.CHANNEL_ID;

public class UploadPhotosService extends Service {
    private static final String TAG = "UploadPhotosService";
    public static final String CHANNEL_ID_1 = "16";
    int sucCount = 0;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private NotificationManagerCompat notificationManager;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID_1)
                .setContentTitle("Example Service")
                .setContentText("Service is running")
                .setContentIntent(pendingIntent)
                .build();
        notificationManager = NotificationManagerCompat.from(this);


        startForeground(2, notification);
    }


    public void uploadParcelable(HangOutModel model) {
        final int progressMax = model.getListOfPics().size();

        final NotificationCompat.Builder notification = new NotificationCompat.Builder(this, CHANNEL_ID_1)
                .setSmallIcon(R.drawable.flag_egypt)
                .setContentTitle("Upload")
                .setContentText("Upload in progress")
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setProgress(progressMax, 0, false);

        notificationManager.notify(2, notification.build());

        /*new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(2000);
                for (int progress = 0; progress <= progressMax; progress += 20) {
                    notification.setProgress(progressMax, progress, false);
                    notificationManager.notify(2, notification.build());
                    SystemClock.sleep(1000);
                }
                stopSelf();
                notification.setContentText("Upload finished")
                        .setProgress(0, 0, false)
                        .setOngoing(false);
                notificationManager.notify(2, notification.build());
            }
        }).start();*/
        Log.e(TAG, "uploadParcelable:size " + progressMax);
        for (int i = 0; i < model.getListOfPics().size(); i++) {
            Log.e(TAG, "uploadParcelable:i " + i);
            FirebaseStorage.getInstance().getReference().child(i + "").putFile(model.getListOfPics().get(i)).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        sucCount++;
                        Log.e(TAG, "onComplete:succount  " + sucCount);
                        notification.setProgress(progressMax, sucCount, false);
                        notification.setContentText("Upload in progress " + "(" + sucCount + "/" + model.getListOfPics().size() + ").");
                        notificationManager.notify(2, notification.build());
                        if (sucCount == progressMax) {
                            stopSelf();
                            notification.setContentText("Upload finished")
                                    .setProgress(0, 0, false)
                                    .setOngoing(false);
                            notificationManager.notify(2, notification.build());
                        }
                    }
                }
            });


        }


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        HangOutModel model = intent.getParcelableExtra("parcel");
        Log.e(TAG, "onStartCommand: " + model.getPlaceName());
        uploadParcelable(model);

        return START_NOT_STICKY;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID_1,
                    "Upload pic",
                    NotificationManager.IMPORTANCE_HIGH
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
}
