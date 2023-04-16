package com.example.taxibookingapp.UI;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.taxibookingapp.ViewModel.LoginViewModel;
import com.example.taxibookingapp.databinding.ActivityLoginBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;
    LoginViewModel loginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        if (loginViewModel.checkNetworkConnectivity()) {
            binding.authUserNavHost.setVisibility(View.VISIBLE);
            binding.noInterNetLL.setVisibility(View.INVISIBLE);
        } else {
            binding.authUserNavHost.setVisibility(View.INVISIBLE);
            binding.noInterNetLL.setVisibility(View.VISIBLE);
        }

        binding.btnRetry.setOnClickListener(view -> {
            if (loginViewModel.checkNetworkConnectivity()) {
                Toast.makeText(this, "Welcome! you're connected", Toast.LENGTH_SHORT).show();
                binding.authUserNavHost.setVisibility(View.VISIBLE);
                binding.noInterNetLL.setVisibility(View.INVISIBLE);
            } else {
                Toast.makeText(this, "Please Connect your device with Internet", Toast.LENGTH_SHORT).show();
            }
        });
    }
}