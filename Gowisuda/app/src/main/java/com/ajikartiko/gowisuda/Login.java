package com.ajikartiko.gowisuda;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class Login extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button button;
    private EditText loginemail;
    private EditText loginpassword;
    private TextView signup, forgetpass;
    private ProgressDialog loginProgres;
    private DatabaseReference mDatabaseRefern;
    private FirebaseUser firebaseUser;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setContentView(R.layout.activity_login);
        button = (Button) findViewById(R.id.btnlogin);
        forgetpass = (TextView) findViewById(R.id.fgtpass);
        loginemail = (EditText) findViewById(R.id.emaillogin);
        loginpassword = (EditText) findViewById(R.id.passlogin);
        signup = (TextView) findViewById(R.id.txtsignup);
        loginProgres = new ProgressDialog(this);
        mDatabaseRefern = FirebaseDatabase.getInstance().getReference().child("Mahasiswa");
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                startActivity(new Intent(Login.this, Home.class));
            }
        };
        forgetpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent forgetintent = new Intent(Login.this, Forgetpassword.class);
                startActivity(forgetintent);
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signupintent = new Intent(Login.this, daftar.class);
                startActivity(signupintent);
            }
        });


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkLogin();


            }


            private void checkLogin() {
                String email = loginemail.getText().toString().trim();
                if (email.isEmpty()){
                    loginemail.setError("masukan email");
                }else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    loginemail.setError("Masukan Email Yang valid");
                }else{
                    loginemail.setError(null);
                }
                String pass = loginpassword.getText().toString().trim();
                if (pass.isEmpty()){
                    loginpassword.setError("Masukan Password");
                }else if (pass.length()<8){
                    loginpassword.setError("Password Minimal 8 Digit");
                }else{
                    loginpassword.setError(null);
                }
                if (!TextUtils.isEmpty(email) &&!TextUtils.isEmpty(pass))
                {
                    loginProgres.setTitle("LOGIN GO WISUDA");
                    loginProgres.setMessage("Silahkan Tunggu, Akun Sedang Masuk....");
                    loginProgres.show();
                    mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful())
                            {
                                loginProgres.dismiss();
                                checkUserExist();
                            }
                            else
                            {
                                loginProgres.dismiss();
                                Toast.makeText(Login.this, "Email Atau Password Salah", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }

        });


    }

    private void CheckVerfikasiemail() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user.isEmailVerified()){
            startActivity(new Intent(Login.this, Home.class));
            finish();
        }else{
            LayoutInflater inflater = getLayoutInflater();
            View alertLayout = inflater.inflate(R.layout.resend, null);
            final AlertDialog.Builder alert = new AlertDialog.Builder(this);
            final Button buttonresend = alertLayout.findViewById(R.id.buttonresend1);
            final Button buttonbackresend = alertLayout.findViewById(R.id.buttonresend2);
            alert.setView(alertLayout);
            alert.setCancelable(false);
            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            alert.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(Login.this, ResendEmail.class));
                    finish();
                    buttonresend.onEditorAction(EditorInfo.IME_ACTION_DONE);

                }
            });
            AlertDialog dialog = alert.create();
            dialog.show();

        }
    }


    private void checkUserExist() {
        final String user_id=mAuth.getCurrentUser().getUid();
        mDatabaseRefern.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(user_id))
                {
                    CheckVerfikasiemail();
                }
                else
                {
                    Toast.makeText(Login.this, "Buat akun anda", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

    }
}







