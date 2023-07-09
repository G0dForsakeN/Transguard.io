package com.example.transguardio;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SettingsPageFemale extends AppCompatActivity {
    DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://transguardioone-default-rtdb.firebaseio.com/").getReference("users");
    String uniqueID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Code to hide title and action bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_settings_page_female);
        EditText ratingText = findViewById(R.id.ratingGetText1);
        EditText numRides = findViewById(R.id.ridesGetText1);
        EditText freq = findViewById(R.id.editTextTextPersonName71);
        EditText timer = findViewById(R.id.editTextTextPersonName81);
        EditText wor = findViewById(R.id.editTextTextPersonName6);
        Intent i = getIntent();
        uniqueID = i.getExtras().getString("UniqueID","");
        ImageButton homeBtn = findViewById(R.id.imageButton61);
        ImageButton saveBtn = findViewById(R.id.saveButton1);
        ImageButton signOutBtn = findViewById(R.id.signOutButton1);
        ImageButton groupBtn = findViewById(R.id.imageButton21);
        groupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(SettingsPageFemale.this, SecondLastPage.class);
                intent2.putExtra("UniqueID", uniqueID);
                startActivity(intent2);
            }
        });
        signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(SettingsPageFemale.this, SignInPage.class);
                startActivity(intent1);
            }
        });
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReference.child(uniqueID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!ratingText.getText().toString().equals("")){
                            databaseReference.child(uniqueID).child("MinStars").setValue(ratingText.getText().toString());
                        }

                        if (!numRides.getText().toString().equals("")){
                            databaseReference.child(uniqueID).child("MinRides").setValue(numRides.getText().toString());
                        }

                        if (!freq.getText().toString().equals("")){
                            databaseReference.child(uniqueID).child("Frequency").setValue(freq.getText().toString());
                        }

                        if (!timer.getText().toString().equals("")){
                            databaseReference.child(uniqueID).child("CheckFreq").setValue(timer.getText().toString());
                        }
                        if (!wor.getText().toString().equals("")){
                            databaseReference.child(uniqueID).child("WOR").setValue(wor.getText().toString());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                Toast.makeText(SettingsPageFemale.this, "Changes Saved", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SettingsPageFemale.this, HomePage.class);
                intent.putExtra("UniqueID", uniqueID);
                startActivity(intent);
            }
        });
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsPageFemale.this, HomePage.class);
                intent.putExtra("UniqueID", uniqueID);
                startActivity(intent);
            }
        });
        databaseReference.child(uniqueID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("MinStars").exists()){
                    ratingText.setHint(snapshot.child("MinStars").getValue().toString());
                }
                if (snapshot.child("MinRides").exists()) {
                    numRides.setHint(snapshot.child("MinRides").getValue().toString());
                }
                if (snapshot.child("Frequency").exists()) {
                    freq.setHint(snapshot.child("Frequency").getValue().toString());
                }
                if (snapshot.child("CheckFreq").exists()) {
                    timer.setHint(snapshot.child("CheckFreq").getValue().toString());
                }
                if (snapshot.child("WOR").exists()) {
                    wor.setHint(snapshot.child("WOR").getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}