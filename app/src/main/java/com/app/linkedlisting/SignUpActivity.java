package com.app.linkedlisting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
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

    public EditText getUsernameEditText() {
        return usernameEditText;
    }

    public void setUsernameEditText(EditText usernameEditText) {
        this.usernameEditText = usernameEditText;
    }

    public EditText getEmailEditText() {
        return emailEditText;
    }

    public void setEmailEditText(EditText emailEditText) {
        this.emailEditText = emailEditText;
    }

    public EditText getPasswordEditText() {
        return passwordEditText;
    }

    public void setPasswordEditText(EditText passwordEditText) {
        this.passwordEditText = passwordEditText;
    }

    public FirebaseAuth getAuth() {
        return mAuth;
    }

    public void setAuth(FirebaseAuth mAuth) {
        this.mAuth = mAuth;
    }

    public FirebaseFirestore getFirestore() {
        return mFirestore;
    }

    public void setFirestore(FirebaseFirestore mFirestore) {
        this.mFirestore = mFirestore;
    }

    public void registerUser() {
        String username = getUsernameEditText().getText().toString().trim();
        String email = getEmailEditText().getText().toString().trim();
        String password = getPasswordEditText().getText().toString().trim();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(SignUpActivity.this, "Please fill all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        getAuth().fetchSignInMethodsForEmail(email)
                .addOnCompleteListener(task -> {
                    boolean isNewUser = task.getResult().getSignInMethods().isEmpty();
                    if (isNewUser) {
                        createUserAccount(email, password, username);
                    } else {
                        Toast.makeText(SignUpActivity.this, "Email already in use. Please use a different email.", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(SignUpActivity.this, "Failed to check email: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void createUserAccount(String email, String password, String username) {
        getAuth().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = getAuth().getCurrentUser();
                        if (firebaseUser != null) {
                            String userId = firebaseUser.getUid();
                            User user = new User(username, email, userId);
                            getFirestore().collection("Users").document(userId).set(user)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(SignUpActivity.this, "Registration successful.", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(SignUpActivity.this, "Failed to save user data.", Toast.LENGTH_SHORT).show());
                        }
                    } else {
                        String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();
                        handleSignUpError(errorCode);
                    }
                });
    }

    private void handleSignUpError(String errorCode) {
        String message;
        switch (errorCode) {
            case "ERROR_EMAIL_ALREADY_IN_USE":
                message = "The email address is already in use by another account.";
                break;
            default:
                message = "Authentication failed.";
                break;
        }
        Toast.makeText(SignUpActivity.this, message, Toast.LENGTH_SHORT).show();
    }

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
