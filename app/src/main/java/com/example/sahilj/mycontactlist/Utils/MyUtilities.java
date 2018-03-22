package com.example.sahilj.mycontactlist.Utils;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by Sahil J on 3/19/2018.
 */

public class MyUtilities {


    public static final String DB_FIRST_NAME = "firstName";
    public static final String DB_LAST_NAME = "lastName";
    public static final String DB_MOBILE_NUMBERS = "mobileNumber";
    public static final String DB_EMAIL_ID = "emailID";
    public static final String DB_ADDRESS = "address";
    public static final String DB_LOCATION = "location";
    private static String user;

    //check Login Status of using Firebase
    public static boolean isUserLogin() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user != null;
    }

    public static String getUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user != null ? user.getUid() : null;
    }

}
