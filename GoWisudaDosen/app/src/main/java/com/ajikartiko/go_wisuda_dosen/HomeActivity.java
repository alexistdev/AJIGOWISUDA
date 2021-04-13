package com.ajikartiko.go_wisuda_dosen;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.ajikartiko.go_wisuda_dosen.model.Mahasiswa;
import com.ajikartiko.go_wisuda_dosen.model.User;
import com.ajikartiko.go_wisuda_dosen.utils.FirestoreUtil;
import com.ajikartiko.go_wisuda_dosen.utils.UserType;
import com.ajikartiko.go_wisuda_dosen.utils.Utils;
import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity {
    private final FirestoreUtil firestoreUtil = new FirestoreUtil();
    private FirebaseAuth mAuth;
    private NavigationView navigationView;
    private User currentUser, otherUser;
    private final FirebaseAuth.AuthStateListener mAuthListener = this::loginCheck;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setUpToolbar();
        mAuth = FirebaseAuth.getInstance();
        Button btnBimbingan = findViewById(R.id.buttonbimbingan);
        navigationView = findViewById(R.id.navigation_menu);
        btnBimbingan.setOnClickListener(v -> {
            Intent historyIntent = new Intent(this, UserListActivity.class);
            historyIntent.putExtra("USER_TO", "chat");
            startActivity(historyIntent);
        });
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.nav_dasboard:
                    startActivity(new Intent(this, com.ajikartiko.go_wisuda_dosen.HomeActivity.class));
                    finish();
                    break;

                case R.id.nav_settting:
                    if (currentUser != null) {
                        Intent intent = new Intent(this, ProfileActivity.class);
                        intent.putExtra("CURRENT_USER", currentUser);
                        startActivity(intent);
                    }
                    break;

                case R.id.nav_logout:
                    FirebaseAuth.getInstance().signOut();
                    break;

                case R.id.nav_android: {
                    Toast.makeText(this, "TODO FOR CREATE FIEATURE", Toast.LENGTH_SHORT).show();
                    break;
                }

                case R.id.nav_share: {
                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Try now");
                    sharingIntent.putExtra(Intent.EXTRA_TEXT, "http://play.google.com/store/apps/detail?id=".concat(getPackageName()));
                    startActivity(Intent.createChooser(sharingIntent, "Share using"));
                }
                break;
            }
            return false;
        });
    }

    public void setUpToolbar() {
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    private void loginCheck(FirebaseAuth firebaseAuth) {
        if (firebaseAuth.getCurrentUser() != null) {
            firestoreUtil.createOrGetUserProfile(this::completedProfile);
        } else {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    private void completedProfile(User user) {
        currentUser = user;
        if (TextUtils.isEmpty(user.getName())) {
            startActivity(new Intent(this, com.ajikartiko.go_wisuda_dosen.CompleteProfileActivity.class));
            finish();
        } else {
            View header = navigationView.getHeaderView(0);
            TextView email = header.findViewById(R.id.headeremail);
            CircleImageView userPhoto = header.findViewById(R.id.headerfoto);

            if (currentUser != null) {
                if (currentUser.getImage() != null || !TextUtils.isEmpty(currentUser.getImage())) {
                    Glide.with(this).load(currentUser.getImage()).error(R.drawable.user).fallback(R.drawable.user).into(userPhoto);
                }
                email.setText(currentUser.getEmail());
            }

            if (currentUser.getType() == UserType.MAHASISWA && currentUser instanceof Mahasiswa) {
                firestoreUtil.userDocReference(((Mahasiswa) currentUser).getPembimbing()).get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        otherUser = Utils.documentToUser(documentSnapshot);
                    }
                });
            }
        }
    }

    public void goToHistory(View view) {
        Intent historyIntent = new Intent(this, UserListActivity.class);
        historyIntent.putExtra("USER_TO", "history");
        startActivity(historyIntent);
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