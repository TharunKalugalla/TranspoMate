package com.example.transpomate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Spinner spinnerRoutes, spinnerBuses;
    private Button buttonViewBus;
    private DatabaseReference databaseReference;

    private Map<String, List<String>> routeBusMap = new HashMap<>();
    private Map<String, Bus> busDetailsMap = new HashMap<>();
    private List<String> routes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinnerRoutes = findViewById(R.id.spinnerRoutes);
        spinnerBuses = findViewById(R.id.spinnerBuses);
        buttonViewBus = findViewById(R.id.buttonViewBus);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        loadRoutes();

        spinnerRoutes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedRoute = routes.get(position);
                loadBusData(selectedRoute);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        buttonViewBus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedBus = (String) spinnerBuses.getSelectedItem();
                if (selectedBus != null && !selectedBus.isEmpty()) {
                    Bus bus = busDetailsMap.get(selectedBus);
                    if (bus != null) {
                        Intent intent = new Intent(MainActivity.this, BusDetailsActivity.class);
                        intent.putExtra("busInfo", bus.info);
                        intent.putExtra("busSeats", bus.seatsAvailable);
                        intent.putExtra("busLat", bus.location.lat);
                        intent.putExtra("busLng", bus.location.lng);
                        startActivity(intent);
                    } else {
                        Toast.makeText(MainActivity.this, "Invalid bus data", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Please select a bus", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadRoutes() {
        databaseReference.child("routes").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                routes.clear();
                for (DataSnapshot routeSnapshot : dataSnapshot.getChildren()) {
                    String route = routeSnapshot.getKey();
                    routes.add(route);
                }
                ArrayAdapter<String> routeAdapter = new ArrayAdapter<>(MainActivity.this,
                        android.R.layout.simple_spinner_item, routes);
                routeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerRoutes.setAdapter(routeAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Failed to load routes", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadBusData(String route) {
        databaseReference.child("buses").child(route).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> buses = new ArrayList<>();
                for (DataSnapshot busSnapshot : dataSnapshot.getChildren()) {
                    String bus = busSnapshot.getKey();
                    buses.add(bus);
                    Bus busDetails = busSnapshot.getValue(Bus.class);
                    busDetailsMap.put(bus, busDetails);
                }

                if (buses.isEmpty()) {
                    Toast.makeText(MainActivity.this, "No buses available for the selected route", Toast.LENGTH_SHORT).show();
                } else {
                    ArrayAdapter<String> busAdapter = new ArrayAdapter<>(MainActivity.this,
                            android.R.layout.simple_spinner_item, buses);
                    busAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerBuses.setAdapter(busAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Failed to load buses", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static class Bus {
        public String info;
        public String departureTime;
        public int seatsAvailable;
        public Location location;

        public static class Location {
            public double lat;
            public double lng;
        }
    }
}