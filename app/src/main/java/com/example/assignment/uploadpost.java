package com.example.assignment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class uploadpost extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST=1;
    String[] permission={"android.permission.READ_EXTERNAL_STORAGE","android.permission.WRITE_EXTERNAL_STORAGE"};
    private Button uploads;
    private ImageView imageView;
    private ProgressBar progressBar;
    private Uri mimageuri;
    // private StorageReference mstorageref;
    private DatabaseReference root= FirebaseDatabase.getInstance().getReference("images");
    private StorageReference reference= FirebaseStorage.getInstance().getReference();
    // private StorageTask muploadtask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploadpost);
        getSupportActionBar().setTitle("Upload Today Post");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        uploads=findViewById(R.id.upload);
        imageView=findViewById(R.id.imageView);
        progressBar=findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(permission,80);
                }
                Intent galaryintent=new Intent();
                galaryintent.setAction(Intent.ACTION_GET_CONTENT);
                galaryintent.setType("image/*");
                startActivityForResult(galaryintent,2);
            }
        });
        uploads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mimageuri!=null)
                {
                    uploadtofirebase(mimageuri);
                    finish();
                    Toast.makeText(uploadpost.this, "Uploading...", Toast.LENGTH_LONG).show();

                }
                else {
                    Toast.makeText(uploadpost.this, "Please Select image before upload", Toast.LENGTH_SHORT).show();
                }
            }


        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==2 && resultCode==RESULT_OK &&data!=null )
        {
            mimageuri=data.getData();
            imageView.setImageURI(mimageuri);
        }
    }
    private void uploadtofirebase(Uri uri)
    {
        StorageReference fileref=reference.child(System.currentTimeMillis()+"." +getfileextension(uri));
        fileref.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        upload picupload=new upload(uri.toString());
                        String picuploadid=root.push().getKey();
                        root.child(picuploadid).setValue(picupload);
                        Toast.makeText(uploadpost.this, "Uploaded successfully", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                progressBar.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(uploadpost.this, "uploading fail", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private String getfileextension(Uri muri)
    {
        ContentResolver cr=getContentResolver();
        MimeTypeMap mime=MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(muri));
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==80)
        {
            if(grantResults[0]== PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "You can Upload your pictures", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(this, "first Allow us for post pictures", Toast.LENGTH_SHORT).show();
            }
        }
    }



}