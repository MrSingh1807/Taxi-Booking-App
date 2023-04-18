package com.example.taxibookingapp.UI;

import static com.example.taxibookingapp.Services.Constants.ACTION_START_LOCATION_SERVICE;
import static com.example.taxibookingapp.Services.Constants.ACTION_STOP_LOCATION_SERVICE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.taxibookingapp.R;
import com.example.taxibookingapp.Services.LocationService;
import com.example.taxibookingapp.SessionManager;
import com.example.taxibookingapp.ViewModel.LoginViewModel;
import com.example.taxibookingapp.databinding.ActivityHomeBinding;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HomeActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    private ActivityHomeBinding binding;
    private AppBarConfiguration mAppBarConfiguration;
    SessionManager sessionManager;
    private long back_pressed;
    NavController navController;
    LoginViewModel loginViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        sessionManager = new SessionManager(this);


        if (loginViewModel.checkNetworkConnectivity()) {
            binding.drawerLayout.setVisibility(View.VISIBLE);
            binding.noInterNetLL.setVisibility(View.INVISIBLE);
        } else {
            binding.drawerLayout.setVisibility(View.INVISIBLE);
            binding.noInterNetLL.setVisibility(View.VISIBLE);
        }

        binding.btnRetry.setOnClickListener(view -> {
            if (loginViewModel.checkNetworkConnectivity()) {
                Toast.makeText(this, "Welcome! you're connected", Toast.LENGTH_SHORT).show();
                binding.drawerLayout.setVisibility(View.VISIBLE);
                binding.noInterNetLL.setVisibility(View.INVISIBLE);
            } else {
                Toast.makeText(this, "Please Connect your device with Internet", Toast.LENGTH_SHORT).show();
            }
        });

        setSupportActionBar(binding.appBarHome.toolbar);
        binding.appBarHome.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_setting, R.id.nav_profile, R.id.nav_history)
                .setOpenableLayout(drawer)
                .build();

        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
//        navigationView.setNavigationItemSelectedListener(menuItem -> {
//
//        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_setting:
                Toast.makeText(this, "Clicked", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_signOut:
                // later add a dialog box here
                sessionManager.spEditor.clear();
                sessionManager.spEditor.commit();
                loginViewModel.firebaseAuth.signOut();
                Toast.makeText(this, "SuccessFully LogOut", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(intent);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (back_pressed + 100 > System.currentTimeMillis()) {
            super.onBackPressed();
        } else {
            Toast.makeText(this, "Press Once again to exit!", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isLocationServiceRunning() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            for (ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)) {
                if (LocationService.class.getName().equals(service.service.getClassName())) {
                    if (service.foreground) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void startLocationService() {
        if (!isLocationServiceRunning()) {
            Intent startServiceIntent = new Intent(getApplicationContext(), LocationService.class);
            startServiceIntent.setAction(ACTION_START_LOCATION_SERVICE);
            startService(startServiceIntent);
        }
    }

    public void stopLocationService() {
        if (isLocationServiceRunning()) {
            Intent startServiceIntent = new Intent(getApplicationContext(), LocationService.class);
            startServiceIntent.setAction(ACTION_STOP_LOCATION_SERVICE);
            stopService(startServiceIntent);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_LOCATION_PERMISSION);
        } else {
            startLocationService();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopLocationService();
    }

}