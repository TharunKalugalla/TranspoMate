package com.example.transpomate;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.os.Handler;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BusLocationActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker busMarker;
    private DatabaseReference databaseReference;
    private String busRoute, busId;
    private Handler handler;
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_location);

        // Retrieve data from intent
        busRoute = getIntent().getStringExtra("busRoute");
        busId = getIntent().getStringExtra("busId");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("buses").child(busRoute).child(busId).child("location");

        handler = new Handler();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Initial location update
        updateBusLocation();

        // Schedule continuous location updates
        runnable = new Runnable() {
            @Override
            public void run() {
                updateBusLocation();
                handler.postDelayed(this, 500); // Update location every 500 milliseconds
            }
        };
        handler.postDelayed(runnable, 500);
    }

    private void updateBusLocation() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                double lat = dataSnapshot.child("lat").getValue(Double.class);
                double lng = dataSnapshot.child("lng").getValue(Double.class);

                LatLng busLatLng = new LatLng(lat, lng);
                if (busMarker == null) {
                    busMarker = mMap.addMarker(new MarkerOptions().position(busLatLng).title("Bus Location"));
                } else {
                    busMarker.setPosition(busLatLng);
                }
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(busLatLng, 15));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors.
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable); // Stop updating location when activity is destroyed
    }
}