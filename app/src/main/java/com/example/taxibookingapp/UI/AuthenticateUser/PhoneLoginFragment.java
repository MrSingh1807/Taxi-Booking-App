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
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.taxibookingapp.Models.User;
import com.example.taxibookingapp.SessionManager;
import com.example.taxibookingapp.UI.HomeActivity;
import com.example.taxibookingapp.ViewModel.LoginViewModel;
import com.example.taxibookingapp.databinding.FragmentPhoneLoginBinding;
import com.google.android.gms.auth.api.identity.GetPhoneNumberHintIntentRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.messaging.FirebaseMessaging;
import com.hbb20.CountryCodePicker;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PhoneLoginFragment extends Fragment {

    FragmentPhoneLoginBinding binding;
    private String SELECTED_COUNTRY_CODE = "+91";
    private static final int CREDENTIAL_PICKER_REQUEST = 120;
    LoginViewModel loginViewModel;
    public static final String TAG = "MeriTaxi";

    /* ************  Firebase Phone Auth  ************** */
    private String mVerificationID;
    private PhoneAuthProvider.ForceResendingToken mResentToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks phoneAuthVerificationStateChangeCallbacks;
    private FirebaseAuth firebaseAuth;
    private String deviceToken;
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
                            binding.userPhoneET.setText(phoneNumber);

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
        loginViewModel = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPhoneLoginBinding.inflate(inflater, container, false);

        /* ************  FCM Token  ************** */
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                return;
            }
            deviceToken = task.getResult();
            Log.d(TAG, "Device Token: "+ deviceToken);
        });

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
                }
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(requireContext(), "Something Went Wrong...", Toast.LENGTH_SHORT).show();

                setVisibility(View.GONE, View.VISIBLE, View.GONE);
            }

            @Override
            public void onCodeSent(@NonNull String verificationID, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(verificationID, forceResendingToken);

                mVerificationID = verificationID;
                mResentToken = forceResendingToken;

                Toast.makeText(requireContext(), "6 Digit OTP sent", Toast.LENGTH_SHORT).show();
                setVisibility(View.GONE, View.GONE, View.VISIBLE);
            }
        };

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.submitBTN.setOnClickListener(it -> {
            // here, Take user credentials & save it in Database also in Local
            PhoneAuthCredential authCredential =
                    PhoneAuthProvider.getCredential(mVerificationID, Objects.requireNonNull(binding.firstPinView.getText()).toString());
            signInWithAuthCredential(authCredential);
        });
    }

    private void signInWithAuthCredential(PhoneAuthCredential phoneAuthCredential) {
        firebaseAuth.signInWithCredential(phoneAuthCredential)
                .addOnSuccessListener(authResult -> {

                    saveUserDataInFirebaseDatabase(authResult);  // save data on Firebase storage
                    // later -> save data in SQLite
                    Toast.makeText(requireContext(), "User Logged In SuccessFully", Toast.LENGTH_SHORT).show();


                    Intent intent = new Intent(requireActivity(), HomeActivity.class);
                    startActivity(intent);
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(requireContext(), "LogIn Failed", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "LogIn Failed" + e.getMessage());
                    Navigation.findNavController(binding.contactNumberET).popBackStack();
                });
    }

    public void saveUserDataInFirebaseDatabase(AuthResult authResult) {
        String userName = binding.userNameET.getText().toString().trim();
        String userPhone = binding.userPhoneET.getText().toString().trim();
        String userEmail = binding.userEmailET.getText().toString().trim();
        String userPassword = binding.userPasswordET.getText().toString().trim();


        if (loginViewModel.validateEmail(userEmail) && loginViewModel.validatePassword(userPassword)) {
            if (!userName.isEmpty() && !userPhone.isEmpty()) {
                User user = new User(userName, userEmail, userPhone, userPassword);
                String uid = Objects.requireNonNull(authResult.getUser()).getUid();

                loginViewModel.userFirebaseDB.child(uid).setValue(user);

                // save  user details & token in shared Preference

                // Also link user with email & password login
             /*
                loginViewModel.firebaseAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                        .addOnSuccessListener(authResult1 -> {
                            Toast.makeText(requireContext(), "User Created Successfully", Toast.LENGTH_SHORT).show();
                      });
              */
            }
        }
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