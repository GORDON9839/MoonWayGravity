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
import com.example.moonwaygravity.Model.Customer;
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
import com.google.android.material.button.*;
import java.util.ArrayList;
import java.util.List;

public class UserProfileFragment extends Fragment {


    Button btn_topup;
    MaterialButton btn_addVehicle;
    TextView balance,nameLabel;
    private RecyclerView vehicleList;
    private FirebaseAuth mAuth=FirebaseAuth.getInstance();;
    private DatabaseReference vehRef, custRef;
    private VehicleListAdapter adapter;
    List<Vehicle> vehicle;

    final String currentUserID = mAuth.getCurrentUser().getUid();
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
        btn_addVehicle = view.findViewById(R.id.addNewVehicle);
        nameLabel = view.findViewById(R.id.nameLabel);
        btn_topup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), TopUpActivity.class);
                startActivity(intent);
            }
        });
        btn_addVehicle.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddNewVehicle.class);
                startActivity(intent);
            }
        });

        custRef = FirebaseDatabase.getInstance().getReference().child("Customer");
        vehRef = FirebaseDatabase.getInstance().getReference().child("Vehicle");
        retrieveVehicle(currentUserID);
        retrieveUserProfile(currentUserID);
        return view;
    }
    private void retrieveVehicle(final String currentUserID) {
        vehicleList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        vehicleList.setLayoutManager(linearLayoutManager);

        vehicle = new ArrayList<>();

        vehRef.orderByChild("customerId").equalTo(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                vehicle.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Vehicle v = snapshot.getValue(Vehicle.class);

                        vehicle.add(v);

                    adapter = new VehicleListAdapter(getActivity(), vehicle);
                    vehicleList.setAdapter(adapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void retrieveUserProfile(final String currentUserID) {
        custRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Customer customer = dataSnapshot.getValue(Customer.class);
                        Double bal = Double.parseDouble(customer.getAccountBalance());
                        nameLabel.setText(customer.getName());
                        balance.setText(String.format("RM %.2f", bal));

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
