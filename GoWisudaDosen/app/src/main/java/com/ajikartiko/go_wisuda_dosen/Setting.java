package com.ajikartiko.go_wisuda_dosen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class Setting extends AppCompatActivity {

    private static final String TAG = Setting.class.getSimpleName();
    Button btnsave;
    private FirebaseAuth firebaseAuth;
    private TextView profilfoto1;
    private DatabaseReference databaseReference;
    private EditText editTextName;
    private CircleImageView profileImageView;
    private FirebaseStorage firebaseStorage;
    private static int PICK_IMAGE = 123;
    Uri imagePath;
    private StorageReference storageReference;


    public Setting() {
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data.getData() != null) {
            imagePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagePath);
                profileImageView.setImageBitmap(bitmap);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);



            firebaseAuth=FirebaseAuth.getInstance();
            if (firebaseAuth.getCurrentUser() == null){
                finish();
                startActivity(new Intent(getApplicationContext(),Registrasi.class));
            }


            editTextName = (EditText) findViewById(R.id.EditTextName1);
            profilfoto1 = (TextView) findViewById(R.id.textviewfotoprofil1);
            btnsave = (Button) findViewById(R.id.btnSaveButton);
            FirebaseUser user=firebaseAuth.getCurrentUser();
            profileImageView = findViewById(R.id.update_imageView);
            firebaseStorage = FirebaseStorage.getInstance();
            storageReference = firebaseStorage.getReference();
            firebaseAuth = FirebaseAuth.getInstance();
            databaseReference = FirebaseDatabase.getInstance().getReference("Dosen");


            profilfoto1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent profileIntent = new Intent();
                    profileIntent.setType("image/*");
                    profileIntent.setAction(android.content.Intent.ACTION_GET_CONTENT);
                    startActivityForResult(android.content.Intent.createChooser(profileIntent, "Select Image."), PICK_IMAGE);

                }
            });

            profileImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent profileIntent = new Intent();
                    profileIntent.setType("image/*");
                    profileIntent.setAction(android.content.Intent.ACTION_GET_CONTENT);
                    startActivityForResult(android.content.Intent.createChooser(profileIntent, "Select Image."), PICK_IMAGE);
                }
            });


        }

        private void userInformation() {
            String name = editTextName.getText().toString().trim();
            userInformasi userInformasi = new userInformasi(name);
            FirebaseUser user = firebaseAuth.getCurrentUser();
            databaseReference.child(user.getUid()).child("profile").setValue(userInformasi);
            startActivity(new Intent(Setting.this, Login.class));
            finish();
        }

        public void save(View view) {
            if (view==btnsave){
                if (imagePath == null) {

                    Drawable drawable = this.getResources().getDrawable(R.drawable.user);
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.user);
                    //openSelectProfilePictureDialog();
                    sendVerificationEmail();
                    userInformation();
                    sendUserData();


                }
                else {
                    sendVerificationEmail();
                    userInformation();
                    sendUserData();

                }
            }
        }

        private void sendUserData() {

            String user_id = firebaseAuth.getCurrentUser().getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference("Dosen");
            StorageReference imageReference = storageReference.child("user profile dosen").child(firebaseAuth.getUid()).child("Images");//User id/Images/Profile Pic.jpg
            UploadTask uploadTask = imageReference.putFile(imagePath);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Setting.this, "Error: Uploading profile picture", Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    databaseReference = FirebaseDatabase.getInstance().getReference("Dosen");
                    databaseReference.child(user_id).child("image").setValue(user_id).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(Setting.this, "firebase", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });




                }
            });
        }


        private void sendVerificationEmail() {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        FirebaseAuth.getInstance().signOut();
                        Toast.makeText(Setting.this, "Berhasil membuat akun, verifikasi email anda", Toast.LENGTH_SHORT).show();
                    }else{
                        overridePendingTransition(0,0);
                        finish();
                        overridePendingTransition(0,0);
                        startActivity(getIntent());
                    }

                }
            });
        }

        public void openSelectProfilePictureDialog() {
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            TextView title = new TextView(this);
            title.setText("Profile Picture");
            title.setPadding(10, 10, 10, 10);   // Set Position
            title.setGravity(Gravity.CENTER);
            title.setTextColor(Color.BLACK);
            title.setTextSize(20);
            alertDialog.setCustomTitle(title);
            TextView msg = new TextView(this);
            msg.setText("Please select a profile picture \n Tap the sample avatar logo");
            msg.setGravity(Gravity.CENTER_HORIZONTAL);
            msg.setTextColor(Color.BLACK);
            alertDialog.setView(msg);
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // Perform Action on Button
                }
            });

            new Dialog(getApplicationContext());
            alertDialog.show();
            final Button okBT = alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL);
            LinearLayout.LayoutParams neutralBtnLP = (LinearLayout.LayoutParams) okBT.getLayoutParams();
            neutralBtnLP.gravity = Gravity.FILL_HORIZONTAL;
            okBT.setPadding(50, 10, 10, 10);   // Set Position
            okBT.setTextColor(Color.BLUE);
            okBT.setLayoutParams(neutralBtnLP);

        }

    }