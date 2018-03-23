package com.example.sahilj.mycontactlist;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.sahilj.mycontactlist.Utils.MyUtilities;
import com.example.sahilj.mycontactlist.model.Person;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FirebaseFirestore db;
    private QuerySnapshot data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        db = FirebaseFirestore.getInstance();

        getData();
    }

    private void getData() {
        String colID = MyUtilities.getUser();
        if (colID != null) {
            db.collection(colID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        data = task.getResult();
                        Toast.makeText(MapsActivity.this, "Done!", Toast.LENGTH_SHORT).show();
                        addMarkers();
                    }else {
                        Toast.makeText(MapsActivity.this, "Oops!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void addMarkers() {
        if(mMap!=null && data!=null){
            List<DocumentSnapshot> documents = data.getDocuments();
            for(DocumentSnapshot documentSnapshot : documents){
                Person person = documentSnapshot.toObject(Person.class);

                if(person.getLocation()!=null) {
                    LatLng location = new LatLng(person.getLocation().getLatitude(), person.getLocation().getLongitude());
                    mMap.addMarker(new MarkerOptions().position(location)
                                                        .title(MyUtilities.getFullName(person.getFirstName(),person.getLastName()))
                                                        .snippet(person.getAddress()));
                }
            }
            CameraUpdate cu = CameraUpdateFactory.newLatLng(new LatLng(22.2587,71.1924));
            mMap.moveCamera(cu);
            cu = CameraUpdateFactory.zoomTo(5);
            mMap.moveCamera(cu);

            Toast.makeText(this, "Contact Marker Added!", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        addMarkers();
    }
}
