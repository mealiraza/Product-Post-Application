package com.example.hardapp;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

public class ItemDetailView extends AppCompatActivity {

    private TextView itemName;
    private TextView itemPrice;
    private TextView itemDis;
    private TextView itemAdd;
    private TextView itemPhone;
    private ImageView itemPic;
    private Button call;
    private Button mesg;
    Item item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail_view);
        getSupportActionBar().hide();
        itemName = findViewById(R.id.itemName);
        itemPrice = findViewById(R.id.itemPrice);
        itemAdd = findViewById(R.id.itemAddress);
        itemPhone = findViewById(R.id.itemPhone);
        itemPic=findViewById(R.id.itemPic);
        itemDis=findViewById(R.id.itemDis);
        call = findViewById(R.id.call);
         mesg = findViewById(R.id.mesg);


        item = getIntent().getParcelableExtra("Item");


        itemName.setText(item.getName());
        itemPrice.setText("Rs." + item.getPrice());
        itemAdd.setText("Location " + item.getLocation());
        itemPhone.setText("Phone " + item.getContactNum());
        itemDis.setText(item.getDiscription());

        Glide.with(this).load(item.getPicture()).into(itemPic);

        mesg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_MAIN);

                intent.addCategory(Intent.CATEGORY_DEFAULT);

                intent.setType("vnd.android-dir/mms-sms");

                startActivity(intent);
            }
        });
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                     if(ContextCompat.checkSelfPermission(ItemDetailView.this, Manifest.permission.CALL_PHONE)
                     !=PackageManager.PERMISSION_GRANTED){

                         ActivityCompat.requestPermissions(ItemDetailView.this, new String[]{Manifest.permission.CALL_PHONE}, 1);
                     }
                Toast.makeText(ItemDetailView.this, item.getContactNum(), Toast.LENGTH_SHORT).show();
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:"+item.getContactNum()));
                startActivity(callIntent);

            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==1){

            if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){

                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:"+item.getContactNum()));
                startActivity(callIntent);
            }
        }
    }
}
