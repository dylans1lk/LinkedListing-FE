package com.app.linkedlisting.ui.inventory;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import com.app.linkedlisting.databinding.FragmentAddInventoryDialogBinding;
import android.util.Log;


public class AddItemDialogFragment extends DialogFragment {
    private static final int PICK_IMAGE_REQUEST = 1;
    private FragmentAddInventoryDialogBinding binding;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        binding = FragmentAddInventoryDialogBinding.inflate(LayoutInflater.from(getContext()));
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setView(binding.getRoot())
                .setTitle("Add New Item")
                .setPositiveButton("Add", (dialog, id) -> {
                    if (binding != null) {
                        attemptSaveItem();  // Directly call without imageUrl initially
                    }
                })
                .setNegativeButton("Cancel", (dialog, id) -> dismiss());
        binding.uploadImageButton.setOnClickListener(v -> openImageChooser());
        return builder.create();
    }

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            Glide.with(this)
                    .load(selectedImageUri)
                    .into(binding.itemImagePreview);
            uploadImageToFirebaseStorage(selectedImageUri);
        }
    }
    private void uploadImageToFirebaseStorage(Uri imageUri) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        final StorageReference imageRef = storageRef.child("images/" + UUID.randomUUID().toString());
        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    Log.d("UploadImage", "Image uploaded successfully");
                    // As soon as the image is uploaded successfully, get the download URL.
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        Log.d("DownloadUrl", "Image URL: " + imageUrl); // Logging the URL
                        if (isAdded()) {  // Check if the fragment is still attached
                            Glide.with(this).load(uri).into(binding.itemImagePreview);
                            // Directly update Firestore with the new image URL
                            updateFirestore(imageUrl);
                        }
                    }).addOnFailureListener(e -> {
                        Log.e("DownloadUrlError", "Failed to get download URL: " + e.getMessage());
                        safelyShowToast("Failed to get download URL: " + e.getMessage());
                    });
                })
                .addOnFailureListener(e -> {
                    Log.e("UploadError", "Failed to upload image: " + e.getMessage());
                    safelyShowToast("Failed to upload image: " + e.getMessage());
                });
    }

    private void updateFirestore(String imageUrl) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && imageUrl != null && !imageUrl.isEmpty()) {
            String userId = user.getUid();
            Map<String, Object> item = new HashMap<>();
            item.put("name", binding.itemName.getText().toString());
            item.put("condition", binding.itemCondition.getText().toString());
            item.put("dateListed", binding.dateListed.getText().toString());
            item.put("description", binding.itemDescription.getText().toString());
            item.put("price", binding.itemPrice.getText().toString());
            item.put("imageUrl", imageUrl);

            FirebaseFirestore.getInstance().collection("Users").document(userId).collection("Inventory")
                    .add(item)
                    .addOnSuccessListener(documentReference -> {
                        Log.d("FirestoreSave", "Item added successfully with Image URL: " + imageUrl);
                        safelyShowToast("Item added successfully");
                    })
                    .addOnFailureListener(e -> {
                        Log.e("FirestoreError", "Error adding item: " + e.getMessage());
                        safelyShowToast("Error adding item: " + e.getMessage());
                    });
        } else {
            Log.e("FirestoreError", "User not logged in or invalid image URL");
            safelyShowToast("User not logged in or invalid image URL");
        }
    }

    private void attemptSaveItem(String imageUrl) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            Map<String, Object> item = new HashMap<>();
            item.put("name", binding.itemName.getText().toString());
            item.put("condition", binding.itemCondition.getText().toString());
            item.put("dateListed", binding.dateListed.getText().toString());
            item.put("description", binding.itemDescription.getText().toString());
            item.put("price", binding.itemPrice.getText().toString());
            item.put("imageUrl", imageUrl);

            FirebaseFirestore.getInstance()
                    .collection("Users").document(userId).collection("Inventory")
                    .add(item)
                    .addOnSuccessListener(documentReference -> {
                        Log.d("FirestoreSave", "Item added successfully with Image URL: " + imageUrl);
                        safelyShowToast("Item added successfully");
                    })
                    .addOnFailureListener(e -> {
                        Log.e("FirestoreError", "Error adding item: " + e.getMessage());
                        safelyShowToast("Error adding item: " + e.getMessage());
                    });
        } else {
            safelyShowToast("User is not logged in.");
        }
    }

    private void attemptSaveItem() {
        attemptSaveItem(null);
    }

    private void safelyShowToast(String message) {
        if (isAdded() && getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Correctly nullify the binding to avoid memory leaks
    }
}