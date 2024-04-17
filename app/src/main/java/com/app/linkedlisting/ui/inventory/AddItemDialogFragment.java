package com.app.linkedlisting.ui.inventory;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.util.Log;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Toast;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.app.linkedlisting.databinding.FragmentAddInventoryDialogBinding;

import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AlertDialog;

public class AddItemDialogFragment extends DialogFragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int STORAGE_PERMISSION_CODE = 2;
    private FragmentAddInventoryDialogBinding binding;
    private Uri imageUri = null;  // Initialize to null

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        binding = FragmentAddInventoryDialogBinding.inflate(LayoutInflater.from(getContext()));
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setView(binding.getRoot())
                .setTitle("Add New Item")
                .setPositiveButton("Add", (dialog, id) -> attemptSaveItem())
                .setNegativeButton("Cancel", (dialog, id) -> dismiss());

        binding.uploadImageButton.setOnClickListener(v -> openImageChooser());
        return builder.create();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            binding.itemImagePreview.setImageURI(imageUri);
        }
    }

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openImageChooser();
        } else {
            Toast.makeText(getContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }

    private void attemptSaveItem() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (imageUri != null) {
            StorageReference fileRef = FirebaseStorage.getInstance().getReference("images/" + userId + "/" + UUID.randomUUID().toString() + ".jpg");
            fileRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        saveItemDetails(uri.toString());  // Pass the URL to save details
                    }).addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Error obtaining image URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }))
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Error uploading image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            saveItemDetails("");  // Save without an image URL
        }
    }

    private void saveItemDetails(String imageUrl) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (user == null) {
            Log.e("FirestoreSave", "User is not logged in.");
            Toast.makeText(getContext(), "User is not logged in.", Toast.LENGTH_SHORT).show();
            return; // Stop further execution if the user is not logged in
        }

        String userId = user.getUid();

        if (isAdded() && binding != null) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", binding.itemName.getText().toString());
            item.put("condition", binding.itemCondition.getText().toString());
            item.put("dateListed", binding.dateListed.getText().toString());
            item.put("description", binding.itemDescription.getText().toString());
            item.put("price", binding.itemPrice.getText().toString());
            item.put("image", imageUrl); // Store image URL under the key "image"

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("Users").document(userId).collection("Inventory")
                    .add(item)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(getContext(), "Item added successfully", Toast.LENGTH_SHORT).show();
                        Log.d("FirestoreSave", "Document added with ID: " + documentReference.getId());
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Error adding item: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("FirestoreSave", "Error adding document", e);
                    });
        } else {
            Log.e("FirestoreSave", "Fragment not attached or binding is null");
        }
    }




    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
