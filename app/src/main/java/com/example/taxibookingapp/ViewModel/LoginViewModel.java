package com.example.taxibookingapp.ViewModel;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Patterns;
import android.widget.Toast;

import androidx.lifecycle.AndroidViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class LoginViewModel extends AndroidViewModel {
    private Application context;

    @Inject
    public LoginViewModel(Application application) {
        super(application);
        this.context = application;
    }

    public FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    public FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    public FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

    // FireBase Authentication
    public String uid = firebaseAuth.getUid();

    // Firebase RealtimeDatabase
    public DatabaseReference userFirebaseDB = firebaseDatabase.getReference().child("users");

    // Firebase Storage
    public StorageReference profilePicStorage = firebaseStorage.getReference().child("profile_pics");

    public boolean checkNetworkConnectivity() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }


    public Boolean validateEmail(String email) {
        if (email.isEmpty()) {
            Toast.makeText(context, "Please Enter an Email ID", Toast.LENGTH_SHORT).show();

        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            String text = "Please Provide a valid Email ID";
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
        } else {
            return true;
        }
        return false;
    }

    public Boolean validatePassword(String passWord) {

        if (passWord.isEmpty()) {
            Toast.makeText(context, "Please Enter password", Toast.LENGTH_SHORT).show();

        } else if (passWord.length() < 6) {
            String text = "Password does't less than 6";
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
        } else {
            return true;
        }
        return false;
    }

}
