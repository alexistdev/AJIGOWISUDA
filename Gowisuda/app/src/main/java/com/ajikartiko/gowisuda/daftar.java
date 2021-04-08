package com.ajikartiko.gowisuda;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class daftar extends AppCompatActivity {

    private TextView logindaf;
    private EditText etpassword, etemail;
    private Button button;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private ProgressDialog mProgress;
    FirebaseUser user;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Mahasiswa");
        user = FirebaseAuth.getInstance().getCurrentUser();
        etpassword = (EditText) findViewById(R.id.password);
        etemail = (EditText) findViewById(R.id.email);
        button = (Button) findViewById(R.id.btn);
        logindaf = (TextView) findViewById(R.id.logindidaftar);
        mAuth = FirebaseAuth.getInstance();
        mProgress = new ProgressDialog(this);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user !=null){
                    sendVerificationEmail();
                }

            }
        };


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRegistration();



            }

            private void startRegistration() {
                final String email = etemail.getText().toString().trim();
                if (email.isEmpty()){
                    etemail.setError("masukan Email");
                }else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    etemail.setError("Masukan Email Yang Valid");
                }else {
                    etemail.setError(null);
                }
                final String pass = etpassword.getText().toString().trim();
                if (pass.isEmpty()){
                    etpassword.setError("Masukan Password");
                }else if (pass.length()<8){
                    etpassword.setError("password Minimal 8 digit");
                }else{
                    etpassword.setError(null);
                }
                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass)) {
                    mProgress.setTitle("GO-WISUDA");
                    mProgress.setMessage("Silahkan Tunggu Sedang Membuat Akun...");
                    mProgress.show();
                    mProgress.setCanceledOnTouchOutside(false);
                    mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                String user_id = mAuth.getCurrentUser().getUid();
                                DatabaseReference current_user_db = mDatabase.child(user_id);
                                current_user_db.child("email").setValue(email);
                                current_user_db.child("password").setValue(pass);

                                startActivity(new Intent(daftar.this, Setting.class));
                                finish();
                                //sendVerificationEmail();
                                mProgress.dismiss();

                            }else {
                                Toast.makeText(daftar.this, "Register gagal", Toast.LENGTH_SHORT).show();
                                mProgress.dismiss();
                            }


                        }
                    });
                }
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
                    Toast.makeText(daftar.this, "Berhasil membuat akun, verifikasi email anda", Toast.LENGTH_SHORT).show();
                }else{
                    overridePendingTransition(0,0);
                    finish();
                    overridePendingTransition(0,0);
                    startActivity(getIntent());
                }

            }
        });
    }

    public void logindaftar(View view) {
        startActivity(new Intent(daftar.this, Login.class));
        finish();
    }
}





































