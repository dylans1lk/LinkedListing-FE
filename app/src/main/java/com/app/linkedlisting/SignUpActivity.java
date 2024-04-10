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

        // Directly proceed with registration, no need to check for username uniqueness
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignUpActivity.this, task -> {
                    if (task.isSuccessful()) {
                        // Save user data in Firestore under the 'Users' collection
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            String userId = firebaseUser.getUid();
                            User user = new User(username, email, userId);
                            mFirestore.collection("Users").document(userId).set(user)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(SignUpActivity.this, "Registration successful.", Toast.LENGTH_SHORT).show();
                                        // Navigate to MainActivity or Login
                                        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(SignUpActivity.this, "Failed to save user data.", Toast.LENGTH_SHORT).show());
                        }
                    } else {
                        Toast.makeText(SignUpActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Updated User class to structure the data for Firestore
    class User {
        public String username;
        public String email;
        public String userId;

        User(String username, String email, String userId) {
            this.username = username;
            this.email = email;
            this.userId = userId;
        }
    }
}
