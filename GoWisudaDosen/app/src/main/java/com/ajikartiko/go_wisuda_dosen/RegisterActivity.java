package com.ajikartiko.go_wisuda_dosen;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ajikartiko.go_wisuda_dosen.model.Dosen;
import com.ajikartiko.go_wisuda_dosen.model.User;
import com.ajikartiko.go_wisuda_dosen.utils.FirestoreUtil;
import com.ajikartiko.go_wisuda_dosen.utils.UserType;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class RegisterActivity extends AppCompatActivity {

    private EditText inputName, inputPassword, inputEmail;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgress;
    private final FirebaseAuth.AuthStateListener mAuthListener = firebaseAuth -> sendVerificationEmail(firebaseAuth.getCurrentUser());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        inputPassword = findViewById(R.id.password);
        inputEmail = findViewById(R.id.email);
        inputName = findViewById(R.id.name);
        mProgress = new ProgressDialog(this);
    }

    public void startRegistration(View view) {
        final String name = inputName.getText().toString().trim();
        if (name.isEmpty()) {
            inputEmail.setError("masukan Nama");
        } else if (name.length() < 4) {
            inputEmail.setError("Masukan Nama Yang Valid minimal 4");
        } else {
            inputEmail.setError(null);
        }

        final String email = inputEmail.getText().toString().trim();
        if (email.isEmpty()) {
            inputEmail.setError("masukan Email");
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            inputEmail.setError("Masukan Email Yang Valid");
        } else {
            inputEmail.setError(null);
        }
        final String pass = inputPassword.getText().toString().trim();
        if (pass.isEmpty()) {
            inputPassword.setError("Masukan Password");
        } else if (pass.length() < 8) {
            inputPassword.setError("password Minimal 8 digit");
        } else {
            inputPassword.setError(null);
        }
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass)) {
            mProgress.setTitle("GO-WISUDA");
            mProgress.setMessage("Silahkan Tunggu Sedang Membuat Akun...");
            mProgress.show();
            mProgress.setCanceledOnTouchOutside(false);
            mAuth.createUserWithEmailAndPassword(email, pass)
                    .addOnFailureListener(e -> Toast.makeText(this, "Register gagal " + e.getMessage(), Toast.LENGTH_SHORT).show())
                    .addOnCompleteListener(task -> mProgress.dismiss());
        }
    }

    private void sendVerificationEmail(FirebaseUser user) {
        if (user != null) {
            user.sendEmailVerification()
                    .addOnSuccessListener(aVoid -> {
                        FirestoreUtil firestoreUtil = new FirestoreUtil();
                        User dosen = new Dosen(user.getUid(), inputName.getText().toString().trim(), user.getEmail(), (user.getPhotoUrl() != null) ? user.getPhotoUrl().toString() : "", UserType.DOSEN);
                        firestoreUtil.userDocReference(user.getUid()).set(dosen).addOnSuccessListener(aVoid1 -> {
                            Toast.makeText(this, "Berhasil, Email Verifikasi telah Dikirim Ke email ", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(this, ResendEmailActivity.class));
                            finish();
                        }).addOnFailureListener(e -> Toast.makeText(RegisterActivity.this, "Verifikasi Gagal, " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    })
                    .addOnFailureListener(e -> Toast.makeText(RegisterActivity.this, "Verifikasi Gagal, " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    public void goToLogin(View view) {
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        finish();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mAuthListener != null) {
            mAuth.addAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}





































