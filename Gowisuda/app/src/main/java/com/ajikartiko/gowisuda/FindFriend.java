package com.ajikartiko.gowisuda;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriend extends AppCompatActivity {

    RecyclerView findfriendlist;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    FirebaseStorage firebaseStorage;
    FirebaseUser user;
    StorageReference storageReference;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friend);

        findfriendlist = (RecyclerView) findViewById(R.id.findfriendrecycle);
            findfriendlist.setHasFixedSize(true);
        findfriendlist.setLayoutManager(new LinearLayoutManager(this));


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        reference = FirebaseDatabase.getInstance().getReference("Dosen");

    }
    @Override
    protected void onStart(){
        super.onStart();
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        FirebaseRecyclerOptions<Find> options = new FirebaseRecyclerOptions.Builder<Find>()
                .setQuery(reference, Find.class).build();

        FirebaseRecyclerAdapter<Find, FindDosenViewHolder > adapter = new FirebaseRecyclerAdapter<Find, FindDosenViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final FindDosenViewHolder holder, final int position, @NonNull final Find model) {
                reference = FirebaseDatabase.getInstance().getReference("Dosen").child(model.image).child("profile");
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String namanya = snapshot.child("name").getValue(String.class);
                        holder.email.setText(namanya);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                if(model.image != null){
                storageReference.child("user profile dosen").child(model.image).child("Images").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).fit().centerInside().into(holder.profileimage);
                    }
                });}
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), Massage.class);;
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("id_dos",model.getImage());
                        intent.putExtra("em_dos",model.getEmail());
                        intent.putExtra("img",model.image);
                        getApplication().startActivity(intent);
                    }
                });




            }

            @NonNull
            @Override
            public FindDosenViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.all_userlayout, viewGroup, false);
                FindDosenViewHolder viewHolder = new FindDosenViewHolder(view);
                return viewHolder;
            }
        };

        findfriendlist.setAdapter(adapter);
        adapter.startListening();
    }
    public static class  FindDosenViewHolder extends RecyclerView.ViewHolder{

        TextView email;
        CircleImageView profileimage;

        public FindDosenViewHolder(@NonNull View itemView) {


            super(itemView);

            email = itemView.findViewById(R.id.allnama);
            profileimage = itemView.findViewById(R.id.alluserimage);

        }
    }


}