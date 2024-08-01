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

    private Map<String, String> routeMap = new HashMap<>();
    private Map<String, List<Bus>> routeBusMap = new HashMap<>();
    private List<String> routeDisplayList = new ArrayList<>();
    private List<String> busDisplayList = new ArrayList<>();
    private List<Bus> selectedRouteBuses = new ArrayList<>();

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
                String selectedRouteKey = routeDisplayList.get(position).split(" - ")[0];
                loadBusData(selectedRouteKey);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        buttonViewBus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedBusPosition = spinnerBuses.getSelectedItemPosition();
                if (selectedBusPosition >= 0 && selectedBusPosition < selectedRouteBuses.size()) {
                    Bus bus = selectedRouteBuses.get(selectedBusPosition);
                    Intent intent = new Intent(MainActivity.this, BusDetailsActivity.class);
                    intent.putExtra("busRoute", routeDisplayList.get(spinnerRoutes.getSelectedItemPosition()).split(" - ")[0]);
                    intent.putExtra("busId", bus.id);
                    intent.putExtra("busInfo", bus.info);
                    intent.putExtra("busSeats", bus.seatsAvailable);
                    intent.putExtra("busLat", bus.location.lat);
                    intent.putExtra("busLng", bus.location.lng);
                    startActivity(intent);
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
                routeMap.clear();
                routeDisplayList.clear();
                for (DataSnapshot routeSnapshot : dataSnapshot.getChildren()) {
                    String routeKey = routeSnapshot.getKey();
                    String routeName = routeSnapshot.getValue(String.class);
                    routeMap.put(routeKey, routeName);
                    routeDisplayList.add(routeKey + " - " + routeName);
                }
                ArrayAdapter<String> routeAdapter = new ArrayAdapter<>(MainActivity.this,
                        android.R.layout.simple_spinner_item, routeDisplayList);
                routeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerRoutes.setAdapter(routeAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Failed to load routes", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadBusData(String routeKey) {
        databaseReference.child("buses").child(routeKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                selectedRouteBuses.clear();
                busDisplayList.clear();
                for (DataSnapshot busSnapshot : dataSnapshot.getChildren()) {
                    Bus bus = busSnapshot.getValue(Bus.class);
                    bus.id = busSnapshot.getKey();  // Store the bus ID
                    selectedRouteBuses.add(bus);
                    busDisplayList.add(bus.departureTime + " - " + bus.info);
                }

                if (busDisplayList.isEmpty()) {
                    Toast.makeText(MainActivity.this, "No buses available for the selected route", Toast.LENGTH_SHORT).show();
                } else {
                    ArrayAdapter<String> busAdapter = new ArrayAdapter<>(MainActivity.this,
                            android.R.layout.simple_spinner_item, busDisplayList);
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
        public String id; // Add this field to store the bus ID
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
