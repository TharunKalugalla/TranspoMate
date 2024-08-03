package com.example.transpomate;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
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
    private DatabaseReference busLocationRef;
    private Marker busMarker;
    private Circle accuracyCircle;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable locationUpdater = new Runnable() {
        @Override
        public void run() {
            busLocationRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        try {
                            Double lat = dataSnapshot.child("location/lat").getValue(Double.class);
                            Double lng = dataSnapshot.child("location/lng").getValue(Double.class);
                            Float bearing = dataSnapshot.child("location/bearing").getValue(Float.class);
                            Float accuracy = dataSnapshot.child("location/accuracy").getValue(Float.class);

                            if (lat != null && lng != null && bearing != null && accuracy != null) {
                                updateBusLocation(new LatLng(lat, lng), bearing, accuracy);
                            } else {
                                Log.e("BusLocationActivity", "Location data is null");
                            }
                        } catch (Exception e) {
                            Log.e("BusLocationActivity", "Exception caught: " + e.getMessage(), e);
                        }
                    } else {
                        Log.e("BusLocationActivity", "DataSnapshot does not exist");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(BusLocationActivity.this, "Failed to get bus location", Toast.LENGTH_SHORT).show();
                }
            });
            handler.postDelayed(this, 500); // Schedule the next execution
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_location);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        String busRoute = getIntent().getStringExtra("busRoute");
        String busId = getIntent().getStringExtra("busId");

        if (busId == null || busRoute == null) {
            Log.e("BusLocationActivity", "busId or busRoute is null");
            return;
        }

        busLocationRef = FirebaseDatabase.getInstance().getReference().child("buses").child(busRoute).child(busId);
    }

    @Override
    protected void onStart() {
        super.onStart();
        handler.post(locationUpdater); // Start updating the location when the activity starts
    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.removeCallbacks(locationUpdater); // Stop updating the location when the activity stops
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }

    private void updateBusLocation(LatLng latLng, float bearing, float accuracy) {
        if (mMap == null) return;

        // Create resized bitmap for the bus marker
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.custom_bus_marker);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 50, 100, false);

        if (busMarker == null) {
            busMarker = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title("Bus Location")
                    .icon(BitmapDescriptorFactory.fromBitmap(resizedBitmap))
                    .flat(true)
                    .anchor(0.5f, 0.5f));
        } else {
            busMarker.setPosition(latLng);
            busMarker.setRotation(bearing);
        }

        if (accuracyCircle == null) {
            accuracyCircle = mMap.addCircle(new CircleOptions()
                    .center(latLng)
                    .radius(accuracy * 50) // Adjust the multiplier as needed for visibility
                    .strokeColor(Color.argb(50, 255, 0, 0))
                    .fillColor(Color.argb(50, 255, 0, 0)));
        } else {
            accuracyCircle.setCenter(latLng);
            accuracyCircle.setRadius(accuracy * 50);
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
    }
}
