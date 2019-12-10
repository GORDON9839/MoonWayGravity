package com.example.moonwaygravity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class ChangePassword extends AppCompatActivity {
    EditText oldPass, newPass, confirmPass;
    Toolbar toolbar;
    Button submit;
    FirebaseDatabase database;
    FirebaseUser firebaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        init();
    }
    private void init(){
        database = FirebaseDatabase.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        oldPass = findViewById(R.id.edtOldPass);
        newPass = findViewById(R.id.edtNewPass);
        confirmPass = findViewById(R.id.edtConfirmPass);
        submit = findViewById(R.id.submitPassword);
        toolbar = findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Change Password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(getDrawable(R.drawable.down_white_26));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nPass = newPass.getText().toString();
                String cPass = confirmPass.getText().toString();
                String oPass = oldPass.getText().toString();
                if (nPass.equals("") || cPass.equals("") || oPass.equals("")) {
                    showToast(R.drawable.no, "Please fill in all the field");
                } else {
                    AuthCredential credential = EmailAuthProvider
                            .getCredential(firebaseUser.getEmail(), oPass);
                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                //check password match
                                if (!nPass.equals(cPass)) {
                                    showToast(R.drawable.no, "New and Confirm password Not match!!");
                                } else {
                                    firebaseUser.updatePassword(nPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            showToast(R.drawable.success_60, "Password update successfully!!");
                                            finish();

                                        }
                                    });
                                }
                            } else {
                                showToast(R.drawable.no, "Authentication fail \n Wrong current password");
                            }
                        }

                    });
                }

            }
        });
    }
    private void showToast(int pngpath, String text) {
        Toast toast = new Toast(ChangePassword.this);
        TextView textView = new TextView(ChangePassword.this);
        textView.setText(text);
        textView.setCompoundDrawablesWithIntrinsicBounds(0, pngpath, 0, 0);
        toast.setView(textView);
        toast.show();
    }
}
