package com.example.taxibookingapp.UI.AuthenticateUser;

import android.app.Notification;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.airbnb.lottie.Lottie;
import com.example.taxibookingapp.R;
import com.example.taxibookingapp.databinding.FragmentSignInBinding;


public class SignIn extends Fragment {
    FragmentSignInBinding binding;
    Animation phoneBTNAnimation, googleBTNAnimate;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        phoneBTNAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.right_animation);
        googleBTNAnimate = AnimationUtils.loadAnimation(requireContext(), R.anim.right_animation);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSignInBinding.inflate(inflater, container, false);

        binding.phoneBTN.setAnimation(phoneBTNAnimation);
        binding.googleBTN.setAnimation(googleBTNAnimate);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.phoneBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavDirections action = SignInDirections.actionSignInToPhoneLoginFragment();
                Navigation.findNavController(view).navigate(action);
            }
        });

    }
}