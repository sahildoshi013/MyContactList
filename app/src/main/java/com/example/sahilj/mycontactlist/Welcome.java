package com.example.sahilj.mycontactlist;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.sahilj.mycontactlist.Adapters.MyAdapter;
import com.example.sahilj.mycontactlist.Utils.MyUtilities;
import com.example.sahilj.mycontactlist.model.Person;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

public class Welcome extends AppCompatActivity implements View.OnClickListener,MyAdapter.OnContactSelectedListener {

    private static final String TAG = "Welcome Activity";
    private static final int LIMIT = 250;
    private FloatingActionButton fabAdd;
    private FirebaseFirestore mFirestore;
    private Query mQuery;
    private MyAdapter mAdapter;
    private FastScrollRecyclerView mRestaurantsRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);


        fabAdd = findViewById(R.id.fabAddContact);
        mRestaurantsRecycler = findViewById(R.id.recycler);

        fabAdd.setOnClickListener(this);

        // Initialize Firestore and the main RecyclerView
        initFirestore();
        initRecyclerView();

        //Get Contact Details
        getContactData();
    }

    private void getContactData() {

    }

    private void initFirestore() {
        // TODO(developer): Implement
        mFirestore = FirebaseFirestore.getInstance();

        String collectionID = MyUtilities.getUser();

        // Get the 50 highest rated restaurants
        if (collectionID != null) {
            mQuery = mFirestore.collection(collectionID)
                    .orderBy(MyUtilities.DB_FIRST_NAME)
                    .limit(LIMIT);
        }else {
            Toast.makeText(this, "Add Contact!", Toast.LENGTH_SHORT).show();
        }

    }

    private void initRecyclerView() {
        if (mQuery == null) {
            Log.w(TAG, "No query, not initializing RecyclerView");
        }

        mAdapter = new MyAdapter(mQuery, this) {

            @Override
            protected void onDataChanged() {
                // Show/hide content if the query returns empty.
                if (getItemCount() == 0) {
                    mRestaurantsRecycler.setVisibility(View.GONE);
                } else {
                    mRestaurantsRecycler.setVisibility(View.VISIBLE);
                }
            }

            @Override
            protected void onError(FirebaseFirestoreException e) {
                // Show a snackbar on errors
                Snackbar.make(findViewById(android.R.id.content),
                        "Error: check logs for info.", Snackbar.LENGTH_LONG).show();
            }
        };

        mRestaurantsRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRestaurantsRecycler.setAdapter(mAdapter);
    }


    @Override
    public void onStart() {
        super.onStart();

        // Start sign in if necessary
        if (shouldStartSignIn()) {
            startSignIn();
            return;
        }

        // Start listening for Firestore updates
        if (mAdapter != null) {
            mAdapter.startListening();
        }
    }

    private void startSignIn() {
        Intent intent = new Intent(this,SplashScreenActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
    }

    private boolean shouldStartSignIn() {
        return (FirebaseAuth.getInstance().getCurrentUser() == null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //Add Option menu in Toolbar
        getMenuInflater().inflate(R.menu.option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.menuLogout:
                //Logout From Firebase
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(this, "Logout Successfully!", Toast.LENGTH_SHORT).show();
                startSignIn();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fabAddContact:
                startAddContactActivity(view);
                break;
        }
    }


    @Override
    public void onContactSelected(DocumentSnapshot person) {
        Person p = person.toObject(Person.class);
        Toast.makeText(this, "Start View Contact Activity " + p.getFullName(), Toast.LENGTH_SHORT).show();
    }

    public void startAddContactActivity(View view) {
        Intent addContactActivity = new Intent(this,ContactAddActivity.class);
        startActivity(addContactActivity);
    }
}
