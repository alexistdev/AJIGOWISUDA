package com.ajikartiko.gowisuda;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class Bimbingan extends AppCompatActivity {

    FirebaseUser fuser;
    CircleImageView imageView;
    TextView username;
    DatabaseReference reference;
    Intent intent;
    ImageButton btn_send;
    EditText text_send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bimbingan);

        imageView = findViewById(R.id.imagemessage);
        username = findViewById(R.id.username1);
        btn_send = findViewById(R.id.pesanbutton);
        text_send = findViewById(R.id.pesanedit);


        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mag = text_send.getText().toString();
                if (!mag.equals("")){
                    
                }else{
                    Toast.makeText(Bimbingan.this, "Tidak bisa mengirim pesan kosong...", Toast.LENGTH_SHORT).show();
                }
                text_send.setText("");
            }
        });


    }
    private void sendMessage (String sender, String receiver, String message){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("Sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);

        reference.child("charts").push().setValue(hashMap);
    }
}