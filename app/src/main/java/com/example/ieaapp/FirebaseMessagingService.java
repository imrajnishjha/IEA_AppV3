package com.example.ieaapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    NotificationManager mNotificationManager;


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);


// playing audio and vibration when user se reques
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
        r.play();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            r.setLooping(false);
        }

        // vibration
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {100, 300, 300, 300};
        v.vibrate(pattern, -1);

        int resourceImage = getResources().getIdentifier(remoteMessage.getNotification().getIcon(), "drawable", getPackageName());

        final long[] registrationCount = new long[1];

        FirebaseDatabase.getInstance().getReference("Temp Registry").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                registrationCount[0] = snapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Intent resultIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification fcmNotification = new NotificationCompat.Builder(FirebaseMessagingService.this, "com.example.ieaadmin")
                .setContentTitle(remoteMessage.getNotification().getTitle())
                .setContentText(remoteMessage.getNotification().getBody())
//                .setContentIntent(pendingIntent)
//                .setStyle(new NotificationCompat.BigTextStyle().bigText(remoteMessage.getNotification().getBody()))
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_MAX)
                .setNumber((int) registrationCount[0])
                .build();

        Log.d("Registration Count: ", String.valueOf(registrationCount[0]));

        fcmNotification.notify();

//        mNotificationManager =
//                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            String channelId = "com.example.ieaadmin";
//            NotificationChannel channel = new NotificationChannel(
//                    channelId,
//                    "Channel human readable title",
//                    NotificationManager.IMPORTANCE_HIGH);
//            mNotificationManager.createNotificationChannel(channel);
//            fcmNotification..setChannelId(channelId);
//        }
//
//        mNotificationManager.notify(100, builder.build());

    }

}


