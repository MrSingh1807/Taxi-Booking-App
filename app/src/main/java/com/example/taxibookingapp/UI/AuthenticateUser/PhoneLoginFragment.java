package com.example.taxibookingapp.UI.AuthenticateUser;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.airbnb.lottie.Lottie;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieComposition;
import com.example.taxibookingapp.UI.HomeActivity;
import com.example.taxibookingapp.databinding.FragmentPhoneLoginBinding;
import com.google.android.gms.auth.api.identity.GetPhoneNumberHintIntentRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.Objects;
import java.util.concurrent.TimeUnit;


public class PhoneLoginFragment extends Fragment {

    FragmentPhoneLoginBinding binding;
    private String SELECTED_COUNTRY_CODE = "+91";
    private static final int CREDENTIAL_PICKER_REQUEST = 120;
    public static final String TAG = "MeriTaxi";

    /* ************  Firebase Phone Auth  ************** */
    private String mVerificationID;
    private PhoneAuthProvider.ForceResendingToken mResentToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks phoneAuthVerificationStateChangeCallbacks;
    private FirebaseAuth firebaseAuth;
    /* ************  Firebase Phone Auth  ************** */

    ActivityResultLauncher<IntentSenderRequest> phoneNumberHintIntentResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartIntentSenderForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result != null && result.getData() != null) {
                        if (result.getResultCode() == RESULT_OK) {
                            String phoneNumber;
                            try {
                                phoneNumber = Identity.getSignInClient(requireActivity()).getPhoneNumberFromIntent(result.getData());
                                if (phoneNumber.contains("+91")) {
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
    }

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
                if (charSequence.length() == 10)
                    sendOTP();
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
                if (charSequence.length() == 6) {
                    binding.progressBar.setVisibility(View.VISIBLE);

                    PhoneAuthCredential authCredential =
                            PhoneAuthProvider.getCredential(mVerificationID, Objects.requireNonNull(binding.firstPinView.getText()).toString());
                    signInWithAuthCredential(authCredential);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        /* ************   Auto Phone Select  ************** */
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

        /* ************   OTP Callbacks  ************** */
        phoneAuthVerificationStateChangeCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                String code = phoneAuthCredential.getSmsCode();
                if (code != null) {
                    binding.firstPinView.setText(code);
                    signInWithAuthCredential(phoneAuthCredential);
                }
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(requireContext(), "Something Went Wrong...", Toast.LENGTH_SHORT).show();

                setVisibility(View.GONE, View.VISIBLE, View.GONE);
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);

                mVerificationID = s;
                mResentToken = forceResendingToken;

                Toast.makeText(requireContext(), "6 Digit OTP sent", Toast.LENGTH_SHORT).show();
                setVisibility(View.GONE, View.GONE, View.VISIBLE);
            }
        };

        return binding.getRoot();
    }

    private void signInWithAuthCredential(PhoneAuthCredential phoneAuthCredential) {
        firebaseAuth.signInWithCredential(phoneAuthCredential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(requireContext(), "Logged In SuccessFully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(requireActivity(), HomeActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(requireContext(), "LogIn Failed", Toast.LENGTH_SHORT).show();

                            Navigation.findNavController(binding.contactNumberET).popBackStack();

                        }
                    }
                });
    }

    private void sendOTP() {
        binding.progressBar.setVisibility(View.VISIBLE);

        String phoneNumber = SELECTED_COUNTRY_CODE + binding.contactNumberET.getText();
        PhoneAuthOptions authOptions = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setPhoneNumber(phoneNumber)
                .setActivity(requireActivity())
                .setCallbacks(phoneAuthVerificationStateChangeCallbacks)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(authOptions);
    }

    private void setVisibility(int progressBar, int cl, int pinView) {
        binding.progressBar.setVisibility(progressBar);
        binding.phoneLoginCLayout.setVisibility(cl);
        binding.firstPinView.setVisibility(pinView);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}