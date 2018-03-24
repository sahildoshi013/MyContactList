package com.example.sahilj.mycontactlist;

import android.content.Intent;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

public class Welcome extends AppCompatActivity implements View.OnClickListener,MyAdapter.OnContactSelectedListener {

    private static final String TAG = "Welcome Activity";
    private static final int LIMIT = 250;
    private Query mQuery;
    private MyAdapter mAdapter;
    private FastScrollRecyclerView mContactRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);


        FloatingActionButton fabAdd = findViewById(R.id.fabAddContact);
        mContactRecycler = findViewById(R.id.recycler);

        fabAdd.setOnClickListener(this);

        // Initialize Firestore and the main RecyclerView
        initFirestore();
        initRecyclerView();

    }

    private void initFirestore() {
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

        String collectionID = MyUtilities.getUser();

        if (collectionID != null) {
            mQuery = mFirestore.collection(collectionID)
                    .orderBy(MyUtilities.DB_FIRST_NAME);

        }else {
            Toast.makeText(this, "Add Contact!", Toast.LENGTH_SHORT).show();
        }

    }

    private void initRecyclerView() {

        if (mQuery == null) {
            Log.w(TAG, "No query, not initializing RecyclerView");
        }

        //initialize adapter with listener
        mAdapter = new MyAdapter(mQuery, this) {

            @Override
            protected void onDataChanged() {
                // Show/hide content if the query returns empty.
                if (getItemCount() == 0) {
                    mContactRecycler.setVisibility(View.GONE);
                } else {
                    mContactRecycler.setVisibility(View.VISIBLE);
                }
            }
        };

        mContactRecycler.setLayoutManager(new LinearLayoutManager(this));
        mContactRecycler.setAdapter(mAdapter); // set adapter to recycler view
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

    //Start login activity
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

    @Override
    protected void onResume() {
        super.onResume();
        if (mAdapter != null) {
            mAdapter.startListening();
            mAdapter.notifyDataSetChanged();
        }
    }

    //check user is login or not
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
            case R.id.menuShowMap:
                //Show map activity with contact markers
                Intent mapActivity = new Intent(this,ContactsOnMapActivity.class);
                startActivity(mapActivity);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fabAddContact:
                //start add contact activity
                startAddContactActivity(view);
                break;
        }
    }


    @Override
    public void onContactSelected(DocumentSnapshot person) {
        //on select particular contact open contact details activity
        MyUtilities.personData = person;
        Intent displayContactActivity = new Intent(this,DisplayContactActivity.class);
        startActivity(displayContactActivity);
    }

    public void startAddContactActivity(View view) {
        //start activity to add new Contact
        Intent addContactActivity = new Intent(this,ContactAddActivity.class);
        addContactActivity.putExtra(MyUtilities.IS_EDIT_MODE,false);
        startActivity(addContactActivity);
    }
}
