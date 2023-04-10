package com.example.taxibookingapp.UI;


import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.example.taxibookingapp.R;
import com.example.taxibookingapp.SessionManager;
import com.example.taxibookingapp.ViewModel.LoginViewModel;
import com.example.taxibookingapp.databinding.ActivitySplashBinding;
import com.google.firebase.auth.FirebaseAuth;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SplashActivity extends AppCompatActivity {

    ActivitySplashBinding binding;
    SessionManager sessionManager;
    LoginViewModel loginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        sessionManager = new SessionManager(this);

        // Hide Status Bar
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (loginViewModel.firebaseAuth.getCurrentUser() != null) {
                    Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    String uid = loginViewModel.firebaseAuth.getCurrentUser().getUid();
                    intent.putExtra("UID", uid);

                    startActivity(intent);
                } else {
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    startActivity(intent);

                }
            }
        }, 3500);

        binding.appNameTV.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.up_animation));

    }
}