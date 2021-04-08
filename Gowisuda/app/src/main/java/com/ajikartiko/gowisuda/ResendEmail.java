package com.ajikartiko.gowisuda;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ResendEmail extends AppCompatActivity {

    MaterialTextView emailresend;
    Button buttonresend;
    FirebaseUser firebaseUser;
    FirebaseAuth mAunt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resend_email);

        emailresend = (MaterialTextView) findViewById(R.id.resendmail);
        buttonresend = (Button) findViewById(R.id.resendbutton);
        mAunt= FirebaseAuth.getInstance();
        firebaseUser = mAunt.getCurrentUser();

        emailresend.setText(firebaseUser.getEmail());
        buttonresend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendVerificationEmail();
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(ResendEmail.this, Login.class));

            }

            private void sendVerificationEmail() {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            FirebaseAuth.getInstance().signOut();
                            Toast.makeText(ResendEmail.this, "Sudah Dikirim Ulang, Cek Email anda", Toast.LENGTH_SHORT)
                                    .show();
                        }else{
                            overridePendingTransition(0,0);
                            finish();
                            overridePendingTransition(0,0);
                            startActivity(getIntent());
                        }

                    }
                });
            }
        });





    }
}