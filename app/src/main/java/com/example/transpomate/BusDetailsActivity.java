package com.example.transpomate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class BusDetailsActivity extends AppCompatActivity {

    private TextView textViewBusInfo, textViewSeatsAvailable;
    private Button buttonViewBusLocation, buttonReserveSeat;

    private String busRoute, busId;
    private double busLat, busLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_details);

        textViewBusInfo = findViewById(R.id.textViewBusDetails);
        textViewSeatsAvailable = findViewById(R.id.textViewSeatsAvailable);
        buttonViewBusLocation = findViewById(R.id.buttonViewBusLocation);
        buttonReserveSeat = findViewById(R.id.buttonReserveSeat);

        // Retrieve data from intent
        busRoute = getIntent().getStringExtra("busRoute");
        busId = getIntent().getStringExtra("busId");
        String busInfo = getIntent().getStringExtra("busInfo");
        int seatsAvailable = getIntent().getIntExtra("busSeats", 0);
        busLat = getIntent().getDoubleExtra("busLat", 0);
        busLng = getIntent().getDoubleExtra("busLng", 0);

        textViewBusInfo.setText(busInfo);
        textViewSeatsAvailable.setText(String.valueOf("Seats Available: " + seatsAvailable));

        buttonViewBusLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BusDetailsActivity.this, BusLocationActivity.class);
                intent.putExtra("busRoute", busRoute);
                intent.putExtra("busId", busId);
                startActivity(intent);
            }
        });

        buttonReserveSeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle seat reservation logic here
            }
        });
    }
}
