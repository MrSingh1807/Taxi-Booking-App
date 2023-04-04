package com.example.taxibookingapp.UI.AuthenticateUser;

import android.app.AlertDialog;
import android.graphics.drawable.Animatable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Toast;

import com.example.taxibookingapp.R;
import com.example.taxibookingapp.databinding.FragmentPhoneLoginBinding;
import com.hbb20.CountryCodePicker;


public class PhoneLoginFragment extends Fragment {

    FragmentPhoneLoginBinding binding;
    private String SELECTED_COUNTRY_CODE = "+91";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPhoneLoginBinding.inflate(inflater, container, false);
        // Inflate the layout for this fragment

        //   Country Code Picker
        binding.countryCodePicker.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                SELECTED_COUNTRY_CODE = binding.countryCodePicker.getSelectedCountryCodeWithPlus();
            }
        });

        // EditText
        binding.contactNumberET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                if (charSequence.length() == 0)
                    Toast.makeText(requireContext(), "Enter Your 10 Digit Mobile Number", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (charSequence.length() == 10) {
                    Toast.makeText(requireContext(), "OTP is sent to your Mobile Number ", Toast.LENGTH_SHORT).show();
                    binding.phoneLoginCLayout.animate()
                            .translationY(binding.phoneLoginCLayout.getHeight())
                            .alpha(0.0f)
                            .setDuration(500);
//                    binding.phoneLoginCLayout.setVisibility(View.GONE);
                    binding.firstPinView.animate()
                            .translationY(binding.firstPinView.getHeight())
                            .alpha(1.0f)
                            .setDuration(500);
                    binding.firstPinView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // Pin View
        binding.firstPinView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                if (charSequence.length() == 0)
                    Toast.makeText(requireContext(), "Please Enter Your OTP", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (charSequence.length() == 6)
                    Toast.makeText(requireContext(), "OTP Submitted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}