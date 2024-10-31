package com.example.transpomate;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreenActivity extends AppCompatActivity {

    private static final int SPLASH_DISPLAY_LENGTH = 2000; // 2 seconds delay

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        FirebaseAuth.getInstance().signOut(); // Force logout for testing

        new Handler().postDelayed(() -> {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
            } else {
                startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
            }
            finish();
        }, SPLASH_DISPLAY_LENGTH);
    }
}