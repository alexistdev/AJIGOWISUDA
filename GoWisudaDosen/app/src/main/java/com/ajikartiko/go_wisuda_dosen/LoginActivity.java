package com.ajikartiko.go_wisuda_dosen;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText emailInput;
    private EditText passwordInput;
    private ProgressDialog loginProgress;
    private final FirebaseAuth.AuthStateListener mAuthListener = this::loginCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        Button login = findViewById(R.id.btnlogin);
        TextView forgetPass = findViewById(R.id.fgtpass);
        TextView signUp = findViewById(R.id.txtsignup);
        emailInput = findViewById(R.id.emaillogin);
        passwordInput = findViewById(R.id.passlogin);
        loginProgress = new ProgressDialog(this);
        forgetPass.setOnClickListener(v -> {
            startActivity(new Intent(com.ajikartiko.go_wisuda_dosen.LoginActivity.this, ForgetPasswordActivity.class));
            finish();
        });

        signUp.setOnClickListener(v -> {
            startActivity(new Intent(com.ajikartiko.go_wisuda_dosen.LoginActivity.this, RegisterActivity.class));
            finish();
        });

        login.setOnClickListener(v -> processLogin());
    }

    private void loginCheck(FirebaseAuth firebaseAuth) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            Log.e("TAG", "loginCheck: "+user.isEmailVerified());
            if (user.isEmailVerified()) {
                startActivity(new Intent(com.ajikartiko.go_wisuda_dosen.LoginActivity.this, com.ajikartiko.go_wisuda_dosen.HomeActivity.class));
            } else {
                startActivity(new Intent(com.ajikartiko.go_wisuda_dosen.LoginActivity.this, ResendEmailActivity.class));
            }
            finish();
        }
    }

    private void processLogin() {
        String email = emailInput.getText().toString().trim();
        if (email.isEmpty()) {
            emailInput.setError("masukan email");
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError("Masukan Email Yang valid");
        } else {
            emailInput.setError(null);
        }
        String pass = passwordInput.getText().toString().trim();
        if (pass.isEmpty()) {
            passwordInput.setError("Masukan Password");
        } else if (pass.length() < 8) {
            passwordInput.setError("Password Minimal 8 Digit");
        } else {
            passwordInput.setError(null);
        }

        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass)) {
            loginProgress.setTitle("LOGIN GO WISUDA");
            loginProgress.setMessage("Silahkan Tunggu, Akun Sedang Masuk....");
            loginProgress.show();
            mAuth.signInWithEmailAndPassword(email, pass)
                    .addOnSuccessListener(authResult -> {
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Email Atau Password Salah " + e.getMessage(), Toast.LENGTH_SHORT).show())
                    .addOnCompleteListener(task -> loginProgress.dismiss());
        }
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







