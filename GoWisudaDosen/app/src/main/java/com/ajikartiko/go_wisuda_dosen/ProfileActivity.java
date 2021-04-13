package com.ajikartiko.go_wisuda_dosen;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ajikartiko.go_wisuda_dosen.model.Mahasiswa;
import com.ajikartiko.go_wisuda_dosen.model.User;
import com.ajikartiko.go_wisuda_dosen.utils.FirestoreUtil;
import com.ajikartiko.go_wisuda_dosen.utils.StorageUtil;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.theartofdev.edmodo.cropper.CropImage;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileActivity extends AppCompatActivity {


    private TextView nameTextView, judulTextView, semsterTextView;
    private CircleImageView profilePicImageView;
    private static final int PICK_IMAGE = 123;
    private Uri resultUri;
    private com.ajikartiko.go_wisuda_dosen.CustomTextInputDialog editTextDialog;
    private User currentUser;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(null);
        }

        currentUser = (User) getIntent().getSerializableExtra("CURRENT_USER");
        editTextDialog = new com.ajikartiko.go_wisuda_dosen.CustomTextInputDialog();
        nameTextView = findViewById(R.id.profile_name_textView);
        judulTextView = findViewById(R.id.profile_judul_textView);
        semsterTextView = findViewById(R.id.profile_semester_textView);
        profilePicImageView = findViewById(R.id.profile_pic_imageView);
        if (currentUser != null) {
            nameTextView.setText(currentUser.getName());
            Glide.with(this).load(currentUser.getImage()).error(R.drawable.user).fallback(R.drawable.user).into(profilePicImageView);
            if (currentUser instanceof Mahasiswa) {
                semsterTextView.setText(String.valueOf(((Mahasiswa) currentUser).getSemester()));
                judulTextView.setText(((Mahasiswa) currentUser).getJudulSkripsi());
            } else {
                findViewById(R.id.tabJudul).setVisibility(View.GONE);
                findViewById(R.id.tabSmester).setVisibility(View.GONE);
            }
        }

        profilePicImageView.setOnClickListener(view -> {
            Intent profileIntent = new Intent();
            profileIntent.setType("image/*");
            profileIntent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(profileIntent, "Select Image."), PICK_IMAGE);
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                resultUri = result.getUri();
                if (resultUri != null) {
                    Glide.with(this).load(resultUri).into(profilePicImageView);
                }
            }
            if (requestCode == PICK_IMAGE && data.getData() != null) {
                Uri imagePath = data.getData();
                CropImage.activity(imagePath)
                        .setAspectRatio(1, 1)
                        .setOutputCompressQuality(50)
                        .start(this);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void buttonClickedEdit(View view) {
        if (view.getId() == R.id.buttonEditName) {
            editTextDialog.setTitle("Edit Name");
            editTextDialog.setTextInput("Name", currentUser.getName(), null);
            editTextDialog.setListener(result -> {
                currentUser.setName(result);
                nameTextView.setText(result);
            });
            editTextDialog.show(getSupportFragmentManager(), "buttonEditName");
        } else if (view.getId() == R.id.buttonEditjudul) {
            editTextDialog.setTitle("Edit Judul");
            editTextDialog.setTextInput("Judul", ((Mahasiswa) currentUser).getJudulSkripsi(), null);
            editTextDialog.setListener(result -> {
                ((Mahasiswa) currentUser).setJudulSkripsi(result);
                judulTextView.setText(result);
            });
            editTextDialog.show(getSupportFragmentManager(), "buttonEditJudul");
        } else if (view.getId() == R.id.buttonEditPhoneNo) {
            editTextDialog.setTitle("Edit Semester");
            editTextDialog.setTextInput("Semester", ((Mahasiswa) currentUser).getSemester().toString(), InputType.TYPE_CLASS_NUMBER);
            editTextDialog.setListener(result -> {
                ((Mahasiswa) currentUser).setSemester(Integer.valueOf(result));
                semsterTextView.setText(result);
            });
            editTextDialog.show(getSupportFragmentManager(), "buttonEditSemester");
        }
    }


    public void buttonSave(View view) {
        if (resultUri != null) {
            StorageUtil storageUtil = new StorageUtil();
            storageUtil.uploadFile(resultUri, "Profile.jpg", task -> {
                if (task.getResult() != null) {
                    currentUser.setImage(task.getResult().toString());
                    updateData();
                }
            }, e -> Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            updateData();
        }
    }

    private void updateData() {
        FirestoreUtil firestoreUtil = new FirestoreUtil();
        firestoreUtil.userDocReference(FirebaseAuth.getInstance().getCurrentUser().getUid()).set(currentUser)
                .addOnSuccessListener(aVoid -> onBackPressed())
                .addOnFailureListener(e -> Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}






