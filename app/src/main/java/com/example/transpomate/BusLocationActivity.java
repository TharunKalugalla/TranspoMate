package com.example.transpomate;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.transpomate.databinding.ActivityBusLocationBinding;

public class BusLocationActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityBusLocationBinding binding;
    private double busLat, busLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityBusLocationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        busLat = getIntent().getDoubleExtra("busLat", 0);
        busLng = getIntent().getDoubleExtra("busLng", 0);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker at the bus location and move the camera
        LatLng busLocation = new LatLng(busLat, busLng);
        mMap.addMarker(new MarkerOptions().position(busLocation).title("Bus Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(busLocation, 15));
    }
}