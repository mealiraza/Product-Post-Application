package com.example.hardapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class FeedBack extends AppCompatActivity {

    private EditText feedback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);
        feedback=findViewById(R.id.review);
        findViewById(R.id.feedbackSubmit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(feedback.getText().toString().equals("")||feedback.getText()==null){

                    feedback.setError("Invalid Feedback");

                }else {
                    writeMyThisFeedBack(feedback.getText().toString());
                }
            }
        });
    }




    private  void writeMyThisFeedBack(String r){


        ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Writing your feedback . . .");
        progressDialog.show();
        Map<String,String> stringMap= new HashMap<>();
        stringMap.put("review", r);
        FirebaseFirestore.getInstance()
                .collection("FeedBack")
                .document(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                .set(stringMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){

                            AlertDialog.Builder builder=new AlertDialog.Builder(FeedBack.this);
                            builder.setMessage("Thank you for your feedback");
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            });
                            builder.show();
                        }
                    }
                });



    }
}
