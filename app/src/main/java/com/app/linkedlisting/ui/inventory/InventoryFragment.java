package com.app.linkedlisting.ui.inventory;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.linkedlisting.databinding.FragmentInventoryBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class InventoryFragment extends Fragment {

    private FragmentInventoryBinding binding;
    private InventoryAdapter inventoryAdapter;
    private List<InventoryItem> inventoryItems = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentInventoryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        setupRecyclerView();
        loadInventoryItems();

        binding.addItemButton.setOnClickListener(view -> showAddItemDialog());

        return root;
    }

    private void setupRecyclerView() {
        binding.inventoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        inventoryAdapter = new InventoryAdapter(getContext(), inventoryItems);
        binding.inventoryRecyclerView.setAdapter(inventoryAdapter);
    }

    private void loadInventoryItems() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users").document(userId).collection("Inventory")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    inventoryItems.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        InventoryItem item = document.toObject(InventoryItem.class);
                        inventoryItems.add(item);
                    }
                    inventoryAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    // Log or handle any errors here.
                });
    }

    private void showAddItemDialog() {
        FragmentManager fragmentManager = getParentFragmentManager();
        AddItemDialogFragment addItemDialog = new AddItemDialogFragment();
        addItemDialog.show(fragmentManager, "AddItemDialog");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
