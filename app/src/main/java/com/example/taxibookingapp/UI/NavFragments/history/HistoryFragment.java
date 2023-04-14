package com.example.taxibookingapp.UI.NavFragments.history;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.taxibookingapp.R;
import com.example.taxibookingapp.databinding.FragmentHistoryBinding;


public class HistoryFragment extends Fragment {

    FragmentHistoryBinding binding;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHistoryBinding.inflate(inflater, container, false);


        return binding.getRoot();
    }
}