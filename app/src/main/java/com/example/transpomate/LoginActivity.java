package com.example.transpomate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.concurrent.Executor;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText emailEditText, passwordEditText;
    private Button loginButton, biometricLoginButton;
    private SharedPreferences preferences;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        biometricLoginButton = findViewById(R.id.biometricLoginButton);
        preferences = getSharedPreferences("userPrefs", MODE_PRIVATE);

        // Login button onClick listener
        loginButton.setOnClickListener(v -> loginWithEmailPassword());

        // Biometric login button onClick listener
        biometricLoginButton.setOnClickListener(v -> checkBiometricSupportAndAuthenticate());

        // "Register Here" link to navigate to registration screen
        TextView registerHere = findViewById(R.id.registerHere);
        registerHere.setOnClickListener(v -> navigateToRegister());

        setupBiometricAuthentication();
    }

    private void loginWithEmailPassword() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Validate email format
        if (!isValidEmail(email)) {
            emailEditText.setError("Please enter a valid email address");
            return;
        }

        // Validate password presence
        if (password.isEmpty()) {
            passwordEditText.setError("Please enter your password");
            return;
        }

        // Firebase authentication
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Store login status
                        preferences.edit().putBoolean("isLoggedIn", true).apply();
                        Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                        navigateToMain();
                    } else {
                        Toast.makeText(LoginActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void setupBiometricAuthentication() {
        Executor executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(LoginActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(LoginActivity.this, "Biometric authentication successful", Toast.LENGTH_SHORT).show();
                navigateToMain();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(LoginActivity.this, "Biometric authentication failed", Toast.LENGTH_SHORT).show();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric Login")
                .setSubtitle("Log in using your biometric credential")
                .setNegativeButtonText("Cancel")
                .build();
    }

    private void checkBiometricSupportAndAuthenticate() {
        BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                biometricPrompt.authenticate(promptInfo);
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Toast.makeText(this, "Device doesn't support biometric authentication", Toast.LENGTH_SHORT).show();
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Toast.makeText(this, "Biometric hardware currently unavailable", Toast.LENGTH_SHORT).show();
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                Toast.makeText(this, "No biometric credentials enrolled", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(this, "Biometric authentication not supported", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void navigateToMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void navigateToRegister() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }
}
