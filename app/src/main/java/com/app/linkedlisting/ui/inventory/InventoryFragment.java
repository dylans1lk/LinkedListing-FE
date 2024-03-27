// This is the INVENTORY FRAGMENT aka the definition of the inventory page's display/layout
// Here we convert the corresponding XML file into the corresponding ViewModel objects.
package com.app.linkedlisting.ui.inventory;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.app.linkedlisting.databinding.FragmentInventoryBinding;

public class InventoryFragment extends Fragment {

    private FragmentInventoryBinding binding;

    // This section helps establish the fragment's UI via updating/initializing
    // the layout and the ViewModel
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        InventoryViewModel homeViewModel =
                new ViewModelProvider(this).get(InventoryViewModel.class);

        binding = FragmentInventoryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Changes the text that's being displayed based on any changes in the LiveData
        final TextView textView = binding.textInventory;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}