package com.example.taxibookingapp.Services;

import static com.example.taxibookingapp.Services.Constants.ACTION_START_LOCATION_SERVICE;
import static com.example.taxibookingapp.Services.Constants.ACTION_STOP_LOCATION_SERVICE;
import static com.example.taxibookingapp.Services.Constants.LOCATION_SERVICE_ID;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.taxibookingapp.R;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

public class LocationService extends Service {
    private static final String LOCATION_TAG = "location";
    NotificationCompat.Builder nmBuilder;
    private final LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);

            if (locationResult.getLastLocation() != null) {
                double latitude = locationResult.getLastLocation().getLatitude();
                double longitude = locationResult.getLastLocation().getLongitude();

                Log.d(LOCATION_TAG, "Location: Latitude - " + latitude + "\n" + "Longitude - " + longitude);
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        createNotification();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not Yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if (action != null) {
                if (action.equals(ACTION_START_LOCATION_SERVICE)) {
                    startLocationService();
                } else if (action.equals(ACTION_STOP_LOCATION_SERVICE)) {
                    stopLocationService();
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public void createNotification() {
        String channel_id = "Location_Notification_Channel";
        NotificationManager notificationManager = (NotificationManager) getSystemService(NotificationManager.class);

        Intent resultIntent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(
                getApplicationContext(),
                0,
                resultIntent,
                PendingIntent.FLAG_IMMUTABLE
        );

        nmBuilder = new NotificationCompat.Builder(
                getApplicationContext(),
                channel_id);
        nmBuilder.setSmallIcon(R.drawable.notification_logo)
                .setContentTitle("Meri Taxi is accessing your Location")
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setContentText("Running")
                .setContentIntent(pendingIntent)
                .setAutoCancel(false)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel = new NotificationChannel(
                        channel_id,
                        "Meri Taxi Service",
                        NotificationManager.IMPORTANCE_DEFAULT);

                notificationChannel.setDescription("This Channel Used By Location Service.");
                notificationManager.createNotificationChannel(notificationChannel);
            }
    }

    @SuppressLint("MissingPermission")
    public void startLocationService() {
        LocationRequest mLocationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 2000)
                .setIntervalMillis(4000)
                .build();

        LocationServices.getFusedLocationProviderClient(this)
                .requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.getMainLooper());
        startForeground(LOCATION_SERVICE_ID, nmBuilder.build());
    }

    public void stopLocationService() {
        LocationServices.getFusedLocationProviderClient(this)
                .removeLocationUpdates(mLocationCallback);
        stopForeground(true);
        stopSelf();
    }
}
