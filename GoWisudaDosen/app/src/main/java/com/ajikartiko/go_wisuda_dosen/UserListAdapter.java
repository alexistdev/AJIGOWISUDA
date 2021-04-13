package com.ajikartiko.go_wisuda_dosen;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.ajikartiko.go_wisuda_dosen.model.Mahasiswa;
import com.ajikartiko.go_wisuda_dosen.model.User;
import com.ajikartiko.go_wisuda_dosen.utils.OnItemClickListener;
import com.bumptech.glide.Glide;

public class UserListAdapter extends ListAdapter<User, UserListAdapter.UserViewHolder> {
    private final OnItemClickListener listener;

    protected UserListAdapter(OnItemClickListener listener) {
        super(User.DIFF_CALLBACK);
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UserViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = getItem(position);
        if (user != null) {
            holder.bindTo(user, listener);
        }
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout root;
        ImageView imageView;
        TextView name, title;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.userRoot);
            imageView = itemView.findViewById(R.id.profile_pic_imageView);
            name = itemView.findViewById(R.id.userName);
            title = itemView.findViewById(R.id.title);
        }

        public void bindTo(User user, final OnItemClickListener listener) {
            Glide.with(imageView.getContext()).load(user.getImage()).fallback(R.drawable.user).error(R.drawable.user).into(imageView);
            name.setText(user.getName());
            if (user instanceof Mahasiswa) {
                title.setText(((Mahasiswa) user).getJudulSkripsi());
            }
            root.setOnClickListener(v -> listener.onItemClick(user));
        }
    }
}
