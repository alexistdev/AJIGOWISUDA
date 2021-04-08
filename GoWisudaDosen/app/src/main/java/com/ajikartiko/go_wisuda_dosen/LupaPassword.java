package com.ajikartiko.go_wisuda_dosen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LupaPassword extends AppCompatActivity {

    private EditText passwordemail;
    private Button resetpassword;
    private FirebaseAuth mAunth;
    private TextView backlogin;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lupa_password);

        passwordemail = (EditText)  findViewById(R.id.fgtemail);
        resetpassword = (Button) findViewById(R.id.fgtbutton);
        mAunth = FirebaseAuth.getInstance();



        resetpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String useremail = passwordemail.getText().toString().trim();
                if (useremail.equals("")){
                    Toast.makeText(LupaPassword.this, "masukan email", Toast.LENGTH_SHORT).show();
                } else {
                    mAunth.sendPasswordResetEmail(useremail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull final Task<Void> task) {
                            final Query checkuser = FirebaseDatabase.getInstance().getReference("users").orderByChild("password").equalTo(mAunth.getUid());
                            checkuser.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()){
                                        String password = dataSnapshot.child("password").getValue().toString();





                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                            if (task.isSuccessful()){
                                Toast.makeText(LupaPassword.this, "Reset password terkirim ke email anda", Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(new Intent(LupaPassword.this, Login.class));
                            }else{
                                Toast.makeText(LupaPassword.this, "Error mengirim reset password", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        backlogin = (TextView) findViewById(R.id.fgtbacklogin);
        backlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backloginintent = new Intent(LupaPassword.this, Login.class);
                startActivity(backloginintent);
            }
        });


    }
    }