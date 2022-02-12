package com.example.storagefirebase;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class Downlods_page extends AppCompatActivity {
    Button gal;
    Button cam;
    ImageView image;
    String galurl;
    String camurl;
    private static final String TAG = "downloads";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downlods_page);

        gal = findViewById(R.id.gallery_button);
        cam = findViewById(R.id.camera_button);
        image = findViewById(R.id.downloads_image);

        Intent intent = getIntent();
        galurl = intent.getStringExtra("gallery");
        camurl = intent.getStringExtra("camera");
        Log.d(TAG, "onCreate:"+galurl);
        Log.d(TAG, "onCreate:"+camurl);

        gal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Glide.with(getApplicationContext()).load(galurl).into(image);
            }
        });

        cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Glide.with(getApplicationContext()).load(camurl).into(image);
            }
        });
    }
}