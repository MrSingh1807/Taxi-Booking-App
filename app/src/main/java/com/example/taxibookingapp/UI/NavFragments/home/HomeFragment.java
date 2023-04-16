package com.example.taxibookingapp.UI.NavFragments.home;

import static android.app.Activity.RESULT_CANCELED;
import static android.content.ContentValues.TAG;
import static android.content.Context.LOCATION_SERVICE;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.HandlerThread;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.taxibookingapp.R;
import com.example.taxibookingapp.ViewModel.MainViewModel;
import com.example.taxibookingapp.databinding.FragmentHomeBinding;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class HomeFragment extends Fragment implements OnMapReadyCallback {

    public static final String PermissionTAG = "PermissionTAG";
    public static final int PLAY_SERVICES_ERROR_CODE = 9002;
    private GoogleMap mGoogleMap;

    private FusedLocationProviderClient mLocationClient;
    private LocationCallback mLocationCallbacks;
    private FragmentHomeBinding binding;

    private HandlerThread mHandler;
    private MainViewModel mainViewModel;

    private final ActivityResultLauncher<String[]> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),
                    permission -> permission.forEach((key, value) ->
                            Log.d(PermissionTAG, "TEST " + key + " " + value)));

    private final ActivityResultLauncher<Intent> enableLocationLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        LocationManager locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
                        Boolean providerEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

                        if (providerEnabled) {
                            Toast.makeText(requireContext(), "GPS is enabled", Toast.LENGTH_SHORT).show();
                        } else if (result.getResultCode() == RESULT_CANCELED) {
                            Toast.makeText(requireContext(), "GPS is not enabled", Toast.LENGTH_SHORT).show();
                        }
                    });

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);


        mLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        mLocationCallbacks = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                Toast.makeText(
                        requireContext(),
                        "Location is: \nLatitude -> ${location!!.longitude} \nLongitude -> ${location.longitude}",
                        Toast.LENGTH_SHORT
                ).show();

                assert location != null;
                Log.d(TAG, "Location is: Latitude -> " + location.getLatitude() + "Longitude -> " + location.getLongitude());
            }
        };

        initGoogleMap();
        return binding.getRoot();
    }

    private boolean requestGPSEnabled() {
        LocationManager locationManager = (LocationManager) requireActivity().getSystemService(LOCATION_SERVICE);
        Boolean provideEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (provideEnabled) {
            return true;
        } else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(requireContext())
                    .setTitle("GPS Permission")
                    .setMessage("GPS is required, to access current location")
                    .setPositiveButton("Yes", (dialogInterface, i) -> {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        enableLocationLauncher.launch(intent);
                    });
            dialog.show();
        }
        return false;
    }

    private boolean checkLocationPermission() {
        return ContextCompat.checkSelfPermission(requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean isServiceOK() {
        GoogleApiAvailability googleApi = GoogleApiAvailability.getInstance();
        int result = googleApi.isGooglePlayServicesAvailable(requireContext());

        if (result == ConnectionResult.SUCCESS) {
            return true;
        } else if (googleApi.isUserResolvableError(result)) {
            Dialog dialog = googleApi.getErrorDialog(requireActivity(), result, PLAY_SERVICES_ERROR_CODE,
                    dialogInterface -> Toast.makeText(requireContext(), "Dialog is Cancelled By USER", Toast.LENGTH_SHORT).show());
            assert dialog != null;
            dialog.show();
        } else {
            Toast.makeText(
                    requireContext(),
                    "Play Services are required by this Application",
                    Toast.LENGTH_SHORT
            ).show();
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private void initGoogleMap() {
        if (isServiceOK()) {
            if (checkLocationPermission()) {
                Toast.makeText(requireContext(), "Ready to Map!", Toast.LENGTH_SHORT).show();

                SupportMapFragment supportMapFragment = SupportMapFragment.newInstance();
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .add(R.id.mapContainerViewFL, supportMapFragment)
                        .commit();
                supportMapFragment.getMapAsync(this);
            } else {
                requestLocationPermission();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private void requestLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requestPermissionLauncher.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.FOREGROUND_SERVICE,
                    Manifest.permission.POST_NOTIFICATIONS
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mGoogleMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng delhi = new LatLng(28.7041, 77.1025);
        googleMap.addMarker(new MarkerOptions().position(delhi).title("Marker in Delhi"));
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(delhi, 5F);
        googleMap.moveCamera(cameraUpdate);

    }

    @Override
    public void onPause() {
        super.onPause();
        if (mLocationCallbacks != null) {
            mLocationClient.removeLocationUpdates(mLocationCallbacks);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.quit();
    }
}