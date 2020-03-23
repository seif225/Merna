package com.example.merna;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.UUID;

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


    public void uploadParcelable(ParcelableHangOutModel model) {
        final int progressMax = model.getListOfPics().size();

        final NotificationCompat.Builder notification = new NotificationCompat.Builder(this, CHANNEL_ID_1)
                .setSmallIcon(R.drawable.flag_egypt)
                .setContentTitle("Upload")
                .setContentText("Upload in progress")
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setProgress(progressMax, 0, false);

        notificationManager.notify(5, notification.build());

        Log.e(TAG, "uploadParcelable: " + "\n \n \n \n ");
        Log.e(TAG, "uploadParcelable:size " + progressMax);
        Log.e(TAG, "uploadParcelable: " + model.getPlaceName());
        Log.e(TAG, "uploadParcelable: " + model.getDate());
        Log.e(TAG, "uploadParcelable: " + model.getAddress());
        Log.e(TAG, "uploadParcelable: " + model.getAlbumName());
        Log.e(TAG, "uploadParcelable: " + model.getRegisteredName());
        Log.e(TAG, "uploadParcelable: " + model.getUnixTime());
        Log.e(TAG, "uploadParcelable: " + "\n \n \n \n ");
        HangOutModel hangOutModel = new HangOutModel();
        ArrayList<String> listOfPics = new ArrayList<>();

        for (int i = 0; i < model.getListOfPics().size(); i++) {
            Log.e(TAG, "uploadParcelable:i " + i);
            String photoName = UUID.randomUUID() + ".jpg";
            FirebaseStorage.getInstance().getReference().child(FirebaseAuth.getInstance().getUid())
                    .child(model.getRegisteredName())
                    .child(photoName).putFile(model.getListOfPics().get(i))
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                sucCount++;
                                Log.e(TAG, "onComplete:succount  " + sucCount);
                                notification.setProgress(progressMax, sucCount, false);
                                notification.setContentText("Upload in progress " + "(" + sucCount + "/" + model.getListOfPics().size() + ").");
                                notificationManager.notify(5, notification.build());

                                if (sucCount == progressMax) {
                                    notification.setContentText("Upload finished")
                                            .setProgress(0, 0, false)
                                            .setOngoing(false);
                                    notificationManager.notify(5, notification.build());
                                }
                            } else {
                                Toast.makeText(UploadPhotosService.this, "something went wrong :( , try again", Toast.LENGTH_SHORT).show();
                                stopSelf();
                                notification.setContentText("upload failed")
                                        .setProgress(0, 0, false)
                                        .setOngoing(false);
                                notificationManager.notify(5, notification.build());
                            }
                        }

                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    FirebaseStorage.getInstance().getReference().child(FirebaseAuth.getInstance().getUid())
                            .child(model.getRegisteredName()).child(photoName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            listOfPics.add(uri.toString());
                            Log.e(TAG, "onSuccess: " + uri );
                            if (sucCount == progressMax) {
                                Log.e(TAG, "onSuccess: "+ " de el log elly bt3ml upload :)" );
                                FirebaseDatabase.getInstance().getReference().child("Users")
                                        .child(FirebaseAuth.getInstance().getUid()).child("Hangouts")
                                        .child(hangOutModel.getRegisteredName()).setValue(hangOutModel);
                                stopSelf();
                            }
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    stopSelf();
                    notification.setContentText("Upload Failed")
                            .setProgress(0, 0, false)
                            .setOngoing(false);
                    notificationManager.notify(5, notification.build());
                }
            });


        }


        hangOutModel.setRegisteredName(model.getRegisteredName());
        hangOutModel.setUnixTime(model.getUnixTime());
        hangOutModel.setDate(model.getDate());
        hangOutModel.setListOfPics(listOfPics);
        hangOutModel.setAddress(model.getAddress());
        hangOutModel.setPlaceName(model.getPlaceName());
        hangOutModel.setAlbumName(model.getAlbumName());


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ParcelableHangOutModel model = intent.getParcelableExtra("parcel");
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
