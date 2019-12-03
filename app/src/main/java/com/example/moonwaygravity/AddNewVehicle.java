package com.example.moonwaygravity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moonwaygravity.Model.Customer;
import com.example.moonwaygravity.Model.Message;
import com.example.moonwaygravity.Model.Vehicle;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.regex.Pattern;

import androidx.appcompat.widget.Toolbar;

public class AddNewVehicle extends AppCompatActivity {
    private FirebaseUser firebaseUser;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_vehicle);
        //initialize
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final EditText vehicleLicensePlate = (EditText) findViewById(R.id.newVehiclePlate);
        final EditText owner = (EditText) findViewById(R.id.newOwner);
        Button btn_AddVehicle = (Button) findViewById(R.id.addVehicle_button);
        Log.d("hmmm", firebaseUser.getUid());
        //get owner
        final DatabaseReference customerRef = database.getReference("Customer");
        customerRef.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Customer customer = dataSnapshot.getValue(Customer.class);
                owner.setText(customer.getName());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        owner.setText(firebaseUser.getDisplayName());
        //set toolbar
        Toolbar toolbar = findViewById(R.id.addVehicle_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //create a prompt button
        btn_AddVehicle.setOnClickListener(new View.OnClickListener() { //validate and save the vehicle plate to firebase
            @Override
            public void onClick(View view) {
                final String plateNumber = vehicleLicensePlate.getText().toString();
                final String ownerName = owner.getText().toString();
                if (!plateNumber.equals("")) { //
                    final Vehicle vehicle = new Vehicle(plateNumber, firebaseUser.getUid(), ownerName);
                    if (isLicensePlate(plateNumber)) { //if match the license plate pattern
                        saveLicensePlate(vehicle);
                    } else { // if not match the pattern
                        AlertDialog.Builder builder = new AlertDialog.Builder(AddNewVehicle.this);
                        builder.setTitle("License Plate Number Pattern ");
                        builder.setMessage("This seems not a Malaysia Vehicle License Plate Number. \n **if you wish to continue press OK");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                saveLicensePlate(vehicle);
                                dialog.dismiss();
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(AddNewVehicle.this, "Cancel", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        });
                        AlertDialog promptMessage = builder.create();
                        promptMessage.show();
                        Button buttonneg = promptMessage.getButton(DialogInterface.BUTTON_NEGATIVE);
                        buttonneg.setBackgroundColor(Color.WHITE);
                        buttonneg.setTextColor(Color.BLACK);
                        Button buttonpos = promptMessage.getButton(DialogInterface.BUTTON_POSITIVE);
                        buttonpos.setBackgroundColor(Color.WHITE);
                        buttonpos.setTextColor(Color.BLACK);

                    }
                } else {
                    Toast toast = new Toast(AddNewVehicle.this);
                    TextView textView = new TextView(AddNewVehicle.this);
                    textView.setText("Should not leave blank on License Plate Number");
                    textView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.no, 0, 0);
                    toast.setView(textView);
                    toast.show();

                }
            }
        });
    }

    private void saveLicensePlate(Vehicle vehicle ) {
        final Vehicle v=vehicle;
        DatabaseReference vehicleRef = database.getReference("Vehicle");
        vehicleRef.orderByChild("vehicleLicensePlateNumber").equalTo(vehicle.getVehicleLicensePlateNumber()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Toast.makeText(AddNewVehicle.this,"Record Exist, cannot added the same vehicle",Toast.LENGTH_LONG).show();
                }else{
                    DatabaseReference vehicleRef = database.getReference("Vehicle");
                    vehicleRef.push().setValue(v, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if (databaseError != null) {
                                Toast.makeText(AddNewVehicle.this, "Error Occured, Please try again" + databaseError.getMessage(), Toast.LENGTH_LONG).show();
                            } else {
                                Toast toast = new Toast(AddNewVehicle.this);
                                TextView textView = new TextView(AddNewVehicle.this);
                                textView.setText("Success");
                                textView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.success_60, 0, 0);
                                toast.setView(textView);
                                toast.show();
                                finish();
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }
    private void checkLicensePlateExistence(String plate){
        DatabaseReference vehicleRef = database.getReference("Vehicle");


    }
    private boolean isLicensePlate(String plate) { // to match whether the license plate match
        Pattern p = Pattern.compile("^([A-Z]{1,3}\\s?(\\d{1,4}))$");
        if (!plate.equals("")) {
            return p.matcher(plate).matches();
        } else {
            return false;
        }

    }

}
