package com.example.taxibookingapp;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SessionManager {

    SharedPreferences sharedPreferences;
    public SharedPreferences.Editor spEditor;
    public Context context;
    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "LOGIN";
    private static final String LOGIN = "IS_LOGIN";
    private static final String USER_ID = "USER_ID";
    private static final String USER_TYPE = "USER_TYPE";
    private static final String USER_PHONE = "USER_PHONE";
    private static final String USER_EMAIL = "USER_EMAIL";
    private static final String USER_TOKEN = "USER_TOKEN";
    private static final String COUNTRY_CODE = "COUNTRY_CODE";

    public SessionManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME,PRIVATE_MODE);
        spEditor = sharedPreferences.edit();
    }

    public void createSession(String user_id, String user_type, String user_phone, String country_code){

        spEditor.putBoolean(LOGIN, true);
        spEditor.putString(USER_ID, user_id);
        spEditor.putString(USER_TYPE, user_type);
        spEditor.putString(USER_PHONE, user_phone);
        spEditor.putString(COUNTRY_CODE, country_code);
        spEditor.apply();
    }


    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<>();
        user.put(USER_ID, sharedPreferences.getString(USER_ID, null));
        user.put(USER_TYPE, sharedPreferences.getString(USER_TYPE, null));
        user.put(USER_PHONE, sharedPreferences.getString(USER_PHONE, null));
        user.put(COUNTRY_CODE, sharedPreferences.getString(COUNTRY_CODE, null));
        user.put(USER_EMAIL, sharedPreferences.getString(USER_EMAIL, null));

        return user;
    }
}
