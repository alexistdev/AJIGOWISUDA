package com.ajikartiko.gowisuda;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;


public class SettingEdit extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private TextView profileNameTextView, profileJudulTextView, profileSemsterTextView;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private CircleImageView profilePicImageView;
    private FirebaseStorage firebaseStorage;
    private static int PICK_IMAGE = 123;
    Uri imagePath;
    private StorageReference storageReference;
    private EditText editTextName;

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data.getData() != null) {
            imagePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagePath);
                profilePicImageView.setImageBitmap(bitmap);
                CropImage.activity(imagePath)
                        .setAspectRatio(1,1)
                        .start(this);
            } catch (IOException e) {
                e.printStackTrace();
            }if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_edit);

        editTextName = (EditText) findViewById(R.id.et_username);
        profileNameTextView = findViewById(R.id.profile_name_textView);
        profileJudulTextView = findViewById(R.id.profile_judul_textView);
        profileSemsterTextView = findViewById(R.id.profile_semester_textView);
        profilePicImageView = findViewById(R.id.profile_pic_imageView);
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReference();
        DatabaseReference databaseReference = firebaseDatabase.getReference("Mahasiswa");
        storageReference = firebaseStorage.getReference();
        storageReference.child("user profile Mahasiswa").child(firebaseAuth.getUid()).child("Images").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).fit().centerInside().into(profilePicImageView);
            }
        });



        profilePicImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profileIntent = new Intent();
                profileIntent.setType("image/*");
                profileIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(profileIntent, "Select Image."), PICK_IMAGE);
            }
        });

        if (firebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(getApplicationContext(),daftar.class));
        }

        final FirebaseUser user=firebaseAuth.getCurrentUser();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot) {
                userInformation userProfile = dataSnapshot.child(user.getUid()).child("profile").getValue(userInformation.class);
                profileNameTextView.setText(userProfile.getName());
                profileJudulTextView.setText(userProfile.getJudulskripsi());
                profileSemsterTextView.setText(userProfile.getSemester());


            }
            @Override
            public void onCancelled( DatabaseError databaseError) {
            }
        });


    }


    public void buttonClickedEditName(View view) {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.dialog_edit_nama, null);
        final EditText etUsername = alertLayout.findViewById(R.id.et_username);
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Edit Nama");
        // this is set the view from XML inside AlertDialog
        alert.setView(alertLayout);
        // disallow cancel of AlertDialog on click of back button and outside touch
        alert.setCancelable(false);
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = etUsername.getText().toString();
                String judulskripsi = profileJudulTextView.getText().toString();
                String semester =  profileSemsterTextView.getText().toString();
                userInformation userinformation = new userInformation(name,judulskripsi, semester);
                FirebaseUser user = firebaseAuth.getCurrentUser();
                databaseReference = FirebaseDatabase.getInstance().getReference("Dosen");
                databaseReference.child(user.getUid()).child("profile").setValue(userinformation);
                etUsername.onEditorAction(EditorInfo.IME_ACTION_DONE);
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }

    public void buttonClickedEditjudul(View view) {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.dialog_edit_judul, null);
        final EditText etUserjudul = alertLayout.findViewById(R.id.et_judul);
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Edit Judul Skripsi");
        // this is set the view from XML inside AlertDialog
        alert.setView(alertLayout);
        // disallow cancel of AlertDialog on click of back button and outside touch
        alert.setCancelable(false);
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String name = profileNameTextView.getText().toString();
                String judulskripsi = etUserjudul.getText().toString();
                String semester =  profileSemsterTextView.getText().toString();
                userInformation userinformation = new userInformation(name,judulskripsi, semester);
                FirebaseUser user = firebaseAuth.getCurrentUser();
                databaseReference = FirebaseDatabase.getInstance().getReference("Dosen");
                databaseReference.child(user.getUid()).child("profile").setValue(userinformation);
                etUserjudul.onEditorAction(EditorInfo.IME_ACTION_DONE);
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }

    public void buttonClickedEditsemester(View view) {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.dialog_edit_semeter, null);
        final EditText etUsersemester = alertLayout.findViewById(R.id.et_usersemester);
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Edit Semester");
        // this is set the view from XML inside AlertDialog
        alert.setView(alertLayout);
        // disallow cancel of AlertDialog on click of back button and outside touch
        alert.setCancelable(false);
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = profileNameTextView.getText().toString();
                String judulskripsi = profileJudulTextView.getText().toString();
                String semester =  etUsersemester.getText().toString();
                userInformation userinformation = new userInformation(name,judulskripsi, semester);
                FirebaseUser user = firebaseAuth.getCurrentUser();
                databaseReference = FirebaseDatabase.getInstance().getReference("Dosen");
                databaseReference.child(user.getUid()).child("profile").setValue(userinformation);
                etUsersemester.onEditorAction(EditorInfo.IME_ACTION_DONE);
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }


    public void buttonsave(View view) {
        finish();
        startActivity(new Intent(SettingEdit.this, Home.class));


    }
    }






