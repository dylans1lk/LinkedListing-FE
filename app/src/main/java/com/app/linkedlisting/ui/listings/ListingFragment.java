package com.app.linkedlisting.ui.listings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.util.Log;
import com.app.linkedlisting.databinding.FragmentListingsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ListingFragment extends Fragment {

    private FragmentListingsBinding binding;
    private ListingAdapter listingAdapter;
    private List<ListingItem> listingItems = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentListingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        setupRecyclerView();
        loadListingItems();

        binding.addItemButton.setOnClickListener(view -> showAddItemDialog());

        return root;
    }

    private void setupRecyclerView() {
        binding.inventoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        listingAdapter = new ListingAdapter(getContext(), listingItems);
        binding.inventoryRecyclerView.setAdapter(listingAdapter);
    }

    private void loadListingItems() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users").document(userId).collection("Listings")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    listingItems.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        ListingItem item = document.toObject(ListingItem.class);
                        listingItems.add(item);
                    }
                    listingAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    // Ideally handle errors in a user-friendly way and log them.
                    Log.e("FirestoreLoadError", "Failed to load listings", e);
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
        binding = null;  // Preventing memory leaks by nullifying the binding when view is destroyed
    }
}
