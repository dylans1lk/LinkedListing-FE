package com.app.linkedlisting.ui.inventory;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import android.util.Log;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.app.linkedlisting.ui.listings.AddItemDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import com.app.linkedlisting.databinding.FragmentInventoryBinding;  // Updated import to use FragmentInventoryBinding
import com.app.linkedlisting.ui.listings.ListingAdapter;

import java.util.List;

public class InventoryFragment extends Fragment {

    private FragmentInventoryBinding binding;  // Updated binding type to match the new layout file
    private InventoryAdapter inventoryAdapter;
    private List<InventoryItem> inventoryItems;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentInventoryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        setupRecyclerView();
        loadInventoryItems();

        binding.addItemButton.setOnClickListener(view -> showAddInventoryItemDialog());
        return root;
    }

    public void setupRecyclerView() {
        binding.inventoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        inventoryAdapter = new InventoryAdapter(getContext(), inventoryItems);
        binding.inventoryRecyclerView.setAdapter(inventoryAdapter);
    }

    public void loadInventoryItems() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users").document(userId).collection("Inventory").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    inventoryItems.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        InventoryItem item = document.toObject(InventoryItem.class);
                        inventoryItems.add(item);
                    }
                    inventoryAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreLoadError", "Failed to load inventory", e);
                });
    }

    private void showAddInventoryItemDialog() {
        FragmentManager fragmentManager = getParentFragmentManager();
        AddInventoryItemDialogFragment addItemDialog = new AddInventoryItemDialogFragment();
        addItemDialog.show(fragmentManager, "addItemDialog");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;  // Properly cleanup the binding to avoid memory leaks
    }
}
