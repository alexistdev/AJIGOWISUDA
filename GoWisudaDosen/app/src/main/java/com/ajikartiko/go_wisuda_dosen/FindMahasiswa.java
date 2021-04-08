package com.ajikartiko.go_wisuda_dosen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindMahasiswa extends AppCompatActivity {

    RecyclerView findfriendlist;
    Uri imagePath;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    FirebaseStorage firebaseStorage;
    FirebaseUser user;
    ArrayList<String> nama;
    ArrayList<String> judul;
    ArrayList<String> semester;
    ArrayList<String> foto;
    StorageReference storageReference;
    DatabaseReference reference;
    adapter_mahasiswa adapter_mahasiswa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_mahasiswa);

        findfriendlist = (RecyclerView) findViewById(R.id.findfriendrecycle);
        findfriendlist.setHasFixedSize(true);
        findfriendlist.setLayoutManager(new LinearLayoutManager(this));


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        reference = FirebaseDatabase.getInstance().getReference("Mahasiswa");

        nama = new ArrayList<>();
        judul = new ArrayList<>();
        semester = new ArrayList<>();
        foto = new ArrayList<>();
    }

    @Override
    protected void onStart() {
        super.onStart();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                nama.clear();
                judul.clear();
                semester.clear();
                foto.clear();
                findfriendlist.removeAllViews();
                for (DataSnapshot snapshot2: snapshot.getChildren()){
                    final String id = snapshot2.getKey();
                    reference.child(id).child("profile").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {


                            String namanya = snapshot.child("name").getValue(String.class);
                            String judulnya = snapshot.child("judulskripsi").getValue(String.class);
                            String semesternya = snapshot.child("semester").getValue(String.class);

                            nama.add(namanya);
                            judul.add(judulnya);
                            semester.add(semesternya);
                            foto.add(id);

                            adapter_mahasiswa = new adapter_mahasiswa(FindMahasiswa.this, nama,judul,semester,foto);
                            findfriendlist.setAdapter(adapter_mahasiswa);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}