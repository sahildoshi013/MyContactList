package com.example.sahilj.mycontactlist.Utils;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by Sahil J on 3/19/2018.
 */

public class MyUtilities {


    //check Login Status of using Firebase
    public static boolean isUserLogin() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user != null;
    }

}
