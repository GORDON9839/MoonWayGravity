package com.example.moonwaygravity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class CustomerLoginActivity extends AppCompatActivity {

    TextInputEditText email, password;
    Button btn_login,btn_signup;
    ProgressDialog dialog;

    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    DatabaseReference reference;
    @Override
    protected void onStart() {
        super.onStart();



        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //check if user is not null
        if(firebaseUser != null){

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();

        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_login);


        auth = FirebaseAuth.getInstance();

        email = findViewById(R.id.emailhint);
        password = findViewById(R.id.passwordhint);
        btn_login = findViewById(R.id.login);
        btn_signup = findViewById(R.id.signup);

        dialog = new ProgressDialog(CustomerLoginActivity.this,ProgressDialog.THEME_DEVICE_DEFAULT_DARK);
        dialog.setTitle("Logging In");
        dialog.setMessage("Please wait...");


        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
                String txt_email = email.getText().toString();
                String txt_password = password.getText().toString();

                if (TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password)) {
                    Toast.makeText(CustomerLoginActivity.this, "All fields are required!", Toast.LENGTH_SHORT).show();
                } else {
                    auth.signInWithEmailAndPassword(txt_email, txt_password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        if(auth.getCurrentUser().isEmailVerified()){
                                            Toast.makeText(CustomerLoginActivity.this, "WELCOME to join MoonWayGravity ", Toast.LENGTH_LONG).show();
                                            dialog.dismiss();
                                            Intent intent = new Intent(CustomerLoginActivity.this, MainActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);

                                        }else{
                                            Toast.makeText(CustomerLoginActivity.this, "Please verified your email address...", Toast.LENGTH_LONG).show();
                                        }
//                                        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//                                        reference = FirebaseDatabase.getInstance().getReference("User").child(firebaseUser.getUid());
//
//
//                                        reference.addValueEventListener(new ValueEventListener() {
//                                                    @Override
//                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                                        User c = dataSnapshot.getValue(User.class);
//                                                        if (c != null) {
//                                                    if (c.getRole().equals("Customer")) {
//                                                        Intent intent = new Intent(CustomerLoginActivity.this, MainActivity.class);
//                                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                                                        startActivity(intent);
//                                                        Toast.makeText(CustomerLoginActivity.this, "WELCOME, " + c.getName().toUpperCase(), Toast.LENGTH_LONG).show();
//                                                        finish();
//                                                    }else{
//                                                        Toast.makeText(CustomerLoginActivity.this, "You are not one of the customer!", Toast.LENGTH_SHORT).show();
//                                                    }
//                                                }
//                                            }
//
//                                            @Override
//                                            public void onCancelled(@NonNull DatabaseError databaseError) {
//                                                System.out.println("The read failed: " + databaseError.getCode());
//                                            }
//                                        });
                                    } else {
                                        Toast.makeText(CustomerLoginActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }
                                }
                            });
                }
            }
        });
        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CustomerLoginActivity.this,CustomerRegisterActivity.class);
                startActivity(intent);
                finish();

            }
        });

    }
}
