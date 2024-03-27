package com.app.linkedlisting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private EditText emailEditText, passwordEditText;
    private Button loginButton, signupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance(); // Initialize Firestore

        emailEditText = findViewById(R.id.usernameOrEmailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        signupButton = findViewById(R.id.signupButton);

        loginButton.setOnClickListener(v -> loginUser());
        signupButton.setOnClickListener(v -> {
            Intent signupIntent = new Intent(SplashActivity.this, SignUpActivity.class);
            startActivity(signupIntent);
        });

        autoLogin();
    }

    private void autoLogin() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            navigateToMainActivity();
        }
    }

    private void loginUser() {
        String input = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (input.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter both email/username and password.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (input.contains("@")) {
            signInWithEmail(input, password);
        } else {
            lookupEmailFromUsername(input, password);
        }
    }

    private void lookupEmailFromUsername(String username, String password) {
        mFirestore.collection("usernames").document(username).get().addOnSuccessListener(documentSnapshot -> {
            String email = documentSnapshot.getString("email"); // Make sure "email" matches the field in Firestore
            if (email != null) {
                signInWithEmail(email, password);
            } else {
                Toast.makeText(SplashActivity.this, "Username not found.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> Toast.makeText(SplashActivity.this, "Error checking username.", Toast.LENGTH_SHORT).show());
    }

    private void signInWithEmail(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                navigateToMainActivity();
            } else {
                handleAuthFailure(task);
            }
        });
    }

    private void handleAuthFailure(@NonNull Task<AuthResult> task) {
        String errorMessage = "Authentication failed."; // Default message

        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
            errorMessage = "Invalid password.";
        } else if (task.getException() instanceof FirebaseAuthInvalidUserException) {
            errorMessage = "Invalid email or user does not exist.";
        }

        Toast.makeText(SplashActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    private void navigateToMainActivity() {
        Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
}
