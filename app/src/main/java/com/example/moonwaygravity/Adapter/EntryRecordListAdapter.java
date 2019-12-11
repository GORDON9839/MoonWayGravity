package com.example.moonwaygravity.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.ColorSpace;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatCallback;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moonwaygravity.Config.Config;
import com.example.moonwaygravity.Model.EntryRecords;
import com.example.moonwaygravity.Model.Vehicle;
import com.example.moonwaygravity.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EntryRecordListAdapter extends RecyclerView.Adapter<EntryRecordListAdapter.entryViewHolder>{
    private Context context;
    private List<EntryRecords> entryRecords;

    private static final int PAYPAL_REQUEST_CODE = 7171;
    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(Config.PAYPAL_CLIENT_ID);

    public onBindCallBack onBind;

    private DatabaseReference parkingLotRef, slotRef,floorRef;
    double fees;
    String parked_slot, floorName;

    FirebaseUser firebaseUser;

    String amount = "";

    public EntryRecordListAdapter(Context context, List<EntryRecords> entryRecords){
        this.entryRecords = entryRecords;
        this.context = context;
    }

    @NonNull
    @Override
    public EntryRecordListAdapter.entryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.parking_information_list_layout,parent,false);
        return new EntryRecordListAdapter.entryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EntryRecordListAdapter.entryViewHolder holder, int position) {
        EntryRecords ent = entryRecords.get(position);
        holder.licensePlate.setText(ent.getVehicleLicensePlateNumber());
        holder.parkingLocation.setText(ent.getParkingSlotNumber());

        if(ent.getParkingSlotNumber() != "" && ent.getParkingSlotNumber()!= null){
            slotRef = FirebaseDatabase.getInstance().getReference().child("ParkingSlot").child(ent.getParkingSlotNumber());
            slotRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.hasChild("name")){
                        parked_slot = dataSnapshot.child("name").getValue().toString();
                        holder.parkingLocation.setText(parked_slot + "   " + ent.getParkedDate() + "  " + ent.getParkedTime());
                    }
            
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

        floorRef = FirebaseDatabase.getInstance().getReference().child("Floors").child(ent.getCarflowLocation());
        floorRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                floorName = dataSnapshot.child("floorName").getValue().toString().toUpperCase();
                holder.flowLocation.setText(floorName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        holder.entryDate.setText(ent.getDate());
        holder.entrytime.setText(ent.getTime());
        calculateFees(ent.getDate(),ent.getTime(),holder);

        if(onBind!=null){
            onBind.onViewBound(holder,position,ent);
        }

    }

    @Override
    public int getItemCount() {
        return entryRecords.size();
    }

    public class entryViewHolder extends RecyclerView.ViewHolder{
        public TextView licensePlate,parkingLocation,entryDate,entrytime,parkingFee,flowLocation;
        public Button payParking;

        public entryViewHolder(View itemView){
            super(itemView);
            licensePlate = itemView.findViewById(R.id.licensePlate);
            parkingLocation = itemView.findViewById(R.id.parkinglocation);
            entryDate = itemView.findViewById(R.id.entrydate);
            entrytime = itemView.findViewById(R.id.entryTime);
            parkingFee = itemView.findViewById(R.id.parkingfee);
            payParking = itemView.findViewById(R.id.payParkingFee);
            flowLocation = itemView.findViewById(R.id.flowLocation);
        }
    }
    public interface onBindCallBack{
        void onViewBound(entryViewHolder viewHolder,int position, EntryRecords ent);
    }
    public void calculateFees(String date,String time,entryViewHolder holder){
        String ed = date;
        String et = time;
        String combine = ed + " " + et;


        try {
            SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date entryd = sdfDate.parse(combine);

            Date currentd = new Date();


            long e = entryd.getTime();
            long c = currentd.getTime();

            long diff = c - e;

            long seconds = diff / 1000;
            final long minutes = seconds / 60;
            final long hours = minutes / 60;
            final long days = hours / 24;

            parkingLotRef = FirebaseDatabase.getInstance().getReference("ParkingLots").child("-LtmhqyC5ACsE-k8dB8Q");
            parkingLotRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    final int dailyRate = Integer.parseInt(dataSnapshot.child("dailyRate").getValue().toString());
                    int hourlyRate = Integer.parseInt(dataSnapshot.child("hourlyRate").getValue().toString());
                    int firstHourRate = Integer.parseInt(dataSnapshot.child("firstHourRate").getValue().toString());
                    if (hours >= 24) {
                        fees = (double) days * dailyRate;
                    } else if (minutes <= 20) {
                        fees = 0.00;
                    } else if (minutes > 20 && hours < 24) {
                        fees = (double) firstHourRate + (hours * hourlyRate);
                    } else if (hours == 1 && minutes > 20) {
                        fees = (double) firstHourRate;
                    }
                    holder.parkingFee.setText(String.format("RM %.2f",fees));




//                    final MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());
//                    String parkingFee = String.format("%.2f", fees);
//                    builder.setMessage("Your Parking Fees for " + licensePlate + " is RM" + parkingFee + "\n Do you want to continue to pay the parking fee?")
//                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialogInterface, int i) {
//                                    processPayment(fees);
//
//                                }
//                            }).setNegativeButton("no", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            dialogInterface.cancel();
//
//                        }
//                    }).show();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        } catch (ParseException e) {
            Log.d("hi", e.getMessage());
        }
    }



}
