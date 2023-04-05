package com.example.taxibookingapp.UI.AuthenticateUser;

import static android.app.Activity.RESULT_OK;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.taxibookingapp.databinding.FragmentPhoneLoginBinding;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.CredentialsApi;
import com.google.android.gms.auth.api.identity.GetPhoneNumberHintIntentRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.common.api.ApiException;
import com.hbb20.CountryCodePicker;


public class PhoneLoginFragment extends Fragment {

    FragmentPhoneLoginBinding binding;
    private String SELECTED_COUNTRY_CODE = "+91";
    private static final int CREDENTIAL_PICKER_REQUEST = 120;
    public static final String TAG = "MeriTaxi";
    ActivityResultLauncher<IntentSenderRequest> phoneNumberHintIntentResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartIntentSenderForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result != null && result.getData() != null) {
                        if (result.getResultCode() == RESULT_OK) {
                            String phoneNumber;
                            try {
                                phoneNumber = Identity.getSignInClient(requireActivity()).getPhoneNumberFromIntent(result.getData());
                                if (phoneNumber.contains("+91")){
                                    phoneNumber = phoneNumber.replace("+91", "");
                                }
                            } catch (ApiException e) {
                                throw new RuntimeException(e);
                            }
                            Toast.makeText(requireContext(), "MOB " + phoneNumber, Toast.LENGTH_SHORT).show();
                            binding.contactNumberET.setText(phoneNumber);

                        } else {
                            Toast.makeText(requireContext(), "No phone numbers found", Toast.LENGTH_LONG).show();
                        }

                    }
                }
            });


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPhoneLoginBinding.inflate(inflater, container, false);
        // Inflate the layout for this fragment

        /* ************  Country Code Picker  ************** */
        binding.countryCodePicker.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                SELECTED_COUNTRY_CODE = binding.countryCodePicker.getSelectedCountryCodeWithPlus();
            }
        });

        /* ************  EditText  ************** */
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
                    /*
                    binding.phoneLoginCLayout.animate()
                            .translationY(binding.phoneLoginCLayout.getHeight())
                            .alpha(0.0f)
                            .setDuration(500);
                    binding.phoneLoginCLayout.setVisibility(View.GONE);
                    binding.firstPinView.animate()
                            .translationY(binding.firstPinView.getHeight())
                            .alpha(1.0f)
                            .setDuration(500);
                    binding.firstPinView.setVisibility(View.VISIBLE);
                     */
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        /* ************  Pin View  ************** */
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

        /* ************   Auto Phone Select  ************** */
        /*  HintRequest hintRequest = new HintRequest.Builder()
                .setPhoneNumberIdentifierSupported(true)
                .build();


        PendingIntent intent = Credentials.getClient(requireContext()).getHintPickerIntent(hintRequest);
        try
        {
            startIntentSenderForResult(intent.getIntentSender(), CREDENTIAL_PICKER_REQUEST, null, 0, 0, 0,new Bundle());
        }
        catch (IntentSender.SendIntentException e)
        {
            e.printStackTrace();
        }
         */
        GetPhoneNumberHintIntentRequest hintRequest = GetPhoneNumberHintIntentRequest.builder().build();
        Identity.getSignInClient(requireActivity())
                .getPhoneNumberHintIntent(hintRequest)
                .addOnSuccessListener(pendingIntent -> {
                    phoneNumberHintIntentResultLauncher.launch(
                            new IntentSenderRequest.Builder(pendingIntent.getIntentSender()).build()
                    );
                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, e.getMessage());
                });


        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}