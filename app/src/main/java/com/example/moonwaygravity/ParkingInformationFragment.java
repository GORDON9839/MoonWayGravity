package com.example.moonwaygravity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.moonwaygravity.Adapter.EntryRecordListAdapter;
import com.example.moonwaygravity.Config.Config;
import com.example.moonwaygravity.Model.EntryRecords;
import com.example.moonwaygravity.Model.Vehicle;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;


public class ParkingInformationFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private static final int PAYPAL_REQUEST_CODE = 7171;
    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(Config.PAYPAL_CLIENT_ID);

    DatabaseReference entryRef, vehicleRef, parkingLotRef;
    Button pay;
    List<String> licensePlate;
    String currentUserId;
    Double fees;
    String amount = "";
    private RecyclerView entryRecycler;
    private EntryRecordListAdapter entryAdapter;
    List<EntryRecords> entryRec;


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
        View view = inflater.inflate(R.layout.fragment_parking_information, container, false);
        pay = view.findViewById(R.id.payParkingFee);
        entryRecycler = view.findViewById(R.id.entryRecycler);

        licensePlate = new ArrayList<>();
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Intent intent = new Intent(getActivity(), PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        getActivity().startService(intent);

        entryRecycler.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        entryRecycler.setLayoutManager(linearLayoutManager);

        entryRec = new ArrayList<>();
        Log.d("hi123","2");

        vehicleRef = FirebaseDatabase.getInstance().getReference("Vehicle");
        vehicleRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    if (data.child("customerId").getValue().equals(currentUserId)) {
                        Log.d("hi123","2");
                        licensePlate.add(data.child("vehicleLicensePlateNumber").getValue().toString());
                        searchEntryRecord(licensePlate);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });


        return view;

    }

    public void searchEntryRecord(final List<String> vehicle) {
        entryRef = FirebaseDatabase.getInstance().getReference("EntryRecords");
        Log.d("hi123","nothing");


            entryRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.d("hmmmm","got");
                    int count=0;

                    entryRec.clear();

                    for (String vh : vehicle) {
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()) {

                            EntryRecords entryRecords = snapshot.getValue(EntryRecords.class);
                            if (vh.equals(entryRecords.getVehicleLicensePlateNumber())) {


                                count++;
                                entryRec.add(entryRecords);
                                Toast.makeText(getActivity(), String.valueOf(count), Toast.LENGTH_LONG).show();
                                //add to entry adapter

                            }



                        }

                    }
//                    Toast.makeText(getActivity(), String.valueOf(entryRec.size()), Toast.LENGTH_LONG).show();
                    entryAdapter = new EntryRecordListAdapter(getActivity(), entryRec);
                    entryRecycler.setAdapter(entryAdapter);

                    entryAdapter.onBind = (viewHolder, position, ent) -> {
                        viewHolder.payParking.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Log.d("hi", "startButton");
                                calculateFees(ent.getDate(), ent.getTime(),ent.getVehicleLicensePlateNumber());

                            }

                        });
                    };



                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });







    }

    private void calculateFees(String date, String time,String licensePlateNumber) {
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


                    final MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());
                    String parkingFee = String.format("%.2f", fees);
                    builder.setMessage("Your Parking Fees for " + licensePlateNumber + " is RM" + parkingFee + "\n Do you want to continue to pay the parking fee?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    processPayment(fees);

                                }
                            }).setNegativeButton("no", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();

                        }
                    }).show();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        } catch (ParseException e) {
            Log.d("hi", e.getMessage());
        }
    }

    private void processPayment(double fees) {
        amount = String.valueOf(fees);
        PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(String.valueOf(amount)), "MYR", "Top Up For E-Wallet", PayPalPayment.PAYMENT_INTENT_SALE);
        Intent intent = new Intent(getActivity(), PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payPalPayment);

        startActivityForResult(intent, PAYPAL_REQUEST_CODE);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAYPAL_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirmation != null) {
                    try {
                        String paymentDetails = confirmation.toJSONObject().toString(4);
                        startActivity(new Intent(getActivity(), PaymentDetails.class)
                                .putExtra("PaymentDetails", paymentDetails)
                                .putExtra("PaymentAmount", amount)
                        );
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    Toast.makeText(getActivity(), "Cancel", Toast.LENGTH_SHORT).show();
                }
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID)
                Toast.makeText(getActivity(), "Invalid", Toast.LENGTH_SHORT).show();
        }
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

