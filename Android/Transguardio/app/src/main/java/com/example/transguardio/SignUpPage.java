package com.example.transguardio;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignUpPage extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://transguardioone-default-rtdb.firebaseio.com/").getReference();
    String uniqueID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Code to hide title and action bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_sign_up_page);
        Spinner spinner = findViewById(R.id.textView);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.gender,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        Button signupButton = findViewById(R.id.signuppagesignupbutton);
        EditText email = findViewById(R.id.editTextTextPersonName);
        EditText number = findViewById(R.id.editTextTextPersonName2);
        EditText password = findViewById(R.id.editTextTextPersonName3);
        EditText reTypePassword = findViewById(R.id.editTextTextPersonName4);
        TextView signIn = findViewById(R.id.signInClickable);
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSignInPage();
            }
        });
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (email.length()==0){
                    email.setError("Email ID cannot be empty");
                } else if (number.length()!=10){
                    number.setError("Number should be 10 digits");
                }else if(password.length()<=6){
                    password.setError("Password should atleast be 7 characters");
                }
                else if (!reTypePassword.getText().toString().equals(password.getText().toString())){
                    reTypePassword.setError("Passwords do not match");
                } else if(spinner.getSelectedItemPosition()==0){
                    ((TextView)spinner.getSelectedView()).setError("Please select a gender");
                }
                else{
                    String phonenumber = number.getText().toString();
                    String password1 = reTypePassword.getText().toString();
                    String gender1 = spinner.getSelectedItem().toString();
                    String emailaddress = email.getText().toString();
                    databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.hasChild(phonenumber)){
                                Toast.makeText(SignUpPage.this, "Phone has already been registerd",Toast.LENGTH_SHORT).show();
                            }
                            else{
                                databaseReference.child("users").child(phonenumber).child("Email").setValue(emailaddress);
                                databaseReference.child("users").child(phonenumber).child("password").setValue(password1);
                                databaseReference.child("users").child(phonenumber).child("gender").setValue(gender1);
                                if (gender1.equals("female")){
                                    databaseReference.child("users").child(phonenumber).child("WOR").setValue("Yes");
                                }
                                else{
                                    databaseReference.child("users").child(phonenumber).child("WOR").setValue("No");
                                }
                                databaseReference.child("users").child(phonenumber).child("MinStars").setValue(0.0);
                                databaseReference.child("users").child(phonenumber).child("MinRides").setValue(0.0);
                                databaseReference.child("users").child(phonenumber).child("CheckFreq").setValue(30);
                                databaseReference.child("users").child(phonenumber).child("Frequency").setValue(1);
                                uniqueID = phonenumber;
                                openHomePage();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });

                }
            }
        });

    }
    public void openSignInPage(){
        Intent intent = new Intent(this, SignInPage.class);
        startActivity(intent);
    }
    public void openHomePage(){
        Intent intent = new Intent(this, HomePage.class);
        intent.putExtra("UniqueID",uniqueID);
        startActivity(intent);
    }
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String text = adapterView.getItemAtPosition(i).toString();
    }


    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}