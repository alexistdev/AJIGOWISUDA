package com.ajikartiko.gowisuda;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ajikartiko.gowisuda.model.Dosen;
import com.ajikartiko.gowisuda.model.Mahasiswa;
import com.ajikartiko.gowisuda.model.User;
import com.ajikartiko.gowisuda.utils.FirestoreUtil;
import com.ajikartiko.gowisuda.utils.StorageUtil;
import com.ajikartiko.gowisuda.utils.UserType;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.ArrayList;
import java.util.HashMap;

public class CompleteProfileActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private EditText name, semester, judul;
    private ImageView avatar;
    private Uri resultUri;
    private Button verify;
    private final HashMap<String, Object> hashMap = new HashMap<>();
    private final ArrayList<User> dosens = new ArrayList<>();
    private final ArrayList<String> users = new ArrayList<>();
    private final FirestoreUtil firestoreUtil = new FirestoreUtil();
    private static final int PICK_IMAGE = 123;
    private ProgressDialog mProgress;

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            resultUri = result.getUri();
            Glide.with(this).load(resultUri).into(avatar);
        }
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data.getData() != null) {
            Uri imagePath = data.getData();
            CropImage.activity(imagePath)
                    .setAspectRatio(1, 1)
                    .setOutputCompressQuality(50)
                    .start(this);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_profile);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, users);
        mProgress = new ProgressDialog(this);
        mProgress.setTitle("GO-WISUDA");
        mProgress.setMessage("Silahkan Tunggu Sedang Memproses Data...");
        mProgress.setCanceledOnTouchOutside(false);
        name = findViewById(R.id.EditTextName);
        semester = findViewById(R.id.EditTextSmst);
        judul = findViewById(R.id.EditTextjudskripsi);
        avatar = findViewById(R.id.update_imageView);
        Spinner dosen_Spin = findViewById(R.id.dosen_spinner);
        verify = findViewById(R.id.btnSaveButton);
        Button logout = findViewById(R.id.logout);
        dosen_Spin.setAdapter(adapter);
        dosen_Spin.setOnItemSelectedListener(this);

        verify.setOnClickListener(v -> {
            String fName = name.getText().toString().trim();
            String fSemester = semester.getText().toString().trim();
            String fJudulSkripsi = judul.getText().toString().trim();
            name.setError((fName.isEmpty()) ? "Nama Tidak Boleh Kosong" : null);
            semester.setError((fSemester.isEmpty()) ? "Semester Tidak Boleh Kosong" : null);
            judul.setError((fJudulSkripsi.isEmpty()) ? "Judul Tidak Boleh Kosong" : null);
            if (!TextUtils.isEmpty(fName) && !TextUtils.isEmpty(fSemester) && !TextUtils.isEmpty(fJudulSkripsi)) {
                mProgress.show();
                hashMap.put("name", fName);
                hashMap.put("semester", Integer.valueOf(fSemester));
                hashMap.put("judulSkripsi", fJudulSkripsi);
                if (resultUri != null) {
                    StorageUtil storageUtil = new StorageUtil();
                    storageUtil.uploadFile(resultUri, "Profile.jpg", task -> {
                        if (task.getResult() != null) {
                            hashMap.put("image", task.getResult().toString());
                            updateData();
                        }
                    }, e -> {
                    });
                } else {
                    updateData();
                }
            }
        });

        logout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
        firestoreUtil.getUsersByType(UserType.DOSEN, task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    if (!TextUtils.isEmpty((String) document.get("name"))) {
                        User user = (document.get("type").equals(UserType.MAHASISWA.name())) ? document.toObject(Mahasiswa.class) : document.toObject(Dosen.class);
                        user.setUserId(document.getId());
                        users.add(user.getName());
                        dosens.add(user);
                    }
                }
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(this, "Error getting documents:", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateData() {
        firestoreUtil.userDocReference(FirebaseAuth.getInstance().getCurrentUser().getUid()).update(hashMap).addOnSuccessListener(aVoid ->
                firestoreUtil.getOrCreateChatChannels((String) hashMap.get("pembimbing"), docId -> {
                    Toast.makeText(this, "Profile Disimpan", Toast.LENGTH_SHORT).show();
                    mProgress.dismiss();
                    startActivity(new Intent(this, HomeActivity.class));
                    finish();
                })
        );
    }

    public void onSelectImageClick(View view) {
        Intent profileIntent = new Intent();
        profileIntent.setType("image/*");
        profileIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(profileIntent, "Select Image."), PICK_IMAGE);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        hashMap.put("pembimbing", dosens.get(position).getUserId());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        verify.setEnabled(false);
    }
}