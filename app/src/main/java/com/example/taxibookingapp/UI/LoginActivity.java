package com.example.taxibookingapp.UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.taxibookingapp.R;
import com.example.taxibookingapp.SessionManager;
import com.example.taxibookingapp.ViewModel.LoginViewModel;
import com.example.taxibookingapp.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;

import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

    }
}