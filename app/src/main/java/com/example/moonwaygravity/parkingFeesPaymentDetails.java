package com.example.moonwaygravity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.JsonReader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class parkingFeesPaymentDetails extends AppCompatActivity {

    TextView txtId, txtAmount, txtStatus,txtCustomer,txtDate,txtTime;
    Button btnSave,btnBack;
    String currentUserid;

    DatabaseReference transRef,entryRef,exitRef;
    RelativeLayout receipt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_fees_payment_details);


        txtAmount = (TextView) findViewById(R.id.txtAmount);
        txtId = (TextView) findViewById(R.id.txtId);
        txtStatus = (TextView) findViewById(R.id.txtStatus);
        txtCustomer = (TextView) findViewById(R.id.txtCustomer);
        txtDate = findViewById(R.id.txtDate);
        txtTime = findViewById(R.id.txttime);

        btnBack = findViewById(R.id.btnback);
        btnSave = findViewById(R.id.btnSave);

        currentUserid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        receipt = findViewById(R.id.receipt);

        //get intent
        Intent intent = getIntent();

        try {
            JSONObject jsonObject = new JSONObject(intent.getStringExtra("PaymentDetails"));
            showDetails(jsonObject.getJSONObject("response"), intent.getStringExtra("PaymentAmount"),intent.getStringExtra("vehicleLicensePlateNumber"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File file = saveBitMap(parkingFeesPaymentDetails.this, receipt);    //which view you want to pass that view as parameter
                if (file != null) {
                    Toast.makeText(parkingFeesPaymentDetails.this,"Receipt has been saved to the gallery!",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(parkingFeesPaymentDetails.this,MainActivity.class);
                    startActivity(intent);
                } else {
                    Log.i("TAG", "Oops! Image could not be saved.");

                }
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(parkingFeesPaymentDetails.this,MainActivity.class);
                startActivity(intent);
            }
        });

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
                startActivity(new Intent(parkingFeesPaymentDetails.this,CustomerLoginActivity.class));
                return true;
        }
        return false;
    }

    private void showDetails(JSONObject response, final String paymentAmount, final String vehicleLicensePlate){
        try{
            txtId.setText(response.getString("id"));
            txtStatus.setText(response.getString("state"));
            txtAmount.setText("MYR" + paymentAmount);

            Date date = Calendar.getInstance().getTime();
            SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MM-yyyy");
            SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");

            txtDate.setText(sdfDate.format(date));
            txtTime.setText(sdfTime.format(date));
            txtCustomer.setText(vehicleLicensePlate);



            transRef = FirebaseDatabase.getInstance().getReference();
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("status", response.getString("state"));
            hashMap.put("amount", paymentAmount);
            hashMap.put("transactionType", "Pay Parking Fees");
            hashMap.put("transactionDate", sdfDate.format(date));
            hashMap.put("transactionTime", sdfTime.format(date));

            transRef.child("Transaction").child(response.getString("id")).setValue(hashMap);
            String transactionID = response.getString("id");


            entryRef = FirebaseDatabase.getInstance().getReference().child("EntryRecords");

            entryRef.orderByChild("vehicleLicensePlateNumber").equalTo(vehicleLicensePlate).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if(snapshot.child("status").getValue().equals("Pending")){
                            String key = snapshot.getKey();
                            entryRef.child(key).child("status").setValue("Paid");
                            entryRef.child(key).child("transID").setValue(transactionID);

//                            uploadExitRecord(key,vehicleLicensePlate,paymentAmount,transactionID);
                            return;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            //save payment
            //save exit record
            //save transacto


        }catch(JSONException e){
            e.printStackTrace();
        }
    }
//    public void uploadExitRecord(String entryID,String vehicleLicensePlate,String amount,String transID){
//        exitRef = FirebaseDatabase.getInstance().getReference();
//        HashMap<String, Object> hashMap = new HashMap<>();
//        hashMap.put("exitDate", "-");
//        hashMap.put("exitTime", "-");
//        hashMap.put("fees", amount);
//        hashMap.put("vehicleLicensePlateNumber", vehicleLicensePlate);
//        hashMap.put("entryId", entryID);
//        hashMap.put("transactionId", transID);
//
//        exitRef.child("ExitRecords").push().setValue(hashMap);
//    }
    private File saveBitMap(Context context, View drawView){
        File pictureFileDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"Handcare");
        if (!pictureFileDir.exists()) {
            boolean isDirectoryCreated = pictureFileDir.mkdirs();
            if(!isDirectoryCreated)
                Log.i("ATG", "Can't create directory to save the image");
            return null;
        }
        String filename = pictureFileDir.getPath() +File.separator+ System.currentTimeMillis()+".jpg";
        File pictureFile = new File(filename);
        Bitmap bitmap =getBitmapFromView(drawView);
        try {
            pictureFile.createNewFile();
            FileOutputStream oStream = new FileOutputStream(pictureFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, oStream);
            oStream.flush();
            oStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("TAG", "There was an issue saving the image.");
        }
        scanGallery( context,pictureFile.getAbsolutePath());
        return pictureFile;
    }
    private Bitmap getBitmapFromView(View view) {
        //Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable =view.getBackground();
        if (bgDrawable!=null) {
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        }   else{
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        }
        // draw the view on the canvas
        view.draw(canvas);
        //return the bitmap
        return returnedBitmap;
    }
    // used for scanning gallery
    private void scanGallery(Context cntx, String path) {
        try {
            MediaScannerConnection.scanFile(cntx, new String[] { path },null, new MediaScannerConnection.OnScanCompletedListener() {
                public void onScanCompleted(String path, Uri uri) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
