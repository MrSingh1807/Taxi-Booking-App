package com.example.taxibookingapp.UI.AuthenticateUser;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.example.taxibookingapp.R;
import com.example.taxibookingapp.SessionManager;
import com.example.taxibookingapp.UI.HomeActivity;
import com.example.taxibookingapp.ViewModel.LoginViewModel;
import com.example.taxibookingapp.databinding.FragmentSignInBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;

import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SignIn extends Fragment {
    FragmentSignInBinding binding;
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
        // Inflate the layout for this fragment
        binding = FragmentSignInBinding.inflate(inflater, container, false);

        binding.phoneSignInBTN.setAnimation(animate);
        binding.googleSignInBTN.setAnimation(animate);
        binding.personalCredentialsCV.setAnimation(animate);
        binding.welcomeTV.setAnimation(animate);
        binding.rideTV.setAnimation(animate);
        binding.cardView2.setAnimation(animate);
        binding.view.setAnimation(animate);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.forSignUpTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavDirections action = SignInDirections.actionSignInToSignUp();
                Navigation.findNavController(view).navigate(action);
            }
        });

        binding.phoneSignInBTN.setOnClickListener(view1 -> {
            NavDirections action = SignInDirections.actionSignInToPhoneLoginFragment();
            Navigation.findNavController(view1).navigate(action);
        });

        binding.signInBTN.setOnClickListener(view2 -> {
            String userEmail = binding.signInEmailET.getText().toString().trim();
            String userPass = binding.singInPasswordET.getText().toString().trim();

            loginViewModel.firebaseAuth.signInWithEmailAndPassword(userEmail, userPass)
                    .addOnSuccessListener(authResult -> {
                        Snackbar.make(view2,"You are successfully SignIn", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();

                        Intent intent = new Intent(requireActivity(), HomeActivity.class);
                        intent.putExtra("Email", userEmail);
                        startActivity(intent);
                    });
        });

    }
}