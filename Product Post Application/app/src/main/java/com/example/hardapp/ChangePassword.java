package com.example.hardapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ChangePassword extends AppCompatActivity {

    private EditText pass;
    private EditText conPass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        pass=findViewById(R.id.changePass);
        conPass=findViewById(R.id.changeConPass);
        findViewById(R.id.changePassBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(ChangePassword.this, "Nwo Change Pressed", Toast.LENGTH_SHORT).show();
                changePassBtn();
            }
        });

    }

    private void changePassBtn(){


        final ProgressDialog progressDialog= new ProgressDialog(this);
        progressDialog.setMessage("Updating . . .");
        if(pass.getText().toString().length()<6){
            pass.setError("Password too short");
        }else if(!pass.getText().toString().equals(conPass.getText().toString())){

            conPass.setError("Password not matched");
        }else {

            progressDialog.show();
            FirebaseAuth.getInstance().getCurrentUser().updatePassword(pass.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){
                                AlertDialog.Builder alert=new AlertDialog.Builder(ChangePassword.this);
                                alert.setMessage("Your Password Succesfully changed");
                                progressDialog.dismiss();
                                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(ChangePassword.this, "Password Changed", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });
                                alert.show();



                            }else{

                                Toast.makeText(ChangePassword.this, "Please sign out and try again", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }

                        }
                    });
        }



    }
}
