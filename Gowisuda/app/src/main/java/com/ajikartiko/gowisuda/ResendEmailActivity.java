package com.ajikartiko.gowisuda;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ResendEmailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resend_email);
        MaterialTextView emailInput = findViewById(R.id.resendmail);
        Button resendEmail = findViewById(R.id.resendbutton);
        FirebaseAuth mAunt = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAunt.getCurrentUser();
        emailInput.setText(firebaseUser.getEmail());
        findViewById(R.id.logout).setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, LoginActivity.class));
        });
        resendEmail.setOnClickListener(view -> sendVerificationEmail());
    }

    private void sendVerificationEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.sendEmailVerification()
                    .addOnSuccessListener(aVoid -> Toast.makeText(ResendEmailActivity.this, "Verifikasi Sudah Dikirim Ulang, Cek Email anda", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(ResendEmailActivity.this, "Email Tidak Berhasil Dikirim, " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }
}