package com.example.moonwaygravity;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MyBroadcastReceiver extends BroadcastReceiver {

    DatabaseReference noticeRef;
    @Override
    public void onReceive(Context context, Intent intent) {
        String action=intent.getStringExtra("action");
        String key=intent.getStringExtra("key");

        Log.wtf("hi123",action);
        Log.wtf("hi123",key);

        noticeRef = FirebaseDatabase.getInstance().getReference("Notification");
        if(action.equals("yes")){
            noticeRef.child(key).child("status").setValue("Inform Staff");
            NotificationManager nm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
            nm.cancel(2);

            Intent yesintent = new Intent(context, MainActivity.class);
            yesintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent yespendingIntent =
                    PendingIntent.getBroadcast(context, 0, yesintent, 0);

            Uri defSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.logo)
                    .setContentText("My Notification")
                    .setContentTitle("The staff is helping you now...")
                    .setAutoCancel(true)
                    .setSound(defSoundUri)
                    .setContentIntent(yespendingIntent);
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0 /* ID of notification */, builder.build());

        }else if(action.equals("no")){
            Intent yesintent = new Intent(context, MainActivity.class);
            yesintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent yespendingIntent =
                    PendingIntent.getBroadcast(context, 0, yesintent, 0);

            Uri defSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.logo)
                    .setContentText("My Notification")
                    .setContentTitle("Thank you for your reply.")
                    .setAutoCancel(true)
                    .setSound(defSoundUri)
                    .setContentIntent(yespendingIntent);
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(1 /* ID of notification */, builder.build());
        }
    }
}
