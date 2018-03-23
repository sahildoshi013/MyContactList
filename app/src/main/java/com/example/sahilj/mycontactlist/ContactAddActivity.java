package com.example.sahilj.mycontactlist;

import android.*;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.sahilj.mycontactlist.Utils.MyUtilities;
import com.example.sahilj.mycontactlist.model.Person;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;
import com.hbb20.CountryCodePicker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class ContactAddActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "Contact Add Activity";
    private static final int PLACE_PICKER_REQUEST = 1;
    private static final int LOC_REQ_CODE = 2;
    private EditText etFirstNumber;
    private EditText etFirstName;
    private EditText etLastName;
    private EditText etSecondNuumber;
    private EditText etAddress;
    private EditText etEmail;
    private CountryCodePicker ccpFirstNumber;
    private CountryCodePicker ccpSecondNumber;
    private FirebaseFirestore db;
    private GoogleApiClient mGoogleApiClient;
    private View view;
    private GeoPoint geoPoint;
    private ImageView btnAddContact;
    private boolean isEditingMode;
    private String docId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_add);

        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etFirstNumber = findViewById(R.id.etFirstMobileNumber);
        etSecondNuumber = findViewById(R.id.etSecondMobileNumber);
        etEmail = findViewById(R.id.etEmailID);
        etAddress = findViewById(R.id.etAddress);
        ccpFirstNumber = findViewById(R.id.ccPicker1);
        ccpSecondNumber = findViewById(R.id.ccPicker2);
        view = findViewById(R.id.mainLayout);
        btnAddContact = findViewById(R.id.btnAddContact);


        //register edit text to ccpicker
        ccpFirstNumber.registerCarrierNumberEditText(etFirstNumber);
        ccpSecondNumber.registerCarrierNumberEditText(etSecondNuumber);

        ccpFirstNumber.setNumberAutoFormattingEnabled(true);
        ccpSecondNumber.setNumberAutoFormattingEnabled(true);

        if(getIntent().getBooleanExtra(MyUtilities.IS_EDIT_MODE,false)){
            isEditingMode = true;
            setDefaultData();
        }


        // Access a Cloud Firestore instance from your Activity
        db = FirebaseFirestore.getInstance();


        //Initialise Google api
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();
    }

    private void setDefaultData() {

        DocumentSnapshot data = MyUtilities.personData;
        Person person = data.toObject(Person.class);

        docId = data.getId();

        etFirstName.setText(person.getFirstName());
        etLastName.setText(person.getLastName());
        List<String> numbers = person.getMobileNumber();
        if(numbers.size()>=1)
            ccpFirstNumber.setFullNumber(numbers.get(0));
        if(numbers.size()==2)
            ccpSecondNumber.setFullNumber(numbers.get(1));
        etEmail.setText(person.getEmailID());
        etAddress.setText(person.getAddress());

    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }


    public void addContact(View view) {
        if(validData()){
            btnAddContact.setVisibility(View.GONE);
            addContactToFireBase();
        }
    }

    //Add contact to firebase
    private void addContactToFireBase() {
        String collectionID = MyUtilities.getUser();

        String firstName = etFirstName.getText().toString();
        String lastName = etLastName.getText().toString();
        List<String> numbers = new ArrayList<>(2);
        if(ccpFirstNumber.isValidFullNumber())
            numbers.add(ccpFirstNumber.getFullNumberWithPlus());
        if(ccpSecondNumber.isValidFullNumber())
            numbers.add(ccpSecondNumber.getFullNumberWithPlus());
        String email = etEmail.getText().toString();
        String address = etAddress.getText().toString();

        Person p = new Person(firstName,lastName,numbers,email,address,geoPoint);

        MyUtilities.person = p;

        Log.v(TAG,collectionID);
        if (collectionID != null) {
            CollectionReference dbRef = db.collection(collectionID);
            if(isEditingMode) {
                dbRef.document(docId).set(p).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(ContactAddActivity.this, "Contact Updated", Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish();
                        }else{
                            Log.v(TAG,task.getException().getMessage());
                            Toast.makeText(ContactAddActivity.this, "Oops!", Toast.LENGTH_SHORT).show();
                        }
                        btnAddContact.setVisibility(View.VISIBLE);
                    }
                });
            }else{

                dbRef.add(p).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                         if(task.isSuccessful()){
                             Toast.makeText(ContactAddActivity.this, "New Contact Added", Toast.LENGTH_SHORT).show();
                             finish();
                         }else{
                             Log.v(TAG,task.getException().getMessage());
                             Toast.makeText(ContactAddActivity.this, "Oops!", Toast.LENGTH_SHORT).show();
                         }
                         btnAddContact.setVisibility(View.VISIBLE);
                    }
                });
            }
        }else{
            Toast.makeText(this, "Oops!", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validData() {
        if(etFirstName.getText().toString().isEmpty() &&
                etLastName.getText().toString().isEmpty() &&
                etFirstNumber.getText().toString().isEmpty() &&
                etEmail.getText().toString().isEmpty() &&
                etAddress.getText().toString().isEmpty()){
            Toast.makeText(this, "Add Some Details", Toast.LENGTH_SHORT).show();
            return false;
        }else if(!etFirstNumber.getText().toString().isEmpty() && !ccpFirstNumber.isValidFullNumber()){
            etFirstNumber.setError(getResources().getString(R.string.invalid_phone_number));
            return false;
        }
        else if(!etSecondNuumber.getText().toString().isEmpty() && !ccpSecondNumber.isValidFullNumber()){
            etSecondNuumber.setError(getResources().getString(R.string.invalid_phone_number));
            return false;
        }else if(!etEmail.getText().toString().isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(etEmail.getText().toString()).matches()){
            etEmail.setError(getResources().getString(R.string.invalid_email_address));
            return false;
        }
        return true;
    }

    public void getLocationFromMap(View view) {
        getCurrentPlaceItems();
    }



    private void getCurrentPlaceItems() {
        if (isLocationAccessPermitted()) {
            showPlacePicker();
        } else {
            requestLocationAccessPermission();
        }
    }

    private boolean isLocationAccessPermitted() {
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationAccessPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOC_REQ_CODE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOC_REQ_CODE) {
            if (resultCode == RESULT_OK) {
                showPlacePicker();
            }
        }else if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                String address = place.getName() + "\n" + place.getAddress();
                etAddress.setText(address);
                LatLng latLong = place.getLatLng();
                geoPoint = new GeoPoint(latLong.latitude,latLong.longitude);
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void showPlacePicker() {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (Exception e) {
            Log.e(TAG, Arrays.toString(e.getStackTrace()));
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Snackbar.make(view, connectionResult.getErrorMessage() + "", Snackbar.LENGTH_LONG).show();
    }
}
