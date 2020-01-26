package com.example.hardapp;



import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity implements View.OnClickListener {

    private Button signUpBtn;
    private FirebaseAuth mAuth;
    private EditText emailInput;
    private EditText passwordInput;
    private Button loginBtn;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;
    ProgressDialog progressDialog;
    private static final String TAG = "Login";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        signUpBtn=findViewById(R.id.signUpScreenBtn);
        signUpBtn.setOnClickListener(this);
        mAuth=FirebaseAuth.getInstance();
        emailInput=findViewById(R.id.emailInput);
        passwordInput=findViewById(R.id.passwordInput);
        loginBtn=findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(this);
        progressDialog=new ProgressDialog(this );




    }

    @Override
    protected void onStart() {
        super.onStart();

        authStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                currentUser=mAuth.getCurrentUser();
                if(currentUser!=null){
                     startActivity(new Intent(Login.this,MainDashboard.class));
                    finish();
                }
            }

        };
       mAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(authStateListener);
    }

    @RequiresApi(api = Build.VERSION_CODES.FROYO)
    @Override
    public void onClick(View v) {
        switch (v.getId()){


            case R.id.signUpScreenBtn:
                startActivity(new Intent(Login.this,Signup.class));
                break;
            case R.id.loginBtn:



                Log.d(TAG, "onClick: login Entered");
                LoginMe(emailInput.getText().toString(), passwordInput.getText().toString());
                break;



        }
    }


    @RequiresApi(api = Build.VERSION_CODES.FROYO)
    private void LoginMe(String email, String pass) {


        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

            emailInput.setError("Incorrect email");
            return;
        }else  if(pass.length()<6){

            passwordInput.setError("Password too short");
            return;
        }



        progressDialog.setMessage("Validation . . .");
        progressDialog.show();
        mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "onClick: Complete");

                if (task.isSuccessful()) {


                    progressDialog.dismiss();
                } else {


                    progressDialog.dismiss();
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException weakPassword) {
                        Toast.makeText(Login.this, "Weak Password", Toast.LENGTH_SHORT).show();
                    } catch (FirebaseAuthInvalidUserException wrong) {
                        Toast.makeText(Login.this, "Wrong Validation", Toast.LENGTH_SHORT).show();
                    } catch (FirebaseAuthInvalidCredentialsException wrong) {
                        Toast.makeText(Login.this, "Wrong Password or email", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Log.d(TAG, "onComplete: " + e.getMessage());
                    }

                }
            }
        });


    }


}
