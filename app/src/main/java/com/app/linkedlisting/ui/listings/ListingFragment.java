package com.app.linkedlisting.ui.listings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.linkedlisting.databinding.FragmentListingsBinding; // Updated import
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ListingFragment extends Fragment {

    private FragmentListingsBinding binding; // Updated binding type
    private ListingAdapter listingAdapter;
    private List<ListingItem> inventoryItems = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentListingsBinding.inflate(inflater, container, false); // Updated method call
        View root = binding.getRoot();

        setupRecyclerView();
        loadInventoryItems();

        binding.addItemButton.setOnClickListener(view -> showAddItemDialog());

        return root;
    }

    private void setupRecyclerView() {
        binding.inventoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        listingAdapter = new ListingAdapter(getContext(), inventoryItems);
        binding.inventoryRecyclerView.setAdapter(listingAdapter);
    }

    private void loadInventoryItems() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users").document(userId).collection("Inventory")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    inventoryItems.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        ListingItem item = document.toObject(ListingItem.class);
                        inventoryItems.add(item);
                    }
                    listingAdapter.notifyDataSetChanged();
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
        binding = null; // Correctly nullify the binding to avoid memory leaks
    }
}
