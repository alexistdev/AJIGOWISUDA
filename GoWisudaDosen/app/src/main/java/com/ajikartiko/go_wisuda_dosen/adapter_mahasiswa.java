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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class adapter_mahasiswa extends RecyclerView.Adapter<adapter_mahasiswa.mahasiswaholder> {
    Context context;
    ArrayList<String> namaList;
    ArrayList<String> judulList;
    ArrayList<String> semesterList;
    ArrayList<String> imageURL;

    public adapter_mahasiswa() {
    }
    class mahasiswaholder extends RecyclerView.ViewHolder{

        View v;
        TextView nama,judul,semester;
        ImageView pp;

        public mahasiswaholder(@NonNull View itemView) {
            super(itemView);
            nama = itemView.findViewById(R.id.allnama);
            judul = itemView.findViewById(R.id.alljudul);
            semester = itemView.findViewById(R.id.allsemester);
            pp = itemView.findViewById(R.id.alluserimage);
            v = itemView;
        }
    }
    public adapter_mahasiswa(Context context, ArrayList<String> namaList, ArrayList<String> judulList, ArrayList<String> semesterList, ArrayList<String> imageURL) {
        this.context = context;
        this.namaList = namaList;
        this.judulList = judulList;
        this.semesterList = semesterList;
        this.imageURL = imageURL;
    }

    @NonNull
    @Override
    public mahasiswaholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.all_userlayout, parent, false);
        return new mahasiswaholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final mahasiswaholder holder, final int position) {

        if (imageURL.get(position) != null) {
            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
            StorageReference storageReference = firebaseStorage.getReference();
            storageReference.child("user profile Mahasiswa").child(imageURL.get(position)).child("Images").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).fit().centerInside().into(holder.pp);
                }
            });
        }
        holder.nama.setText(namaList.get(position));
        holder.judul.setText(judulList.get(position));
        holder.semester.setText(semesterList.get(position));
        holder.v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MessageBox.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("nama", namaList.get(position));
                intent.putExtra("img", imageURL.get(position));
                context.startActivity(intent);
            }

        });
    }

    @Override
    public int getItemCount() {
        return namaList.size();
    }
}
