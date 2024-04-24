package com.app.linkedlisting;

import android.content.Intent;
import android.widget.EditText;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.test.core.app.ApplicationProvider;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doReturn;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.AuthResult;

public class SignUpActivityTest {

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private FirebaseAuth mockAuth;
    @Mock
    private FirebaseFirestore mockFirestore;
    @Mock
    private FirebaseUser mockUser;
    private Task<AuthResult> successfulAuthResultTask;

    private SignUpActivity activity;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        activity = new SignUpActivity();

        // Using the setters to inject mock objects
        activity.setAuth(mockAuth);
        activity.setFirestore(mockFirestore);

        // Setting up EditTexts with a context for testing
        activity.setUsernameEditText(new EditText(ApplicationProvider.getApplicationContext()));
        activity.setEmailEditText(new EditText(ApplicationProvider.getApplicationContext()));
        activity.setPasswordEditText(new EditText(ApplicationProvider.getApplicationContext()));

        // Prepare mocked Task for a successful scenario
        successfulAuthResultTask = Tasks.forResult(mock(AuthResult.class));
        doReturn(successfulAuthResultTask).when(mockAuth).fetchSignInMethodsForEmail(anyString());
        when(mockAuth.createUserWithEmailAndPassword(anyString(), anyString())).thenReturn(successfulAuthResultTask);

        // Assume getCurrentUser() will return a non-null FirebaseUser
        when(mockAuth.getCurrentUser()).thenReturn(mockUser);
        when(mockUser.getUid()).thenReturn("123456");
    }

    @Test
    public void testRegisterUser_WithEmptyFields() {
        activity.getUsernameEditText().setText("");
        activity.getEmailEditText().setText("");
        activity.getPasswordEditText().setText("");
        activity.registerUser();

        // Assertions for toast would go here, possibly using Espresso
    }

    @Test
    public void testRegisterUser_WithEmailAlreadyInUse() {
        activity.getEmailEditText().setText("test@example.com");
        activity.getPasswordEditText().setText("password123");
        activity.getUsernameEditText().setText("testUser");
        activity.registerUser();

        // Assertions for toast would go here, possibly using Espresso
    }

    @Test
    public void testRegisterUser_SuccessfulRegistration() {
        activity.getEmailEditText().setText("test@example.com");
        activity.getPasswordEditText().setText("password123");
        activity.getUsernameEditText().setText("testUser");
        activity.registerUser();

        // Assert successful registration and navigation to MainActivity
        verify(activity).startActivity(new Intent(activity, MainActivity.class));
    }
}
