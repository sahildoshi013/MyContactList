package com.example.sahilj.mycontactlist;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sahilj.mycontactlist.Utils.MyUtilities;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;
import com.hbb20.CountryCodePicker;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class ContactAddActivity extends AppCompatActivity {

    private static final String TAG = "Contact Add Activity";
    private EditText etFirstNumber;
    private EditText etFirstName;
    private EditText etLastName;
    private EditText etSecondNuumber;
    private EditText etAddress;
    private EditText etEmail;
    private CountryCodePicker ccpFirstNumber;
    private CountryCodePicker ccpSecondNumber;
    private FirebaseFirestore db;

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

        //register edit text to ccpicker
        ccpFirstNumber.registerCarrierNumberEditText(etFirstNumber);
        ccpSecondNumber.registerCarrierNumberEditText(etSecondNuumber);

        ccpFirstNumber.setNumberAutoFormattingEnabled(true);
        ccpSecondNumber.setNumberAutoFormattingEnabled(true);


        // Access a Cloud Firestore instance from your Activity

        db = FirebaseFirestore.getInstance();
    }

    public void addContact(View view) {
        if(validData()){
            Toast.makeText(this, "Add New Contact", Toast.LENGTH_SHORT).show();
            addContactToFireBase();
        }
    }

    //Add contact to firebase
    private void addContactToFireBase() {
        Map<String, Object> docData = new HashMap<>();
        docData.put(MyUtilities.DB_FIRST_NAME, etFirstName.getText().toString());
        docData.put(MyUtilities.DB_LAST_NAME, etLastName.getText().toString());
        docData.put(MyUtilities.DB_MOBILE_NUMBERS, Arrays.asList(ccpFirstNumber.getFullNumberWithPlus(),ccpSecondNumber.getFullNumberWithPlus()));
        docData.put(MyUtilities.DB_EMAIL_ID, etEmail.getText().toString());
        docData.put(MyUtilities.DB_ADDRESS, etAddress.getText().toString());
        docData.put(MyUtilities.DB_LOCATION, new GeoPoint(0,0));

        String collectionID = MyUtilities.getUser();

        Log.v(TAG,collectionID);
        if (collectionID != null) {
            db.collection(collectionID).add(docData).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(@NonNull Task<DocumentReference> task) {
                     if(task.isSuccessful()){
                         Toast.makeText(ContactAddActivity.this, "New Contact Added", Toast.LENGTH_SHORT).show();
                         finish();
                     }else{
                        Log.v(TAG,task.getException().getMessage());
                         Toast.makeText(ContactAddActivity.this, "Oops!", Toast.LENGTH_SHORT).show();
                     }
                }
            });
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

    }

}
