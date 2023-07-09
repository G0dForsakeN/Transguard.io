package com.example.transguardio;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignInPage extends AppCompatActivity {
    String uniqueID;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://transguardioone-default-rtdb.firebaseio.com/").getReference("users");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Code to hide title and action bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_sign_in_page);
        TextView signUpTxt = findViewById(R.id.signUpTxt);
        EditText phoneNo = findViewById(R.id.signInPagePhoneNumber);
        EditText password = findViewById(R.id.signInPagePasswordText);
        Button button = findViewById(R.id.signInPageSignInButton);
        final Boolean[] flag = {true};
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = phoneNo.getText().toString();
                String passwordTxt = password.getText().toString();
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(phoneNumber).exists()){
                            if (String.valueOf(dataSnapshot.child(phoneNumber).child("password").getValue()).equals(passwordTxt)){
                                if (flag[0]){
                                    flag[0] =false;
                                    Toast.makeText(SignInPage.this, "Login Successful",Toast.LENGTH_SHORT).show();
                                    uniqueID = phoneNumber;
                                }
                                openHomePage();
                            }else{
                                password.setError("Incorrect Password");
                            }
                        } else{
                            phoneNo.setError("Phone not registered");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
        signUpTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSignUpPage();
            }
        });
    }

    private void openHomePage() {
        Intent intent = new Intent(this, HomePage.class);
        intent.putExtra("UniqueID",uniqueID);
        startActivity(intent);
    }

    private void openSignUpPage() {
        Intent intent = new Intent(this, SignUpPage.class);
        startActivity(intent);
    }
}