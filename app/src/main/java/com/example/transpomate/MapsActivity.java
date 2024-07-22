package com.example.transpomate;

import android.os.Bundle;




import androidx.fragment.app.FragmentActivity;




import com.google.android.gms.maps.CameraUpdateFactory;

import com.google.android.gms.maps.GoogleMap;

import com.google.android.gms.maps.OnMapReadyCallback;

import com.google.android.gms.maps.SupportMapFragment;

import com.google.android.gms.maps.model.LatLng;

import com.google.android.gms.maps.model.MarkerOptions;

import com.example.transpomate.databinding.ActivityMapsBinding;




public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {




    private GoogleMap mMap;

    private ActivityMapsBinding binding;

    private double latitude;

    private double longitude;




    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);




        binding = ActivityMapsBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());




        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()

                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);




        latitude = getIntent().getDoubleExtra("latitude", 0);

        longitude = getIntent().getDoubleExtra("longitude", 0);

    }




    /**

     * Manipulates the map once available.

     * This callback is triggered when the map is ready to be used.

     * This is where we can add markers or lines, add listeners or move the camera.

     * If Google Play services is not installed on the device, the user will be prompted to install

     * it inside the SupportMapFragment. This method will only be triggered once the user has

     * installed Google Play services and returned to the app.

     */

    @Override

    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;




        // Add a marker in the bus location and move the camera

        LatLng busLocation = new LatLng(latitude, longitude);

        mMap.addMarker(new MarkerOptions().position(busLocation).title("Bus Location"));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(busLocation, 15));

    }

}