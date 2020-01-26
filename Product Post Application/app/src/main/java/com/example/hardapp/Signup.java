package com.example.hardapp;




import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class Signup extends AppCompatActivity implements View.OnClickListener {

    //==============================================================================================
    //Objects
    //==============================================================================================

    private EditText signUpEmail;
    private EditText signUpPhoneNumber;
    private EditText signUpPassword;
    private EditText signUpConPassword;
    private EditText signUpUserName;
    private Button loginScreenBtn;
    private EditText name;
    private Button createAccount;
    private ImageView profilePic;
    private TextView errorMsg;
    private ProgressDialog progressDialog;
    private Uri profileUri;
    private static final String TAG = "SignUp";




    private FirebaseAuth mAuth;
    private int PICK_IMAGE_REQUEST = 1;
    private User signUpUser;
    private boolean profileIsSelected;

    //==============================================================================================
    //Override Methods
    //==============================================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getSupportActionBar().hide();
        objectsInit();



    }



    private void objectsInit(){


        signUpUser=new User();
        mAuth=FirebaseAuth.getInstance();
        createAccount=findViewById(R.id.createBtn);
        createAccount.setOnClickListener(this);
        signUpEmail=findViewById(R.id.reEmailInput);
        signUpPhoneNumber=findViewById(R.id.phoneNumbeInput);
        signUpPassword=findViewById(R.id.passwordInputReg);
        signUpConPassword=findViewById(R.id.rePasswordInputReg);
        signUpUserName=findViewById(R.id.reUserName);
        profilePic=findViewById(R.id.reProfile);
        profilePic.setOnClickListener(this);
        progressDialog =new ProgressDialog(Signup.this);
        loginScreenBtn=findViewById(R.id.loginScreenBtn);
        loginScreenBtn.setOnClickListener(this);


    }




    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.createBtn:

                signUpUser=new User();
                signUpUser.setPhone(signUpPhoneNumber.getText().toString());
                signUpUser.setEmail(signUpEmail.getText().toString());
                signUpUser.setPass(signUpPassword.getText().toString());
                signUpUser.setName(signUpUserName.getText().toString());
                userDataValidation(signUpUser, signUpConPassword.getText().toString());



                break;


            case R.id.reProfile:
                getPictureFromGallery();
                break;

            case R.id.loginScreenBtn:
                progressDialog.show();
                startActivity(new Intent(Signup.this,Login.class));
                progressDialog.dismiss();
                finish();


                break;
        }


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1&&resultCode==RESULT_OK){


            Toast.makeText(this, "Selected", Toast.LENGTH_SHORT).show();
            Glide.with(Signup.this).load(data.getData())
                    .apply(RequestOptions.centerCropTransform())
                    .apply(RequestOptions.circleCropTransform())
                    .into(profilePic);
           // profilePic.setImageURI(data.getData());
            profileIsSelected=true;
            profileUri=data.getData();
        }
    }




    //==============================================================================================
    //Custom Methods
    //==============================================================================================


    private void userDataValidation(User newUser,String conPass){

        if(!profileIsSelected){
            Toast.makeText(this, "Profile Picture Not selected", Toast.LENGTH_SHORT).show();
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(newUser.getEmail()).matches()){

            signUpEmail.setError("Incorrect email");
            Log.d(Signup.this.getClass().getName(), "email");

        }else if(newUser.getPhone().length()<10){

            Log.d(Signup.this.getClass().getName(), "Number");
            signUpPhoneNumber.setError("incorrect phone number");

        }else if(newUser.getPass().length()<6){

            signUpPassword.setError("Password too short");
            Log.d(Signup.this.getClass().getName(), "pass2");
        }else if(!signUpPassword.getText().toString().equals(conPass)){

            signUpPassword.setError("Password not match");
            Log.d(Signup.this.getClass().getName(), "pass2");
        }
        else{
            Toast.makeText(this, "Every Thing is fine", Toast.LENGTH_SHORT).show();
            createUser(newUser.getEmail(),newUser.getPass());
        }
        Log.d(Signup.this.getClass().getName(), "CreateBtn");

    }







    private void getPictureFromGallery(){

        Intent intent=new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        profileIsSelected=true;
        startActivityForResult(intent,1);
    }


    private void createUser(final String e, final String p){


        progressDialog.setMessage("Creating User");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        mAuth.createUserWithEmailAndPassword(e,p)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            Log.d(TAG, "onComplete: Now user Created");
                            Toast.makeText(Signup.this, String.valueOf(profileUri), Toast.LENGTH_SHORT).show();
                            FirebaseStorage firebaseStorage=FirebaseStorage.getInstance();
                            final StorageReference storageReference=firebaseStorage.getReference(signUpUser.getEmail()+"/"+"profile_picture");
                            UploadTask uploadTask= storageReference.putFile(profileUri);
                            progressDialog.setMessage("Profile Picture Uploading "+new File(String.valueOf(profileUri)).length()+"KB");

                            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                @Override
                                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                    if (!task.isSuccessful()) {
                                        throw task.getException();
                                    }

                                    // Continue with the task to get the download URL
                                    return storageReference.getDownloadUrl();
                                }
                            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        final Uri downloadUri = task.getResult();
                                        mAuth.signInWithEmailAndPassword(e,p)
                                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {

                                                mAuth.getCurrentUser().updateProfile(getUpdateProfileData(downloadUri, signUpUser.getName()))
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                           if(task.isSuccessful()){

                                                               mAuth.signOut();
                                                               Map<String,String> data=new HashMap<>();
                                                               data.put("Phone",signUpUser.getPhone());
                                                               FirebaseFirestore.getInstance()
                                                                       .collection("UserInformation")
                                                                       .document(e).set(data).
                                                                       addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                   @Override
                                                                   public void onComplete(@NonNull Task<Void> task) {
                                                                       if(task.isSuccessful()){

                                                                           progressDialog.dismiss();

                                                                           AlertDialog.Builder alertDialog=new AlertDialog.Builder(Signup.this);
                                                                           alertDialog.setMessage("Congrats your are registerd");
                                                                           alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                                               @Override
                                                                               public void onClick(DialogInterface dialog, int which) {

                                                                                   finish();
                                                                               }
                                                                           });
                                                                           alertDialog.create().show();

                                                                       }
                                                                   }
                                                               });


                                                           }
                                                            }
                                                        });


                                            }
                                        });

                                    } else {

                                    }
                                }
                            });





                        }else {


                            try {
                                throw  task.getException();
                            }catch (FirebaseAuthUserCollisionException exist){

                                Toast.makeText(Signup.this, "email already register", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }


                        }
                    }

                });

    }




    private UserProfileChangeRequest getUpdateProfileData(Uri link,String name){

        return new UserProfileChangeRequest.Builder().
                setDisplayName(name)
                .setPhotoUri(link)
                .build();

    }

}


