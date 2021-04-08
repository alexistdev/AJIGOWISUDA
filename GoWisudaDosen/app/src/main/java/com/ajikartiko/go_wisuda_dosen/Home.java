package com.ajikartiko.go_wisuda_dosen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class Home extends AppCompatActivity {

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    FirebaseAuth mAunt;
    FirebaseStorage firebaseStorage;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    TextView email1;
    CircleImageView foto2;
    NavigationView navigationView;
    Button btnbimbingan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        btnbimbingan = (Button)  findViewById(R.id.buttonbimbingan);
        email1 = (TextView) findViewById(R.id.headeremail);
        mAunt= FirebaseAuth.getInstance();
        firebaseUser = mAunt.getCurrentUser();
        foto2 = (CircleImageView) findViewById(R.id.headerfoto);
        databaseReference = FirebaseDatabase.getInstance().getReference();


        setUpToolbar();
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_menu);
        final View headerview = navigationView.getHeaderView(0);
        TextView emailnav = headerview.findViewById(R.id.headeremail);
        final CircleImageView userfoto = headerview.findViewById(R.id.headerfoto);

        firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReference();
        storageReference = firebaseStorage.getReference();
        storageReference.child("user profile dosen").child(mAunt.getUid()).child("Images").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).fit().centerInside().into(userfoto);
            }
        });

        emailnav.setText(firebaseUser.getEmail());


        btnbimbingan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home.this, Bimbingan.class));
            }
        });
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {


            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId())
                {
                    case  R.id.nav_dasboard:

                        Intent intent = new Intent(Home.this, Home.class);
                        startActivity(intent);
                        break;

                    case R.id.nav_mahasiswa:
                        Intent intent10 = new Intent(Home.this, FindMahasiswa.class);
                        startActivity(intent10);
                        break;

                    case R.id.nav_settting:
                        Intent intent1 = new Intent(Home.this, SettingEdit.class);
                        startActivity(intent1);
                        break;

                    case R.id.nav_logout:
                        mAunt.signOut();
                        startActivity(new Intent(Home.this, Login.class));
                        break;



//Paste your privacy policy link

                    case  R.id.nav_android:{
                        Intent intent2 = new Intent(Home.this, Setting.class);
                        startActivity(intent2);
                        break;
                    }
//
//                        Intent browserIntent  = new Intent(Intent.ACTION_VIEW , Uri.parse(""));
//                        startActivity(browserIntent);
//
//                    }
                    //       break;

                    case  R.id.nav_share:{

                        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                        sharingIntent.setType("text/plain");
                        String shareBody =  "http://play.google.com/store/apps/detail?id=" + getPackageName();
                        String shareSub = "Try now";
                        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareSub);
                        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                        startActivity(Intent.createChooser(sharingIntent, "Share using"));

                    }
                    break;
                }
                return false;
            }
        });
    }
    public void setUpToolbar() {
        drawerLayout = findViewById(R.id.drawerLayout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

    }


}


