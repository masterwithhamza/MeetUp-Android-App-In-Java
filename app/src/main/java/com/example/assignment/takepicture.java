package com.example.assignment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class takepicture extends AppCompatActivity {
    Button capture_button;
    ImageView image_preview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_takepicture);
        getSupportActionBar().setTitle("Take Today Snap");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        capture_button=findViewById(R.id.capture_button);
        image_preview=findViewById(R.id.image_preview);
        if(ContextCompat.checkSelfPermission(takepicture.this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(takepicture.this,new String[]{Manifest.permission.CAMERA},101);
        }
        capture_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent();
                    intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent,101);
                }catch (Exception e)
                {
                    e.printStackTrace();
                    Toast.makeText(takepicture.this, "not working", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            // Get the captured image as a Bitmap
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
            image_preview.setImageBitmap(imageBitmap);
            // Do something with the imageBitmap
        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}