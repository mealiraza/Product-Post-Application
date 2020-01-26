package com.example.hardapp;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Objects;

import static java.lang.String.valueOf;

public class MyAccount extends AppCompatActivity {

    private ImageView profile;
    private TextView profileName;
    private TextView email;
    private TextView phone;
    private TextView lastLogin;
    private Button back;
    private FirebaseUser currentUser;
    private static final String TAG = "MyAccount";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);
        getSupportActionBar().hide();


        profile=findViewById(R.id.myAccountProfile);
        profileName=findViewById(R.id.myAccountName);
        email=findViewById(R.id.myAccountEmail);
        phone=findViewById(R.id.myAccountPhone);
        lastLogin=findViewById(R.id.myAccountLastLogin);
        back=findViewById(R.id.myAccountBack);
        currentUser=FirebaseAuth.getInstance().getCurrentUser();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        Glide.with(this)
                .load(currentUser.getPhotoUrl())
                .apply(RequestOptions.centerCropTransform())
                .apply(RequestOptions.circleCropTransform())
                .into(profile);

       FirebaseFirestore.getInstance()
                .collection("UserInformation")
                .document(currentUser.getEmail())
               .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
           @Override
           public void onComplete(@NonNull Task<DocumentSnapshot> task) {
               if (task.isSuccessful()) {
                   DocumentSnapshot document = task.getResult();
                   if (document.exists()) {
                       Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                       Log.d(TAG, "onComplete: "+document.get("Phone"));
                       phone.setText( String.valueOf(document.get("Phone")));
                   } else {
                       Log.d(TAG, "No such document");
                   }
               } else {
                   Log.d(TAG, "get failed with ", task.getException());
               }
           }
       });



        profileName.setText(currentUser.getDisplayName());
        email.setText(currentUser.getEmail());
        lastLogin.setText("ID :"+FirebaseAuth.getInstance().getUid());
    }
}
