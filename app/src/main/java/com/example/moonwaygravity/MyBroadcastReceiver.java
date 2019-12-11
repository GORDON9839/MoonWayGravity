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
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.moonwaygravity.Model.Chatroom;
import com.example.moonwaygravity.Model.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MyBroadcastReceiver extends BroadcastReceiver {

    DatabaseReference noticeRef;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseUser firebaseUser;
    List<Chatroom> chatrooms = new ArrayList();

    @Override
    public void onReceive(Context context, Intent intent) {
        String action=intent.getStringExtra("action");
        String key=intent.getStringExtra("key");
        String entryKey=intent.getStringExtra("entryKey");


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


            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            Date date = new Date();


            retrieveActiveChatrooms(firebaseUser.getUid(),entryKey);

            DatabaseReference entryRef = database.getReference("EntryRecords");
            entryRef.child(entryKey).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    String licensePlate = dataSnapshot.child("vehicleLicensePlateNumber").getValue().toString();

                    String msg = "I have an issue which is my car has been stolen by unauthorized driver\n" +
                            "My vehicle license plate number is " + licensePlate
                            + "\nDate is " + date;

                    Boolean isActive = false;

                    //Log.wtf("hi123",data1.getKey());
                    if (chatrooms.size() != 0) { //check the chatroom is retrieved
                        for (Chatroom croom : chatrooms) {
                            if (croom.getStatus().equals("active")) { // if the status is equal to active then send message
                                isActive = true;
                                sendMessage(croom.getId(), croom.getCustomerid(), croom.getStaffid(), msg, date);
                                break;
                            }
                        }
                    }
                    if (isActive == false) { // if no any active chatroom find open a new one
                        String newChatroomid = createChatroom(firebaseUser.getUid(), "", "active");
                        sendMessage(newChatroomid, firebaseUser.getUid(), "", msg, date); // receiver is null will be active by staff
                    }
                }



                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


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
    private void retrieveActiveChatrooms(String uid,String key) {


        DatabaseReference chatroomRef = database.getReference("Chatroom");
        chatroomRef.orderByChild("customerid").equalTo(uid).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {



                    Chatroom chatroom = dataSnapshot.getValue(Chatroom.class);
                    if (chatroom.getStatus().toLowerCase() != "close") {
                        chatroom.setId(dataSnapshot.getKey());
                        chatrooms.add(chatroom);
                    }


                }


            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private String createChatroom(String customerid, String staffid, String status) {
        DatabaseReference chatroomRef = database.getReference("Chatroom");
        String chatroomid = "";
        Date date = new Date();
        Chatroom chatroom = new Chatroom();
        chatroom.setCustomerid(firebaseUser.getUid());
        chatroom.setStaffid("");
        chatroom.setDate(date);
        chatroom.setStatus(status);
        chatroomid = chatroomRef.push().getKey(); // create a new key for chatroom
        chatroomRef.child(chatroomid).setValue(chatroom); // push to the chatroom node
        return chatroomid;
    }
    private void sendMessage(String chatroomid, String sender, String receiver, String message, Date date) { // push to firebase for message
        DatabaseReference messageRef = database.getReference("Message");
        Message msg = new Message();
        msg.setChatroomid(chatroomid);
        msg.setDate(date);
        msg.setReceiver(receiver);
        msg.setSender(sender);
        msg.setMessage(message);
        messageRef.push().setValue(msg);
    }
}
