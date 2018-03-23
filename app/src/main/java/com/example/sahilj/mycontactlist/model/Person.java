package com.example.sahilj.mycontactlist.model;

import android.util.Patterns;

import com.google.firebase.firestore.GeoPoint;
import com.hbb20.CountryCodePicker;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Sahil J on 3/22/2018.
 */

public class Person implements Serializable {

    private String firstName;
    private String lastName;
    private List<String> mobileNumber;
    private String emailID;
    private String address;
    private GeoPoint location;

    public Person(){

    }

    public Person(String firstName, String lastName, List<String> mobileNumber, String emailID, String address, GeoPoint location) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.mobileNumber = mobileNumber;
        this.emailID = emailID;
        this.address = address;
        this.location = location;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<String> getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(List<String> mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getEmailID() {
        return emailID;
    }

    public void setEmailID(String emailID) {
        this.emailID = emailID;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

}
