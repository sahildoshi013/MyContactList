package com.example.sahilj.mycontactlist.Utils;


import android.util.Patterns;

import com.example.sahilj.mycontactlist.model.Person;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

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
    public static final String PERSON = "person";
    public static final String IS_EDIT_MODE = "Is Edit Mode";
    private static String user;
    public static Person person;
    public static DocumentSnapshot personData;

    //check Login Status of using Firebase
    public static boolean isUserLogin() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user != null;
    }

    public static String getUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user != null ? user.getUid() : null;
    }

    public static  String getFullName(String firstName,String lastName) {

        String name;

        name = firstName;

        if(!name.isEmpty())
            name += " ";

        name+=lastName;
        return name;
    }

    public static String getContactNumber(List<String> mobileNumber) {

        if(mobileNumber!=null && mobileNumber.size()>0) {
            if (Patterns.PHONE.matcher(mobileNumber.get(0)).matches())
                return mobileNumber.get(0);

            if (Patterns.PHONE.matcher(mobileNumber.get(1)).matches())
                return mobileNumber.get(1);
        }
        return null;
    }

    public static String getFirstCharacter(String firstName, String lastName){
        Character firstChar ='0';
        if(firstName!=null && !firstName.isEmpty())
            firstChar = firstName.charAt(0);
        else if(lastName!=null && !lastName.isEmpty())
            firstChar = lastName.charAt(0);

        return (Character.isDigit(firstChar)) ? "#" : String.valueOf(firstChar);
    }
}
