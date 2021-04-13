package com.ajikartiko.go_wisuda_dosen;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class ForgetPasswordActivity extends AppCompatActivity {

    private EditText inputEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgetpassword);
        inputEmail = findViewById(R.id.fgtemail);

        Button resetPassword = findViewById(R.id.fgtbutton);
        resetPassword.setOnClickListener(v -> {
            String userEmail = inputEmail.getText().toString().trim();
            if (TextUtils.isEmpty(userEmail)) {
                Toast.makeText(com.ajikartiko.go_wisuda_dosen.ForgetPasswordActivity.this, "masukan email", Toast.LENGTH_SHORT).show();
            } else {
                FirebaseAuth.getInstance().sendPasswordResetEmail(userEmail)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(com.ajikartiko.go_wisuda_dosen.ForgetPasswordActivity.this, "Reset password terkirim ke email anda", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(com.ajikartiko.go_wisuda_dosen.ForgetPasswordActivity.this, com.ajikartiko.go_wisuda_dosen.LoginActivity.class));
                            finish();
                        })
                        .addOnFailureListener(e -> Toast.makeText(this, "Gagal, " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });

        TextView backLogin = findViewById(R.id.fgtbacklogin);
        backLogin.setOnClickListener(v -> startActivity(new Intent(com.ajikartiko.go_wisuda_dosen.ForgetPasswordActivity.this, com.ajikartiko.go_wisuda_dosen.LoginActivity.class)));
    }
}