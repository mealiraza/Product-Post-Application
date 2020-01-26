package com.example.hardapp;



import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;


public class AddNewItem extends AppCompatActivity{

    private EditText postName;
    private EditText price;
    private EditText phone;
    private EditText location;
    private EditText dis;
    private ImageView pic;
    private Spinner itemType;
    private Button submit;
    private Uri localUri;
    private Item postItem;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_item);
        postName = findViewById(R.id.postName);
        price = findViewById(R.id.price);
        submit = findViewById(R.id.postSubmitBtn);
        location=findViewById(R.id.location);
        dis=findViewById(R.id.discription);
        phone=findViewById(R.id.num);
        itemType = findViewById(R.id.itemType);
        pic=findViewById(R.id.itemPic);
        db = FirebaseFirestore.getInstance();

        pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getPictureFromGallery();
            }
        });
        itemType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position==0||position==2){

                    price.setEnabled(true);
                    price.setText("");
                }else {
                    price.setEnabled(false);
                    price.setText("No Price");

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        List<String> categories = new ArrayList<String>();
        categories.add("Price");
        categories.add("Exchange");
        categories.add("Both");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        itemType.setAdapter(dataAdapter);
        final ProgressDialog progressDialog = new ProgressDialog(this);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final Item item = new Item();
                item.setName(postName.getText().toString());
                item.setPrice(price.getText().toString());
                item.setLocation(location.getText().toString());
                item.setDiscription(dis.getText().toString());
                item.setContactNum(phone.getText().toString());
                item.setPicture("");


                if(localUri==null||localUri.toString().equals("")){

                    Toast.makeText(AddNewItem.this, "Select a picture", Toast.LENGTH_SHORT).show();
                }
                else if(item.getName().equals("")||item.getName()==null){
                    postName.setError("Incorrect Name");
                }else if(item.getContactNum().equals("")||item.getContactNum()==null){
                    phone.setError("Incorrect phone");
                }else if(item.getDiscription().equals("")||item.getDiscription()==null){
                    dis.setError("shor discription");
                }else if(itemType.getSelectedItemPosition()==0&&price.getText().toString().equals("")){
                    price.setError("Invalid Price");
                }else if(item.getLocation().equals("")||item.getLocation()==null){
                    location.setError("Invalid Address");
                }else {
                    progressDialog.show();
                    FirebaseStorage storage=FirebaseStorage.getInstance();
                    final StorageReference reference =storage.getReference(FirebaseAuth.getInstance().getCurrentUser().getEmail()+"/"+"posts/"+localUri.getLastPathSegment());

                    UploadTask uploadTask= reference .putFile(localUri);

                    Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }

                            // Continue with the task to get the download URL
                            return reference.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
                                item.setPicture(String.valueOf(downloadUri));
                                db.collection("Items").document(item.getName()).set(item).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()) {
                                            progressDialog.dismiss();
                                            finish();
                                        }
                                    }
                                });
                            }
                        }
                    });

                }
                //  item.setPicture(String.valueOf( downloadUri));



            }
        });


    }



    private void getPictureFromGallery(){

        Intent intent=new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1&&resultCode==RESULT_OK){

            localUri=data.getData();

            Glide.with(AddNewItem.this).load(localUri)
                    .apply(RequestOptions.circleCropTransform())
                    .into(pic);
        }
    }
}
