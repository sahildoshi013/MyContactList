package com.example.sahilj.mycontactlist;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sahilj.mycontactlist.Utils.MyUtilities;
import com.example.sahilj.mycontactlist.model.Person;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.List;

public class DisplayContactActivity extends AppCompatActivity {

    private static final int RC_EDIT_CONTACT = 1;
    private TextView tvFirstNumber;
    private TextView tvSecondNumber;
    private TextView tvEmailID;
    private TextView tvAddress;
    private CollapsingToolbarLayout toolbarLayout;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_contact);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        toolbarLayout = findViewById(R.id.toolbar_layout);

        tvFirstNumber = findViewById(R.id.tvFirstNumber);
        tvSecondNumber = findViewById(R.id.tvSecondNumber);
        tvEmailID = findViewById(R.id.tvEmailID);
        tvAddress = findViewById(R.id.tvAddress);

        Person person = MyUtilities.personData.toObject(Person.class);
        setData(person);

        //initialise database
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_display_contact,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_delete:
                deleteContact();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteContact() {
        AlertDialog.Builder alert = new AlertDialog.Builder(DisplayContactActivity.this);
        alert.setTitle("Delete");
        alert.setMessage("Are you sure you want to delete?");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String docId = MyUtilities.personData.getId();
                String colID = MyUtilities.getUser();
                if (colID != null) {
                    db.collection(colID).document(docId).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(DisplayContactActivity.this, "Deleted!", Toast.LENGTH_SHORT).show();
                                finish();
                            }else{
                                Toast.makeText(DisplayContactActivity.this, "Oops!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                dialog.dismiss();
            }
        });

        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alert.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Person person = MyUtilities.person;
        setData(person);
    }

    private void setData(Person person) {
        if(person !=null) {

            final String name = MyUtilities.getFullName(person.getFirstName(),person.getLastName());
            List<String> numbers = person.getMobileNumber();
            String email = person.getEmailID();
            final String address = person.getAddress();
            final GeoPoint geopoint = person.getLocation();

            if(name!=null && !name.isEmpty())
                toolbarLayout.setTitle(name);
            else
                toolbarLayout.setTitle("(No Name)");

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
            if((address!=null && !address.isEmpty())){
                tvAddress.setText(address);
                tvAddress.setVisibility(View.VISIBLE);
            }
            if(geopoint!=null){
             //   tvAddress.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_location_on_yellow_24dp),null,getResources().getDrawable(R.drawable.ic_map_yellow_24dp),null);
                tvAddress.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        double lat = geopoint.getLatitude();
                        double lon = geopoint.getLongitude();
                        Uri gmmIntentUri = Uri.parse("geo:"+lat+","+lon+"?q="+lat+","+lon+"("+name+"\n"+address+")");
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
        Intent editContact = new Intent(this,ContactAddActivity.class);
        editContact.putExtra(MyUtilities.IS_EDIT_MODE,true);
        editContact.putExtra("ParentClassSource", DisplayContactActivity.class);
        startActivity(editContact);
    }

}
