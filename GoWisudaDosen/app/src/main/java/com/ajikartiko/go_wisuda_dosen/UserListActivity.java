package com.ajikartiko.go_wisuda_dosen;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ajikartiko.go_wisuda_dosen.model.Mahasiswa;
import com.ajikartiko.go_wisuda_dosen.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class UserListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private UserListAdapter adapter;
    private String goTo = "chat";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Mahasiswa Bimbingan");
        }
        goTo = getIntent().getStringExtra("USER_TO");
        adapter = new UserListAdapter(item -> {
            if (item instanceof User) {
                Intent intent = (goTo.equalsIgnoreCase("chat")) ? new Intent(this, ChatActivity.class) : new Intent(this, HistoryActivity.class);
                intent.putExtra("OTHER_USER", (User) item);
                startActivity(intent);
            }
        });
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(adapter);

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("users").whereEqualTo("pembimbing", FirebaseAuth.getInstance().getCurrentUser().getUid()).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<User> users  = new ArrayList<>();
                    for (QueryDocumentSnapshot snapshot: queryDocumentSnapshots) {
                        User mahasiswa = snapshot.toObject(Mahasiswa.class);
                        mahasiswa.setUserId(snapshot.getId());
                        users.add(mahasiswa);
                    }
                    adapter.submitList(users);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed" + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}