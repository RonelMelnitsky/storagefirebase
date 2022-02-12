package com.example.storagefirebase;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity {
    Button gal;
    Button cam;
    ImageView holder;
    Button downloads;
    ActivityResultLauncher<String> gallery_launcher;
    ActivityResultLauncher camera_launcher;
    FirebaseStorage fs;
    StorageReference refgal;
    StorageReference refcam;
    String galurl;
    String camurl;
    Uri result2;
    Bitmap picture;
    private static final String TAG = "mainactivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gal = findViewById(R.id.gallery);

        cam = findViewById(R.id.camera);
        holder = findViewById(R.id.imageholder);
        downloads = findViewById(R.id.downloads);
        fs = FirebaseStorage.getInstance();
        refgal = fs.getReference().child("gallery");
        refcam = fs.getReference().child("camera");

        gallery_launcher = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                result2 = result;
                String uri =result.toString();
                Toast.makeText(getApplicationContext(), uri, Toast.LENGTH_LONG).show();
                holder.setImageURI(result);
                Upload_gallery();
            }
        });

        camera_launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode()==RESULT_OK && result.getData()!=null){
                    Bundle bun = result.getData().getExtras();
                    picture = (Bitmap) bun.get("data");
                    holder.setImageBitmap(picture);
                    Upload_camera();
                }
            }
        });

        gal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gallery_launcher.launch("image/*");
            }
        });

        cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                camera_launcher.launch(intent);
            }
        });

        downloads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),Downlods_page.class);
                Log.d(TAG, "onClick: url uel " + galurl);
                Log.d(TAG, "onClick: url uel " + camurl);
                intent.putExtra("gallery",galurl);
                intent.putExtra("camera",camurl);
                startActivity(intent);
            }
        });

    }

    private void Upload_gallery(){
        Log.d(TAG, "Upload_gallery: started");
        UploadTask ut = refgal.putFile(result2);
        Log.d(TAG, "Upload_gallery: second");
        Task<Uri> task = ut.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if(task.isSuccessful()){
                    return refgal.getDownloadUrl();
                }
                throw task.getException();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                galurl = task.getResult().toString();
                Log.d(TAG, "onComplete: url "+galurl);
            }
        });
    }

    private void Upload_camera(){
        Log.d(TAG, "Upload_gallery: started");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        picture.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte[] data = baos.toByteArray();
        UploadTask ut = refcam.putBytes(data);
        Log.d(TAG, "Upload_gallery: second");
        Task<Uri> task = ut.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if(task.isSuccessful()){
                    return refcam.getDownloadUrl();
                }
                throw task.getException();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                camurl = task.getResult().toString();
                Log.d(TAG, "onComplete: url "+camurl);
            }
        });
    }
}