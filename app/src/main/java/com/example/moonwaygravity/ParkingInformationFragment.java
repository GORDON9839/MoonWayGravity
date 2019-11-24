package com.example.moonwaygravity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.moonwaygravity.Model.Vehicle;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class ParkingInformationFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    DatabaseReference entryRef,vehicleRef;
    Button pay;
    List<String> licensePlate;
    String currentUserId;


    public ParkingInformationFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_parking_information, container, false);
        pay = view.findViewById(R.id.payParkingFee);

        final MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());
        builder.setMessage("Your Parking Fees for")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).setNegativeButton("no", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();

            }
        })
        .show();

        licensePlate = new ArrayList<>();
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vehicleRef = FirebaseDatabase.getInstance().getReference("Vehicle");
                vehicleRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot data:dataSnapshot.getChildren()){
                            if(data.child("customerId").getValue().equals(currentUserId)){
                                licensePlate.add(data.child("vehicleLicensePlateNumber").getValue().toString());


                            }

                        }
                        searchEntryRecord(licensePlate);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });





        return view;

    }
    public void searchEntryRecord(final List<String> vehicle){
        entryRef = FirebaseDatabase.getInstance().getReference("EntryRecords");
        entryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot data:dataSnapshot.getChildren()){
                    Log.d("hi",vehicle.get(0));
                    for(int i=0;i<vehicle.size();i++){
                        Log.d("hi",vehicle.get(i));
                        if(data.child("vehicleLicensePlateNumber").getValue().equals(vehicle.get(i))){
                            Log.d("hi","abc");
                        }
                    }



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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

