package com.ajikartiko.go_wisuda_dosen;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.holder> {
    private Context context;
    private List<Users> mUsers;

    class holder extends RecyclerView.ViewHolder{

        View v;
        TextView nama;
        ImageView pp;

        public holder(@NonNull View itemView) {
            super(itemView);
            nama = (TextView) itemView.findViewById(R.id.allnama);
            pp = (ImageView)itemView.findViewById(R.id.alluserimage);
            v = itemView;
        }

    }

    public UserAdapter(Context context, List<Users> mUsers) {
        this.context = context;
        this.mUsers = mUsers;
    }

    @NonNull
    @Override
    public holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_chat, parent, false);
        return new holder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull final holder holder, int position) {
        DatabaseReference reference;
        final Users users = mUsers.get(position);

        if (users.getImage()!= null) {
            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
            StorageReference storageReference = firebaseStorage.getReference();
            storageReference.child("user profile Mahasiswa").child(users.getImage()).child("Images").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).fit().centerInside().into(holder.pp);
                }
            });
        }
        reference = FirebaseDatabase.getInstance().getReference("Mahasiswa").child(users.getImage()).child("profile");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String namanya = snapshot.child("name").getValue(String.class);
                holder.nama.setText(namanya);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MessageBox.class);;
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("id_dos",users.getImage());
                intent.putExtra("nama",users.getEmail());
                intent.putExtra("img",users.getImage());
                context.startActivity(intent);
            }

        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }
}
