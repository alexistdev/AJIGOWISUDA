package com.ajikartiko.gowisuda;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Massage extends AppCompatActivity {

    RecyclerView recyclerView;
    ImageButton massagesend1;
    TextView massageemail1;
    CircleImageView fotomassage1;
    EditText massage90;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;
    FirebaseUser firebaseUser;
    Intent intent;
    MessageAdapter messageAdapter;
    List<ChatModel> mchat;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_massage);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        recyclerView = findViewById(R.id.recyclermassgae);
        massageemail1 = findViewById(R.id.messageemail);
        massage90 = findViewById(R.id.massagekirim);
        massagesend1 = findViewById(R.id.massagesend);
        fotomassage1 = findViewById(R.id.massageimage);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        recyclerView = findViewById(R.id.recyclermassgae);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        intent = getIntent();
        final String id_dosen = intent.getStringExtra("id_dos");
        final String em_dosen = intent.getStringExtra("em_dos");
        String img_dosen = intent.getStringExtra("img");
        reference = FirebaseDatabase.getInstance().getReference("Dosen").child(img_dosen).child("profile");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String namanya = snapshot.child("name").getValue(String.class);
                massageemail1.setText(namanya);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if(img_dosen != null){
            storageReference.child("user profile dosen").child(img_dosen).child("Images").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).fit().centerInside().into(fotomassage1);
                }
            });}

        massagesend1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mesaage = massage90.getText().toString().trim();
                if (!mesaage.equals("")){
                    sendmesaage(firebaseUser.getUid(), id_dosen,em_dosen, mesaage);


                }else{
                    Toast.makeText(Massage.this, "Tidak bisa ngirim pesan kosong...", Toast.LENGTH_SHORT).show();

                }
                massage90.setText("");

            }
        });
        ReadMesages();

    }

    private void sendmesaage(final String sender, final String receiver, final String id_receiver, final String mesaage) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> hashMap =new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("id_receiver", id_receiver);
        hashMap.put("message", mesaage);
        databaseReference.child("chats").push().setValue(hashMap);

        final DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("History_chat");
        databaseReference2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                databaseReference2.child(sender).child(receiver).child("id").setValue(receiver+sender);
                databaseReference2.child(receiver).child(sender).child("id").setValue(sender+receiver);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void ReadMesages(){
        final String id_dosen = intent.getStringExtra("id_dos");
        final String id_mahasiswa = firebaseUser.getUid();
        mchat = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference().child("chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mchat.clear();
                for (DataSnapshot snapshot :dataSnapshot.getChildren()){
                    ChatModel chatModel = snapshot.getValue(ChatModel.class);
                    if (chatModel.getReceiver().equals(id_dosen) &&
                            chatModel.getSender().equals(id_mahasiswa)
                            || chatModel.getReceiver().equals(id_mahasiswa)
                            && chatModel.getSender().equals(id_dosen)){
                        mchat.add(chatModel);
                    }
                    messageAdapter = new MessageAdapter(Massage.this, mchat);
                    recyclerView.setAdapter(messageAdapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}