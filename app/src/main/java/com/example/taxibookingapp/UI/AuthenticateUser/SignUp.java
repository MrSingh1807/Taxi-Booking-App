package com.example.taxibookingapp.UI.AuthenticateUser;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.example.taxibookingapp.Models.User;
import com.example.taxibookingapp.R;
import com.example.taxibookingapp.SessionManager;
import com.example.taxibookingapp.UI.HomeActivity;
import com.example.taxibookingapp.ViewModel.LoginViewModel;
import com.example.taxibookingapp.databinding.FragmentSignUpBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;

import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;


@AndroidEntryPoint
public class SignUp extends Fragment {

    FragmentSignUpBinding binding;
    LoginViewModel loginViewModel;
    Animation animate;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        animate = AnimationUtils.loadAnimation(requireContext(), R.anim.right_animation);
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSignUpBinding.inflate(inflater, container, false);
        // Animate All Views
        binding.welcomeTV.setAnimation(animate);
        binding.rideTV.setAnimation(animate);
        binding.personalCredentialsCV.setAnimation(animate);
        binding.cardView2.setAnimation(animate);
        binding.view.setAnimation(animate);
        binding.phoneSignUpBTN.setAnimation(animate);
        binding.googleSignUpBTN.setAnimation(animate);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.forSignInTV.setOnClickListener(it -> {
            NavDirections action = SignUpDirections.actionSignUpToSignIn();
            Navigation.findNavController(it).navigate(action);
        });
        binding.phoneSignUpBTN.setOnClickListener(it -> {
            NavDirections action = SignUpDirections.actionSignUpToPhoneLoginFragment();
            Navigation.findNavController(it).navigate(action);
        });
        binding.signUpBTN.setOnClickListener(it -> {
            String userName = binding.userNameET.getText().toString().trim();
            String userPhone = binding.userPhoneET.getText().toString().trim();
            String userEmail = binding.userEmailET.getText().toString().trim();
            String userPassword = binding.userPasswordET.getText().toString().trim();

            if (!userName.isEmpty() || !userPhone.isEmpty()) {
                if (loginViewModel.validateEmail(userEmail) && loginViewModel.validatePassword(userPassword)) {
                    loginViewModel.firebaseAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                            .addOnSuccessListener(authResult -> {
                                User user = new User(userName,userEmail,userPhone,userPassword);
                                String uid = Objects.requireNonNull(authResult.getUser()).getUid();

                                loginViewModel.userFirebaseDB.child(uid).setValue(user);
                                // save  user details & token in shared Preference

                                Toast.makeText(requireContext(), "You have successfully Login", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(requireActivity(), HomeActivity.class);
                                startActivity(intent);
                            });

                }
            } else {
                Toast.makeText(requireContext(), "All the field are mandatory", Toast.LENGTH_SHORT).show();
            }
        });
    }
}