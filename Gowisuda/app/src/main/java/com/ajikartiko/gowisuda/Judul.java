package com.ajikartiko.gowisuda;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class Judul extends AppCompatActivity {

    private Button upload, download, selectfile;
    private TextView notification;
    Uri pdfuri; // Uri Are Actually URLs tat are meant for local storage
    ProgressDialog progressDialog;
    FirebaseStorage firebaseStorage; //used for uploading Files...
    FirebaseDatabase database;
    FirebaseAuth mAuth;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_judul);

        upload = findViewById(R.id.buttonuploadjud);
        download = findViewById(R.id.buttondownloadjud);
        selectfile = findViewById(R.id.selectfile);
        notification = findViewById(R.id.notif);

        firebaseStorage = FirebaseStorage.getInstance(); // Return an Object Of Firebase Storage
        database= FirebaseDatabase.getInstance(); // Return an object of firebase Database
        mAuth = FirebaseAuth.getInstance();


        selectfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(Judul.this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
                    selectPdf();
                }
                else
                    ActivityCompat.requestPermissions(Judul.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 9);
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pdfuri!=null) //the user has selected the file
                uploadfile(pdfuri);
                else
                    Toast.makeText(Judul.this, "Select a file", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadfile(Uri pdfuri) {

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(progressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("Uploading FIle...");
        progressDialog.setProgress(0);
        progressDialog.show();

        final String filename = System.currentTimeMillis()+"";
        StorageReference storageReference= firebaseStorage.getReference(); // return path
        storageReference.child("File Proposal Skripsi").child(filename).putFile(pdfuri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        String url = taskSnapshot.getUploadSessionUri().toString();
                        String user_id = mAuth.getCurrentUser().getUid();
                        reference = FirebaseDatabase.getInstance().getReference("Skripsi");
                        reference.child(user_id).child("Proposal").setValue(user_id).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful())
                                    Toast.makeText(Judul.this, "File Successfully", Toast.LENGTH_SHORT).show();
                                else
                                    Toast.makeText(Judul.this, "File not Successfully", Toast.LENGTH_SHORT).show();

                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Judul.this, "File not Successfully", Toast.LENGTH_SHORT).show();

            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                int currentProgress =(int) (100*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                progressDialog.setProgress(currentProgress);

            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode==9 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            selectPdf();
        }else
            Toast.makeText(Judul.this, "Pleass Provide Permisssion", Toast.LENGTH_SHORT).show();


    }

    private void selectPdf() {
        // To offer user to select using file manager

        // We will be using an Intent

        Intent intent = new Intent();
        intent.setType("Application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT); // Untuk mengambil file
        startActivityForResult(intent, 86);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==86 && resultCode==RESULT_OK && data!=null){
            pdfuri=data.getData();
            notification.setText("A file is Select :" +data.getData().getLastPathSegment());

        }else{
            Toast.makeText(Judul.this, "Pleass Select a File", Toast.LENGTH_SHORT).show();
        }
    }
}