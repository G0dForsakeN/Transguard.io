package com.example.transguardio;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class SecondLastPage extends AppCompatActivity {
    DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://transguardioone-default-rtdb.firebaseio.com/").getReference("users");
    String uniqueID;
    String group = "";
    Boolean flag = true;
    Integer numberOfMem=0;
    TextView pleaseWork;
    EditText fail;
    EditText fail1;
    String Gender;
    ArrayList<String> addMembers = new ArrayList<>();
    ArrayList<String> deleteMembers = new ArrayList<>();
    ArrayList<String> numbers = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Code to hide title and action bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_second_last_page);
        Intent i = getIntent();
        uniqueID = i.getExtras().getString("UniqueID","");
        EditText getUserNumber = findViewById(R.id.pleaseworkenterusernumber);
        fail = getUserNumber;
        EditText getUserName = findViewById(R.id.pleaseworkenterusername);
        fail1 = getUserName;
        ImageButton addNewUserBtn = findViewById(R.id.pleaseworkaddnewuserbutton);
        TextView showGrp = findViewById(R.id.pleaseworktextviewfordisplayingnames);
        pleaseWork = showGrp;
        fetchGroupMembers(uniqueID);
        ImageButton homeIcon = findViewById(R.id.pleaseworkhomeicon);
        ImageButton settingsPage = findViewById(R.id.pleaseworksettingicon);
        ImageButton removeUser = findViewById(R.id.pleaseworkdustbinbutton);

        databaseReference.child(uniqueID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("gender").exists()){
                    Gender = snapshot.child("gender").getValue().toString();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        settingsPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Gender == "Male"){
                    addAndRemoveMembersFromFirebase(addMembers,deleteMembers,numbers);
                    openSettingsPage();
                }else{
                    addAndRemoveMembersFromFirebase(addMembers,deleteMembers,numbers);
                    openSettingsPageFemale();
                }
            }
        });
        homeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addAndRemoveMembersFromFirebase(addMembers,deleteMembers,numbers);
                openHomePage();
            }
        });
        addNewUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getUserNumber.getText().toString().equals("")){
                    getUserNumber.setError("Empty");
                } else if (getUserName.getText().toString().equals("")){
                    getUserName.setHint("Empty");
                }
                else{
                    addGroupMember(uniqueID,getUserNumber.getText().toString(),getUserName.getText().toString());
                    showGrp.setText(group);
                }
            }
        });
        removeUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getUserName.getText().toString().equals("")){
                    getUserName.setHint("Empty");
                }
                else{
                    RemoveGroupMember(uniqueID,getUserNumber.getText().toString(),getUserName.getText().toString());
                    showGrp.setText(group);
                }
            }
        });
    }

    private void addAndRemoveMembersFromFirebase(ArrayList<String> addMembers, ArrayList<String> removeMembers, ArrayList<String> numbers){
        for (String element : addMembers) {
            databaseReference.child(uniqueID).child("group").push().setValue(element);
        }
        for (String element1 : numbers ) {
            databaseReference.child(uniqueID).child("groupNumbers").push().setValue(element1);
        }
        databaseReference.child(uniqueID).child("group").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    String childNodeValue = childSnapshot.getValue(String.class);
                    if (deleteMembers.contains(childNodeValue)) {
                        childSnapshot.getRef().removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle any errors that occur
            }
        });
    }
    private void RemoveGroupMember(String uniqueID, String toString, String toString1) {
        if (group.contains(toString1)){
            String newString = removeSubstring(group,toString1+"\n");
            group = newString;
            flag = false;
        }
        if (!flag){
            flag=true;
            pleaseWork.setText(group);
            Toast.makeText(SecondLastPage.this,"User removed from the group", Toast.LENGTH_SHORT).show();
            deleteMembers.add(toString1);
        } else{
            fail1.setError("Member not in the group");
        }
    }

    private void openSettingsPage() {
        Intent intent = new Intent(SecondLastPage.this, SettingsPage.class);
        intent.putExtra("UniqueID", uniqueID);
        startActivity(intent);
    }

    private void openSettingsPageFemale() {
        Intent intent = new Intent(SecondLastPage.this, SettingsPageFemale.class);
        intent.putExtra("UniqueID", uniqueID);
        startActivity(intent);
    }
    private void openHomePage() {
        Intent intent = new Intent(SecondLastPage.this, HomePage.class);
        intent.putExtra("UniqueID", uniqueID);
        startActivity(intent);
    }

    private void addGroupMember(String uniqueID, String toString, String toString1) {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (dataSnapshot.getKey().equals(toString)){
                        group = group + toString1 + "\n";
                        flag = false;
                    }
                }
                if (!flag){
                    flag = true;
                    pleaseWork.setText(group);
                    addMembers.add(toString1);
                    numbers.add(toString);
                    Toast.makeText(SecondLastPage.this,"User added to the group", Toast.LENGTH_SHORT).show();
                } else{
                    fail.setError("Number not registered");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public String removeSubstring(String originalString, String substringToRemove) {
        // Check if the substring exists in the original string
        if (originalString.contains(substringToRemove)) {
            // Remove the substring using the replace() method
            return originalString.replace(substringToRemove, "");
        } else {
            // Return the original string if the substring is not found
            return originalString;
        }
    }

    private void fetchGroupMembers(String uniqueID) {
        databaseReference.child(uniqueID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.child("group").exists()){
                    pleaseWork.setText("-");
                } else{
                    databaseReference.child(uniqueID).child("group").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                numberOfMem = numberOfMem+1;
                                group = group + dataSnapshot.getValue().toString() + "\n";
                            }
                            pleaseWork.setText(group);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}