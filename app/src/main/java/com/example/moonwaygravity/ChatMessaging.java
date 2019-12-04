package com.example.moonwaygravity;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.moonwaygravity.Adapter.MessageAdapter;
import com.example.moonwaygravity.Model.Chatroom;
import com.example.moonwaygravity.Model.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class ChatMessaging extends Fragment {
    private FirebaseUser firebaseUser;
    ImageButton btn_send, btn_call;
    EditText text_send;
    MessageAdapter messageAdapter;
    List<Message> messages;

    RecyclerView recyclerView;
    private OnFragmentInteractionListener mListener;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();

    public ChatMessaging() {
        // Required empty public constructor
    }

    Resources res;
    List<Chatroom> chatrooms = new ArrayList();
    public static ChatMessaging newInstance() {
        ChatMessaging fragment = new ChatMessaging();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //initialization
        View view = inflater.inflate(R.layout.fragment_chat_messaging, container, false);
        res = view.getResources();
        recyclerView = view.findViewById(R.id.recycle_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        btn_send = view.findViewById(R.id.btn_send);
        text_send = view.findViewById(R.id.text_send);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        retrieveActiveChatrooms(firebaseUser.getUid()); // read all the acive chatroom base on the customer
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = text_send.getText().toString();
                if (!msg.equals("")) { // check msg is blank
                    Boolean isActive = false;
                    Date date = new Date();
                    if (chatrooms.size() != 0) { //check the chatroom is retrieved
                        for (Chatroom croom : chatrooms) {
                            if (croom.getStatus().equals(res.getString(R.string.activeState))) { // if the status is equal to active then send message
                                isActive = true;
                                sendMessage(croom.getId(), croom.getCustomerid(), croom.getStaffid(), msg, date);
                                readActiveMessage();
                                break;
                            }
                        }
                    }
                    if (isActive == false) { // if no any active chatroom find open a new one
                        String newChatroomid = createChatroom(firebaseUser.getUid(), "", res.getString(R.string.activeState));
                        sendMessage(newChatroomid, firebaseUser.getUid(), "", msg, date); // receiver is null will be active by staff
                        readActiveMessage();
                    }
                    text_send.setText(""); // set the text blank after sent
                } else {
                    Toast.makeText(getActivity(), "Please enter your enquires", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }

    // for retrieve message
    private void retrieveActiveChatrooms(String uid) {
        DatabaseReference chatroomRef = database.getReference("Chatroom");
        chatroomRef.orderByChild("customerid").equalTo(uid).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                Chatroom chatroom = dataSnapshot.getValue(Chatroom.class);
                if (chatroom.getStatus().toLowerCase() != res.getString(R.string.closeState)) {
                    chatroom.setId(dataSnapshot.getKey());
                    chatrooms.add(chatroom);
                }
                readActiveMessage();
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
    private void readActiveMessage() {

        messages = new ArrayList<>();
        DatabaseReference messageRef = database.getReference("Message");
        if (chatrooms.size() != 0) {
            for (Chatroom croom : chatrooms) {
                if (croom.getStatus().equals(res.getString(R.string.activeState))) {
                    messageRef.orderByChild("chatroomid").equalTo(croom.getId()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            messages.clear();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Message message = snapshot.getValue(Message.class);
                                messages.add(message);
                            }
                            messageAdapter = new MessageAdapter(getActivity(), messages);
                            recyclerView.setAdapter(messageAdapter);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    break;
                }
            }
        }
    }

    // for send message
    private String createChatroom(String customerid, String staffid, String status) {
        DatabaseReference chatroomRef = database.getReference("Chatroom");
        String chatroomid = "";
        Date date = new Date();
        Chatroom chatroom = new Chatroom();
        chatroom.setCustomerid(firebaseUser.getUid());
        chatroom.setStaffid("");
        chatroom.setDate(date);
        chatroom.setStatus(res.getString(R.string.activeState));
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
    // TODO: Rename method, update argument and hook method into UI event
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
