package com.example.moonwaygravity;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.app.Notification.EXTRA_NOTIFICATION_ID;


public class PushNotification extends Service {
    FirebaseUser user;
    DatabaseReference mDatabase;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
        return super.onStartCommand(intent,flags,startId);
    }


    @Override
    public void onCreate() {
        super.onCreate();
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user !=null){


            mDatabase = FirebaseDatabase.getInstance().getReference("Notification");
            mDatabase.addValueEventListener(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot data:dataSnapshot.getChildren()){
                                String status = data.child("status").getValue().toString();
                                String userId = data.child("customerId").getValue().toString();
                                String entryId = data.child("entryId").getValue().toString();
                                String key = data.getKey();
                                if(userId.equals(user.getUid())&& status.equals("Pending")) {
                                    Intent yesintent = new Intent(PushNotification.this, MyBroadcastReceiver.class);
                                    yesintent.putExtra("action","yes");
                                    yesintent.putExtra("key",key);
                                    yesintent.putExtra("entryKey",entryId);
                                    PendingIntent yespendingIntent =
                                            PendingIntent.getBroadcast(PushNotification.this, 0, yesintent, PendingIntent.FLAG_UPDATE_CURRENT);


                                    Uri defSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                    NotificationCompat.Builder builder = new NotificationCompat.Builder(PushNotification.this)
                                            .setSmallIcon(R.drawable.logo)
                                            .setContentText("My Notification")
                                            .setContentTitle("Your car is detected has been drove by someone. Do you want to report")
                                            .setAutoCancel(true)
                                            .setSound(defSoundUri)
                                            .setContentIntent(yespendingIntent)
                                            .addAction(R.drawable.yes, "Yes", yespendingIntent)
                                            .addAction(R.drawable.no, "No", yespendingIntent);
                                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                    notificationManager.notify(2 /* ID of notification */, builder.build());


                                }

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    }
            );
        }
    }






}
