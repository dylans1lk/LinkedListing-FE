package com.app.linkedlisting.ui.inventory;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.app.linkedlisting.databinding.FragmentAddInventoryDialogBinding;

import java.util.HashMap;
import java.util.Map;

public class AddItemDialogFragment extends DialogFragment {
    private FragmentAddInventoryDialogBinding binding;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        binding = FragmentAddInventoryDialogBinding.inflate(LayoutInflater.from(getContext()));
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setView(binding.getRoot())
                .setTitle("Add New Item")
                .setPositiveButton("Add", (dialog, id) -> attemptSaveItem())
                .setNegativeButton("Cancel", (dialog, id) -> dismiss());
        return builder.create();
    }

    private void attemptSaveItem() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            Map<String, Object> item = new HashMap<>();
            item.put("name", binding.itemName.getText().toString());
            item.put("condition", binding.itemCondition.getText().toString());
            item.put("dateListed", binding.dateListed.getText().toString());
            item.put("description", binding.itemDescription.getText().toString());
            item.put("price", binding.itemPrice.getText().toString());

            FirebaseFirestore.getInstance()
                    .collection("Users").document(userId).collection("Inventory")
                    .add(item)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(getContext(), "Item added successfully", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Error adding item: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(getContext(), "User is not logged in.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
