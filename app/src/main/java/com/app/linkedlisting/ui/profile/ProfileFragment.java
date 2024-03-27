package com.app.linkedlisting.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button; // Import Button class

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.app.linkedlisting.databinding.FragmentProfileBinding;
import com.google.firebase.auth.FirebaseAuth; // Import FirebaseAuth

import com.app.linkedlisting.SplashActivity;
import com.app.linkedlisting.R;


public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ProfileViewModel profileViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textProfile;
        profileViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        // Find the logout button by its ID and set a click listener
        Button logoutButton = root.findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(view -> {
            // Logout the user
            FirebaseAuth.getInstance().signOut();

            // Redirect to SplashActivity or your designated login screen
            startActivity(new Intent(getActivity(), SplashActivity.class));

            // To ensure the user cannot navigate back to the profile screen after logging out
            getActivity().finish();
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}