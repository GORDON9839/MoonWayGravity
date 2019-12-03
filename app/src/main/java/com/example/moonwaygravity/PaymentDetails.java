package com.example.moonwaygravity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.JsonReader;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class PaymentDetails extends AppCompatActivity {

    TextView txtId, txtAmount, txtStatus,txtCustomer;
    String currentUserid;

    DatabaseReference transRef,custRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_details);


        txtAmount = (TextView)findViewById(R.id.txtAmount);
        txtId = (TextView)findViewById(R.id.txtId);
        txtStatus = (TextView)findViewById(R.id.txtStatus);
        txtCustomer = (TextView)findViewById(R.id.txtCustomer);

        currentUserid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //get intent
        Intent intent = getIntent();

        try{
            JSONObject jsonObject = new JSONObject(intent.getStringExtra("PaymentDetails"));
            Toast.makeText(this, jsonObject.toString(), Toast.LENGTH_SHORT).show();
            showDetails(jsonObject.getJSONObject("response"), intent.getStringExtra("PaymentAmount"));
        }catch(JSONException e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(PaymentDetails.this,CustomerLoginActivity.class));
                return true;
        }
        return false;
    }

    private void showDetails(JSONObject response, String paymentAmount){
        try{
            txtId.setText(response.getString("id"));
            txtStatus.setText(response.getString("state"));
            txtAmount.setText("MYR" + paymentAmount);
            txtCustomer.setText(currentUserid);

            transRef = FirebaseDatabase.getInstance().getReference();
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("paymentId",response.getString("id"));
            hashMap.put("status", response.getString("state"));
            hashMap.put("message", paymentAmount);
            hashMap.put("transactionType", "Reload Credit");
            hashMap.put("customerId", currentUserid);

            transRef.child("Transaction").push().setValue(hashMap);

            custRef = FirebaseDatabase.getInstance().getReference().child("Customer");
            custRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot data:dataSnapshot.getChildren()){
                        if(data.child("customerId").getValue().equals(currentUserid)){
                            int balance = Integer.parseInt(data.child("accountBalance").getValue().toString());
                            updateBalance(balance);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }catch(JSONException e){
            e.printStackTrace();
        }
    }
    public void updateBalance(int bal){
        custRef.child(currentUserid).child("accountBalance").setValue(String.valueOf(bal));
    }


}
