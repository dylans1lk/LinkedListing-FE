package com.app.linkedlisting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignUpActivity extends AppCompatActivity {

    private EditText usernameEditText, emailEditText, passwordEditText;
    private Button signupButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance(); // Initialize Firestore

        usernameEditText = findViewById(R.id.usernameEditText);
        emailEditText = findViewById(R.id.emailSignUpEditText);
        passwordEditText = findViewById(R.id.passwordSignUpEditText);
        signupButton = findViewById(R.id.signUpButton);

        signupButton.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String username = usernameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(SignUpActivity.this, "Please fill all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if username is unique
        mFirestore.collection("usernames").document(username).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Toast.makeText(SignUpActivity.this, "Username already taken.", Toast.LENGTH_SHORT).show();
                } else {
                    // Username is unique, proceed with registration
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(SignUpActivity.this, task1 -> {
                                if (task1.isSuccessful()) {
                                    // Save username in Firestore
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    if (user != null) {
                                        String userId = user.getUid();
                                        mFirestore.collection("usernames").document(username).set(new User(username, userId))
                                                .addOnSuccessListener(aVoid -> {
                                                    Toast.makeText(SignUpActivity.this, "Registration successful.", Toast.LENGTH_SHORT).show();
                                                    // Navigate to MainActivity or Login
                                                    Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                })
                                                .addOnFailureListener(e -> Toast.makeText(SignUpActivity.this, "Failed to save username.", Toast.LENGTH_SHORT).show());
                                    }
                                } else {
                                    Toast.makeText(SignUpActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            } else {
                Toast.makeText(SignUpActivity.this, "Failed to check username uniqueness.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Assuming you have a User class to structure the data for Firestore
    class User {
        public String username;
        public String userId;

        User(String username, String userId) {
            this.username = username;
            this.userId = userId;
        }
    }
}
