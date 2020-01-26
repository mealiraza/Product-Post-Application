package com.example.hardapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;

public class ChangeDisplayName extends AppCompatActivity {


    private EditText displayName;
    private Button changeNameBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_display_name);
        displayName=findViewById(R.id.newName);
        changeNameBtn=findViewById(R.id.changeNameBtn);
        changeNameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final ProgressDialog progressDialog=new ProgressDialog(ChangeDisplayName.this);
                progressDialog.setCancelable(false);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setMessage("Changing name  . . .");
                progressDialog.show();
                FirebaseAuth
                        .getInstance()
                        .getCurrentUser()
                        .updateProfile(getUpdateProfileData(displayName.getText().toString()))
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if(task.isSuccessful()){

                                    AlertDialog.Builder alert=new AlertDialog.Builder(ChangeDisplayName.this);
                                    alert.setMessage("Your name Succesfully changed");
                                    progressDialog.dismiss();
                                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Toast.makeText(ChangeDisplayName.this, "Password Changed", Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                            finish();
                                        }
                                    });
                                    alert.show();



                                }else {

                                    Toast.makeText(ChangeDisplayName.this, "Password not Changed", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }
                            }
                        });
            }
        });
    }


    private UserProfileChangeRequest getUpdateProfileData(String name){

        return new UserProfileChangeRequest.Builder().
                setDisplayName(name)
                .build();

    }
}
