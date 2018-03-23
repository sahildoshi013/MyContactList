package com.example.sahilj.mycontactlist;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Patterns;
import android.view.View;
import android.widget.TextView;

import com.example.sahilj.mycontactlist.Utils.MyUtilities;
import com.example.sahilj.mycontactlist.model.Person;
import com.google.firebase.firestore.GeoPoint;

import java.util.List;

public class DisplayContactActivity extends AppCompatActivity {

    private Person person;
    private TextView tvFirstNumber;
    private TextView tvSecondNumber;
    private TextView tvEmailID;
    private TextView tvAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_contact);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        CollapsingToolbarLayout toolbarLayout = findViewById(R.id.toolbar_layout);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        tvFirstNumber = findViewById(R.id.tvFirstNumber);
        tvSecondNumber = findViewById(R.id.tvSecondNumber);
        tvEmailID = findViewById(R.id.tvEmailID);
        tvAddress = findViewById(R.id.tvAddress);

        person = MyUtilities.person;

        if(person!=null) {
            toolbarLayout.setTitle(person.getFullName());
            List<String> numbers = person.getMobileNumber();
            String email = person.getEmailID();
            String address = person.getAddress();
            final GeoPoint geopoint = person.getLocation();

            if (numbers.size() == 1) {
                tvFirstNumber.setText(numbers.get(0));
                tvFirstNumber.setVisibility(View.VISIBLE);
            }
            if(numbers.size() == 2){
                String number1 = numbers.get(0);
                String number2 = numbers.get(1);
                if(Patterns.PHONE.matcher(number1).matches()) {
                    tvFirstNumber.setText(numbers.get(0));
                    tvFirstNumber.setVisibility(View.VISIBLE);
                }
                if(Patterns.PHONE.matcher(number2).matches()) {
                    tvSecondNumber.setText(numbers.get(1));
                    tvSecondNumber.setVisibility(View.VISIBLE);
                }
            }
            if(email!=null && !email.isEmpty()){
                tvEmailID.setText(email);
                tvEmailID.setVisibility(View.VISIBLE);
            }
            if((address!=null && !address.isEmpty()) || geopoint!=null){
                tvAddress.setText(address);
                tvAddress.setVisibility(View.VISIBLE);
            }
            if(geopoint!=null){
                tvAddress.setCompoundDrawables(null,null,getResources().getDrawable(R.drawable.ic_map_white_24dp),null);
                tvAddress.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Uri gmmIntentUri = Uri.parse("geo:"+geopoint.getLatitude()+","+geopoint.getLongitude());
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        if (mapIntent.resolveActivity(getPackageManager()) != null) {
                            startActivity(mapIntent);
                        }
                    }
                });
            }
        }

    }

    public void openEditContactActivity(View view) {

    }

    public void openMapActivity(View view) {

    }
}
