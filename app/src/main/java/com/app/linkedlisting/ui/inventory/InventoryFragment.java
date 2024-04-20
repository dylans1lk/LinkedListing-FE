package com.app.linkedlisting.ui.inventory;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.app.linkedlisting.databinding.FragmentInventoryBinding;  // Updated import to use FragmentInventoryBinding

public class InventoryFragment extends Fragment {

    private FragmentInventoryBinding binding;  // Updated binding type to match the new layout file

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        InventoryViewModel inventoryViewModel =
                new ViewModelProvider(this).get(InventoryViewModel.class);  // Ensure correct ViewModel usage

        binding = FragmentInventoryBinding.inflate(inflater, container, false);  // Updated to use FragmentInventoryBinding
        View root = binding.getRoot();

        final TextView textView = binding.textInventory;  // Update ID based on actual ID in fragment_inventory.xml
        inventoryViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;  // Properly cleanup the binding to avoid memory leaks
    }
}
