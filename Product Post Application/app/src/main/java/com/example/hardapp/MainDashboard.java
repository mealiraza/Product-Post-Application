package com.example.hardapp;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class MainDashboard extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,View.OnClickListener{

    private RecyclerView recyclerView;
    private static final String TAG = "Navigation";
    private TextView mainMail;
    private TextView userNameView;
    private ImageView mainProfile;
    private FirebaseAuth mAuth;

    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_dashboard);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        recyclerView=findViewById(R.id.recyclerView);
        db=FirebaseFirestore.getInstance();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainDashboard.this,AddNewItem.class));
            }
        });



        db.collection("Items").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if(task.isSuccessful()){


                    List<Item> itemList=new ArrayList<>();

                    for (QueryDocumentSnapshot q:task.getResult()){

                        Log.d(TAG, "onComplete: "+q.getData());
                        itemList.add( q.toObject(Item.class));
                        Log.d(TAG, "onCreate: the size of array"+itemList.size());
                        MyAdaptor myAdaptor=new MyAdaptor(getApplicationContext(), itemList);
                        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
                        recyclerView.setAdapter(myAdaptor);
                    }
                }
            }
        });


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        initObjects(navigationView.getHeaderView(0));
        setMainProfileData(FirebaseAuth.getInstance().getCurrentUser());






    }



    private void initObjects(View nav){
        mainMail=nav.findViewById(R.id.userMail);
        mainProfile=nav.findViewById(R.id.headProfile);
        userNameView=nav.findViewById(R.id.userName);
        mAuth= FirebaseAuth.getInstance();
        Toast.makeText(this, mAuth.getCurrentUser().getDisplayName(), Toast.LENGTH_SHORT).show();


    }

    private void setMainProfileData(FirebaseUser currentUser){


          mainMail.setText(currentUser.getEmail());
          userNameView.setText(currentUser.getDisplayName());
        Glide.with(MainDashboard.this)
                .load(currentUser.getPhotoUrl())
                .apply(RequestOptions.centerCropTransform())
                .apply(RequestOptions.circleCropTransform())
                .into(mainProfile);

    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_dashboard, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.refresh) {
            startActivity(getIntent());
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id){

            case R.id.nav_change_pss:

                startActivity(new Intent(MainDashboard.this,ChangePassword.class));
                break;

                case R.id.nav_change_name:
                    startActivity(new Intent(MainDashboard.this,ChangeDisplayName.class));
                    break;

            case R.id.nav_home:
               startActivity(getIntent());
               finish();
                break;

            case R.id.nav_feedback:
                startActivity(new Intent(MainDashboard.this,FeedBack.class));

                break;

            case R.id.myAccount:

                startActivity(new Intent(MainDashboard.this,MyAccount.class));

                break;
            case R.id.getSupport:
                startActivity(new Intent(MainDashboard.this,GetSupportLocation.class));
                break;
                    case R.id.nav_signout:
                        SignOut();
                        break;



        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

        }
    }


    private void SignOut(){

        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(MainDashboard.this,Login.class));
        finish();
    }


    private void writeThisFeedback(String review){


    }
}
