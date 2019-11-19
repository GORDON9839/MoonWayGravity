package com.example.moonwaygravity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class CustomerRegisterActivity extends AppCompatActivity {

    TextInputEditText name, email, contact, password,confirm_password;
    Button btn_register,btn_cancel;

    FirebaseAuth auth;
    DatabaseReference reference;
    Context context;

    ProgressDialog dialog;

    String emailRegex = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$";
    String contactRegex = "^(01)[0-46-9]*[0-9]{7,8}$";
    String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,10}$";

    @Override
    protected void onStart() {
        super.onStart();
        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    Log.d("hi", "connected");
                } else {
                    Log.d("hi", "not connected");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Log.d("hi","Listener was cancelled");
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_register);


        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        contact = findViewById(R.id.contact);
        password = findViewById(R.id.password);
        confirm_password = findViewById(R.id.confirmPassword);
        btn_register = findViewById(R.id.submit);
        btn_cancel = findViewById(R.id.cancel);

        auth = FirebaseAuth.getInstance();
        dialog = new ProgressDialog(CustomerRegisterActivity.this,ProgressDialog.THEME_DEVICE_DEFAULT_DARK);
        dialog.setTitle("Creating Account");
        dialog.setMessage("Please wait...");

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();


                String txt_name = name.getText().toString();
                String txt_email = email.getText().toString();
                String txt_contact = contact.getText().toString();
                String txt_password = password.getText().toString();
                String txt_cpassword = confirm_password.getText().toString();

                if(TextUtils.isEmpty(txt_name)|| TextUtils.isEmpty(txt_email)|| TextUtils.isEmpty(txt_contact)|| TextUtils.isEmpty(txt_password)|| TextUtils.isEmpty(txt_cpassword)){
                    Toast.makeText(CustomerRegisterActivity.this, "All fields are required!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }else if(txt_cpassword.equals(txt_password)==false) {
                    Toast.makeText(CustomerRegisterActivity.this, "Confirm Password not matched with password...", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }else if(!txt_email.matches(emailRegex)){
                    Toast.makeText(CustomerRegisterActivity.this, "Invalid email address format...", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }else if(!txt_contact.matches(contactRegex)){
                    Toast.makeText(CustomerRegisterActivity.this, "Invalid contact number format...", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }else if(!txt_password.matches(passwordRegex)){
                    Toast.makeText(CustomerRegisterActivity.this, "The password must be more than 8 character include 1 uppercase, 1 lowercase and 1 digit...", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
                else
                {
                    register(txt_name,txt_email,txt_contact,txt_password);

                }
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CustomerRegisterActivity.this, CustomerLoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    private void register(final String name, final String email, final String contact, final String password){

        auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("hi", "login");
                        if(task.isSuccessful()){
                            final FirebaseUser firebaseUser = auth.getCurrentUser();
                            String userid = firebaseUser.getUid();
                            Log.d("hi", "register");

                            reference = FirebaseDatabase.getInstance().getReference("Customer").child(userid);
                            Log.d("hi", "register1");

                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("customerId", userid);
                            hashMap.put("name", name);
                            hashMap.put("contact", contact);
                            hashMap.put("accountBalance", "0");
                            hashMap.put("imageUrl", "default");

                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    Toast.makeText(CustomerRegisterActivity.this, "Registered successfully. Please check your email for verification...", Toast.LENGTH_LONG).show();
                                                    FirebaseAuth.getInstance().signOut();
                                                    dialog.dismiss();

                                                    Intent intent = new Intent(CustomerRegisterActivity.this, CustomerLoginActivity.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(intent);
                                                    finish();
                                                }else{
                                                    Toast.makeText(CustomerRegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                }

                                            }
                                        });

                                    }
                                }
                            });
                        }else{
                            FirebaseAuthException e = (FirebaseAuthException )task.getException();
                            Log.d( "hi","Failed Registration: "+e.getMessage());
                            Toast.makeText(CustomerRegisterActivity.this, "This email address already existed...", Toast.LENGTH_SHORT).show();

                        }
                    }
                });

    }
}

