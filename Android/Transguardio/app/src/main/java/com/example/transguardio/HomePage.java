package com.example.transguardio;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomePage extends AppCompatActivity {

    SupportMapFragment smf;
    FusedLocationProviderClient client;
    private GoogleMap googleMap1;
    private SearchView searchView;
    private ArrayList<ArrayList<Double>> twoDArrayList = new ArrayList<>();
    private ArrayList<ArrayList<Double>> finalArrayList = new ArrayList<>();
    private Double WOR;
    private Double NumRides;
    private Double MinStars;
    private Map<LatLng,Marker> markerMap = new HashMap<>();
    private ArrayList<Marker> sourceDest = new ArrayList<>();
    private String freq;
    String Gender="Male";
    BitmapDescriptor customMarker1;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://transguardioone-default-rtdb.firebaseio.com/").getReference("users");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Code to hide title and action bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_home_page);
        // Get PhoneNumber from Sign In and Sign Up Page
        Intent i = getIntent();
        String uniqueID = i.getExtras().getString("UniqueID","");
        freq = uniqueID;
        // Initialize variables for Google Maps
        smf = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
        client = LocationServices.getFusedLocationProviderClient(this);
        // Initializing Search View
        searchView =  (SearchView) findViewById(R.id.mapView2);
        // Initializing Markers
        ArrayList<Double> innerList1 = new ArrayList<>();
        innerList1.add(50.0);
        innerList1.add(3.0);
        innerList1.add(1.0);
        innerList1.add(12.811755);
        innerList1.add(80.193980);
        twoDArrayList.add(innerList1);
        ArrayList<Double> innerList2 = new ArrayList<>();
        innerList2.add(100.0);
        innerList2.add(4.0);
        innerList2.add(0.0);
        innerList2.add(12.805406);
        innerList2.add(80.192651);
        twoDArrayList.add(innerList2);
        ArrayList<Double> innerList3 = new ArrayList<>();
        innerList3.add(100.0);
        innerList3.add(3.5);
        innerList3.add(0.0);
        innerList3.add(12.808227);
        innerList3.add(80.191214);
        twoDArrayList.add(innerList3);
        ArrayList<Double> innerList4 = new ArrayList<>();
        innerList4.add(300.0);
        innerList4.add(4.5);
        innerList4.add(1.0);
        innerList4.add(12.808668);
        innerList4.add(80.186287);
        twoDArrayList.add(innerList4);
        ArrayList<Double> innerList5 = new ArrayList<>();
        innerList5.add(400.0);
        innerList5.add(5.0);
        innerList5.add(1.0);
        innerList5.add(12.802820);
        innerList5.add(80.193357);
        twoDArrayList.add(innerList5);
        // Check to see if filter is toggled
        Switch filter = findViewById(R.id.switch1);
        Switch trackMyRide = findViewById(R.id.switch3);
        Switch checkIn = findViewById(R.id.switch2);
        ImageButton myBtn = findViewById(R.id.bookRideBtn);
        ImageButton settingsButton = findViewById(R.id.imageButton5);
        ImageButton groupBtn = findViewById(R.id.imageButton2);
        groupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomePage.this, SecondLastPage.class);
                intent.putExtra("UniqueID", uniqueID);
                startActivity(intent);
            }
        });
        // Get Gender and Timer Limit
//        databaseReference.child(freq).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.child("Frequency").exists()) {
//                    Integer timer1 = Integer.parseInt(snapshot.child("Frequency").getValue().toString());
//                    timer = timer1 * 60000;
//                } else {
//                    timer = 40000;
//                }
//                if (snapshot.child("checkFreq").exists()) {
//                    Integer timer2 = Integer.parseInt(snapshot.child("CheckFreq").getValue().toString());
//                    timerFreq = timer2 * 1000;
//                } else {
//                    timerFreq = 40000;
//                }
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

        // Check Gender and Open Corresponding Page

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU){
            if (ContextCompat.checkSelfPermission(HomePage.this, Manifest.permission.POST_NOTIFICATIONS)!=PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(HomePage.this, new String[]{Manifest.permission.POST_NOTIFICATIONS},101);
            }
        }
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReference.child(uniqueID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child("gender").exists()) {
                            Gender=snapshot.child("gender").getValue().toString();
                        }
                        if (Gender.equals("Male")){
                            Intent intent = new Intent(HomePage.this,SettingsPage.class);
                            intent.putExtra("UniqueID",uniqueID);
                            startActivity(intent);
                        }else{
                            Intent intent1 = new Intent(HomePage.this,SettingsPageFemale.class);
                            intent1.putExtra("UniqueID",uniqueID);
                            startActivity(intent1);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

            }
        });
        myBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkIn.isChecked()&&!trackMyRide.isChecked()){
                    makeNotificationTimer();
                } else if(!checkIn.isChecked()&&trackMyRide.isChecked()){
                    makeNotificationWithText("Ride Confirmed", "Driver: Dhruv.H, 4.5 Stars, Toyata Corolla, TN25AN1640");
                } else if(!checkIn.isChecked()&&!trackMyRide.isChecked()){
                    Toast.makeText(HomePage.this, "Safety Warning: No Ride Safety Toggles are on", Toast.LENGTH_SHORT).show();
                } else{
                    makeNotificationWithText("Ride Confirmed", "Driver: Dhruv.H, 4.5 Stars, Toyata Corolla, TN25AN1640");
                }
            }
        });
        checkIn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!filter.isChecked() && !trackMyRide.isChecked() && !checkIn.isChecked()){
                    Toast.makeText(HomePage.this, "Safety Warning: All toggles are off.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        trackMyRide.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!filter.isChecked() && !trackMyRide.isChecked() && !checkIn.isChecked()){
                    Toast.makeText(HomePage.this, "Safety Warning: All toggles are off.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        filter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    databaseReference.child(uniqueID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.child("WOR").getValue().toString().equals("Yes")){
                                WOR=1.0;
                            }else{
                                WOR=0.0;
                            }
                            NumRides = Double.parseDouble(snapshot.child("MinRides").getValue().toString());
                            MinStars = Double.parseDouble(snapshot.child("MinStars").getValue().toString());
                            for (ArrayList<Double> innerList : twoDArrayList){
                        if(WOR==1.0){
                            if((NumRides<=innerList.get(0))&&(MinStars<=innerList.get(1))&&(innerList.get(2)==1.0)){
                                finalArrayList.add(innerList);
                            }
                        } else{
                            if((NumRides<=innerList.get(0))&&(MinStars<=innerList.get(1))){
                                finalArrayList.add(innerList);
                            }
                        }
                    }
                                        for (ArrayList<Double> i: twoDArrayList){
                                            LatLng newMarker = new LatLng(i.get(3),i.get(4));
                                            if (!finalArrayList.contains(i)){
                                                Log.d("WTF1",markerMap.toString());
                                                markerMap.get(newMarker).remove();
                                                markerMap.remove(newMarker);
                                            }
                                        }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                }
                else{
                    for (ArrayList<Double> innerList : twoDArrayList){
                        LatLng newMarker = new LatLng(innerList.get(3),innerList.get(4));
                        Log.d("OKAY",markerMap.toString());
                        if (!isMarkerPresent(newMarker)){
                            MarkerOptions markerOptions = new MarkerOptions().icon(customMarker1).position(newMarker).title(innerList.toString());
                            Marker markerToCheck1 = googleMap1.addMarker(markerOptions);
                            markerMap.put(newMarker,markerToCheck1);
                        }
                    }
                }
                if (!filter.isChecked() && !trackMyRide.isChecked() && !checkIn.isChecked()){
                    Toast.makeText(HomePage.this, "Safety Warning: All toggles are off.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                String location = searchView.getQuery().toString();
                List<Address> addressList = null;
                try{
                if (location!=null){
                    Geocoder geocoder = new Geocoder(HomePage.this);
                    try{
                        addressList = geocoder.getFromLocationName(location,1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Address address = addressList.get(0);
                    LatLng latlng = new LatLng(address.getLatitude(),address.getLongitude());
                    MarkerOptions markerOptions = new MarkerOptions().position(latlng).title("Drop Location!");
                    Marker destination = googleMap1.addMarker(markerOptions);
                    googleMap1.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng,15));
                    sourceDest.add(destination);
                }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(HomePage.this, "Location Not Found",Toast.LENGTH_SHORT).show();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        Dexter.withContext(getApplicationContext()).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                getMyLocation();
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }
    private boolean isMarkerPresent(LatLng latlng) {
        return markerMap.containsKey(latlng);
    }

    // Add Title and Text for Making a Notification
    public void makeNotificationWithText(String Title, String Content) {
        String channelID = "CHANNEL_ID_NOTIFICATION";
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(Title)
                .setAutoCancel(true)
                .setContentText(Content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = notificationManager.getNotificationChannel(channelID);
            if (notificationChannel == null) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                notificationChannel = new NotificationChannel(channelID, "Some Description", importance);
                notificationChannel.setLightColor(Color.GREEN);
                notificationChannel.enableVibration(true);
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }

        // Notify the notification manager with the initial notification
        notificationManager.notify(0, builder.build());
    }

    // Display User's Current Location
    private void getMyLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                smf.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                BitmapDescriptor customMarker = BitmapDescriptorFactory.fromResource(R.drawable.caricon);
                BitmapDescriptor customMarker2 = BitmapDescriptorFactory.fromResource(R.drawable.currentlocation);
                customMarker1 = customMarker;
                googleMap1 = googleMap;
//                LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
                LatLng latlng = new LatLng(12.809631, 80.193690);
                MarkerOptions markerOptions = new MarkerOptions().position(latlng).title("Your location!").icon(customMarker2);
                Marker sourceMarker = googleMap1.addMarker(markerOptions);
                googleMap1.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng,15));
                sourceDest.add(sourceMarker);
                    for (ArrayList<Double> innerList : twoDArrayList){
                        LatLng newMarker = new LatLng(innerList.get(3),innerList.get(4));
                        if (!isMarkerPresent(newMarker)){
                            MarkerOptions markerOptions1 = new MarkerOptions().position(newMarker).icon(customMarker).title(innerList.toString());
                            Marker markerToCheck = googleMap1.addMarker(markerOptions1);
                            markerMap.put(newMarker,markerToCheck);
                        } else{
                            markerMap.get(newMarker).remove();
                        }

                }
    }
});
            }
        });
    }

    //Display Notification Timer
    public void makeNotificationTimer() {
        String channelID = "CHANNEL_ID_NOTIFICATION";
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Safety Check")
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        // Start a countdown timer
        new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Update the notification text with the remaining time
                int seconds = (int) (millisUntilFinished / 1000);
                builder.setContentText("Timer: " + seconds + " seconds");

                // Notify the notification manager with the updated notification
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(0, builder.build());
            }
            @Override
            public void onFinish() {
                // Update the notification text when the countdown is finished
                builder.setContentText("Timer finished");

                // Notify the notification manager with the updated notification
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(0, builder.build());
            }
        }.start();
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = notificationManager.getNotificationChannel(channelID);
            if (notificationChannel == null) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                notificationChannel = new NotificationChannel(channelID, "Some Description", importance);
                notificationChannel.setLightColor(Color.GREEN);
                notificationChannel.enableVibration(true);
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
        // Notify the notification manager with the initial notification
        notificationManager.notify(0, builder.build());
    }
}