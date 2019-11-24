package com.example.moonwaygravity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.moonwaygravity.Adapter.VehicleListAdapter;
import com.example.moonwaygravity.Model.Vehicle;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserProfileFragment extends Fragment {


    Button btn_topup;
    TextView balance;
    private RecyclerView vehicleList;
    private FirebaseAuth mAuth;
    private DatabaseReference vehRef, custRef;
    private VehicleListAdapter adapter;
    List<Vehicle> vehicle;

    private OnFragmentInteractionListener mListener;

    public UserProfileFragment() {
        // Required empty public constructor
    }


    public static UserProfileFragment newInstance(String param1, String param2) {
        UserProfileFragment fragment = new UserProfileFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        btn_topup = view.findViewById(R.id.topup);
        vehicleList = (RecyclerView) view.findViewById(R.id.vehicleList);
        balance = view.findViewById(R.id.balance);

        btn_topup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), TopUpActivity.class);
                startActivity(intent);
            }
        });

        mAuth = FirebaseAuth.getInstance();
        final String currentUserID = mAuth.getCurrentUser().getUid();
        custRef = FirebaseDatabase.getInstance().getReference().child("Customer");
        vehRef = FirebaseDatabase.getInstance().getReference().child("Vehicle");

        vehicle(currentUserID);
        userProfile(currentUserID);


        return view;
    }

    private void vehicle(final String currentUserID) {
        vehicleList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        vehicleList.setLayoutManager(linearLayoutManager);


        vehicle = new ArrayList<>();

        vehRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                vehicle.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Vehicle v = snapshot.getValue(Vehicle.class);
                    if (v.getCustomerId().equals(currentUserID)) {
                        vehicle.add(v);
                    }
                    adapter = new VehicleListAdapter(getActivity(), vehicle);
                    vehicleList.setAdapter(adapter);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void userProfile(final String currentUserID) {
        custRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String custid = snapshot.child("customerId").getValue().toString();
                    if (custid.equals(currentUserID)) {
                        Double bal = Double.parseDouble(snapshot.child("accountBalance").getValue().toString());
                        balance.setText(String.format("RM %.2f", bal));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
