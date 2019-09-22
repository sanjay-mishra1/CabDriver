package com.example.sanjay.cabdriver.NewTrip;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sanjay.cabdriver.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.example.sanjay.cabdriver.TrackerService.carLoc;
import static java.security.AccessController.getContext;

public class NewTrip extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
     DatabaseReference databaseReference;
    private Trip trip;
    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_trip);
         databaseReference=  FirebaseDatabase.getInstance().getReference("TRIP/"+trip.getTripid());
         bottomNavigationView=findViewById(R.id.bottomNav);
         bottomNavigationView.setItemIconTintList(null);
         trip=new Trip();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        receive();

    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //focus on indore
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng( 22.7583833,75.9384289)));

    }
      void loadMap(){
          try {
              LatLng latLngPickup = trip.getUserLocation();

              LatLng latLngDest = trip.getDesinationLocation();
              mMap.addMarker(new MarkerOptions().position(latLngPickup));
              mMap.addMarker(new MarkerOptions().position(latLngDest));
              mMap.moveCamera(CameraUpdateFactory.newLatLng(latLngPickup));
          }catch (Exception e){}
      }

    void receive(){
        trip.setTripid(getIntent().getStringExtra("TRIPID"));
        loadData();
    }
    void loadData(){
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            trip=dataSnapshot.getValue(Trip.class);
            //extract user info
             FirebaseDatabase.getInstance().getReference("USERS/"+trip.getUserphone()).addListenerForSingleValueEvent(new ValueEventListener() {
                 @Override
                 public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                     trip.setUsername((String)dataSnapshot.child("USER_NAME").getValue());
                     trip.setUserImg((String)dataSnapshot.child("USER_IMG").getValue());
                     trip.setUserRating((String)dataSnapshot.child("USER_RATING").getValue());
                      loadMap();
                 }

                 @Override
                 public void onCancelled(@NonNull DatabaseError databaseError) {

                 }
             });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    void loadDataListner(){
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
             trip=   dataSnapshot.getValue(Trip.class);
             trip.setInfoFields(NewTrip.this);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (s.equalsIgnoreCase("cancel")){
                    Toast.makeText(NewTrip.this,"Trip is cancelled by the user",Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    void loadNavListener(){
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.contact:
                         callUser();
                         return true;
                    case R.id.user_location:
                        openLocInMap(carLoc,trip.getUserLocation());
                         return true;
                    case R.id.destination_location:
                        openLocInMap(trip.getUserLocation(),trip.getDesinationLocation());
                         return true;
                    case R.id.cancelRide:
                        cancelRide();
                        return true;
                }
                return false;
            }
        });
    }

    private void openLocInMap(LatLng fromLoc,LatLng toLoc) {
        Intent intent=new Intent(Intent.ACTION_VIEW,Uri.parse("https://map.google.com/maps?saddr="+fromLoc.latitude+","+fromLoc.longitude
                +"&daddr="+toLoc.latitude+","+toLoc.longitude));
        intent.setPackage("com.google.android.apps.maps");
        startActivity(intent);
    }

    private void cancelRide() {
    }



    private void callUser() {
             if (isHaveCallPermision()) {


                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse(trip.getUserphone()));
                startActivity(callIntent);
            }else{
                Toast.makeText(this,"Not have call permission",Toast.LENGTH_SHORT).show();
            }

    }
    public  boolean isHaveCallPermision() {
        if (Build.VERSION.SDK_INT >= 23) {
            boolean hasPermission = (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED);
            if (!hasPermission) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CALL_PHONE},
                        1);
                return false;
            }else{

                return true;
            }
        }else{
            return true;
        }

    }
    public void startTripOnClick(View view) {
         final View dialogView = View.inflate( this, R.layout.otp_dialog, null);
          final Dialog dialog = new Dialog(this);

         dialog.setCanceledOnTouchOutside(true);


        dialog.setContentView(dialogView);

        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_BACK) {
                    dialog.dismiss();

                    return true;
                }

                return false;
            }
        });
        final TextView wrongOTP=dialogView.findViewById(R.id.wrongOTP);
        final EditText otp1=dialogView.findViewById(R.id.OTP1);
        final EditText otp2=dialogView.findViewById(R.id.OTP2);
        final EditText otp3=dialogView.findViewById(R.id.OTP3);
        final EditText otp4=dialogView.findViewById(R.id.OTP4);
        dialogView.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
             }
        });
        dialogView.findViewById(R.id.otpDone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String otp=otp1.getText().toString()+otp2.getText().toString()+
                          otp3.getText().toString()+otp4.getText().toString();
            if (otp.equals(trip.getTripOTP())){
                bottomNavigationView.getMenu().removeItem(2);
                bottomNavigationView.getMenu().setGroupVisible(3,true);
            }else{
                wrongOTP.setVisibility(View.VISIBLE);
                Handler handler=new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        wrongOTP.setVisibility(View.INVISIBLE);
                    }
                },1500);
                otp1.setText("");
                otp2.setText("");
                otp3.setText("");
                otp4.setText("");
            }
            }
        });





        dialog.show();
      }
}
