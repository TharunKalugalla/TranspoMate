package com.example.transpomate;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class BusDetailsActivity extends AppCompatActivity {

    private TextView textViewBusDetails;
    private TextView textViewSeatsAvailable;
    private Button buttonViewBusLocation;
    private String busDetails;
    private int seatsAvailable;
    private double latitude;
    private double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_details);

        textViewBusDetails = findViewById(R.id.textViewBusDetails);
        textViewSeatsAvailable = findViewById(R.id.textViewSeatsAvailable);
        buttonViewBusLocation = findViewById(R.id.buttonViewBusLocation);

        busDetails = getIntent().getStringExtra("busDetails");
        seatsAvailable = Integer.parseInt(getIntent().getStringExtra("seatsAvailable"));
        latitude = getIntent().getDoubleExtra("latitude", 0);
        longitude = getIntent().getDoubleExtra("longitude", 0);

        textViewBusDetails.setText(busDetails);
        textViewSeatsAvailable.setText("Seats Available: " + seatsAvailable);

        buttonViewBusLocation.setOnClickListener(v -> {
            Intent intent = new Intent(BusDetailsActivity.this, MapsActivity.class);
            intent.putExtra("latitude", latitude);
            intent.putExtra("longitude", longitude);
            startActivity(intent);
        });
    }
}