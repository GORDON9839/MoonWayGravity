package com.example.moonwaygravity;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

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
import com.example.moonwaygravity.Model.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ChatMessaging extends Fragment {
    FirebaseUser firebaseUser;
    DatabaseReference reference;

    ImageButton btn_send;
    EditText text_send;
    Context context;

    MessageAdapter messageAdapter;
    List<Message> messages;

    RecyclerView recyclerView;
    private OnFragmentInteractionListener mListener;

    public ChatMessaging() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static ChatMessaging newInstance(String param1, String param2) {
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
        View view = inflater.inflate(R.layout.fragment_chat_messaging, container, false);


        recyclerView = view.findViewById(R.id.recycle_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        btn_send = view.findViewById(R.id.btn_send);
        text_send = view.findViewById(R.id.text_send);


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        final String category = "Chat";


        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = text_send.getText().toString();
                if (!msg.equals("")) {
                    sendMessage(firebaseUser.getUid(), "", msg, category, "123");
                    text_send.setText(""); // set the text blank after sent
                } else {
                    Toast.makeText(getActivity(), "Please enter your enquires", Toast.LENGTH_SHORT).show();
                }

            }
        });
        readMessage(firebaseUser.getUid(), category);
        return view;
    }

    private void sendMessage(String sender, String receiver, String message, String category, String date) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", "");
        hashMap.put("message", message);
        hashMap.put("category", category);
        hashMap.put("date", date);

        reference.child("Message").push().setValue(hashMap);
    }
    // TODO: Rename method, update argument and hook method into UI event

    private void readMessage(final String myid, final String category) {
        messages = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Message");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messages.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Message message = snapshot.getValue(Message.class);

                    if (message.getSender().equals(myid) || message.getReceiver().equals(myid)) {
                        messages.add(message);
                    }

                    messageAdapter = new MessageAdapter(getActivity(), messages);
                    recyclerView.setAdapter(messageAdapter);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

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
