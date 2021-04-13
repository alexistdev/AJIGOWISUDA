package com.ajikartiko.go_wisuda_dosen.model;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.ajikartiko.go_wisuda_dosen.utils.UserType;

import java.io.Serializable;
import java.util.Objects;

public abstract class User implements Serializable {
    private String userId;
    private String name;
    private String email;
    private String image;
    private UserType type;

    public User() { }

    public User(String userId, String name, String email, String image, UserType type) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.image = image;
        this.type = type;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public UserType getType() {
        return type;
    }

    public void setType(UserType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public static final DiffUtil.ItemCallback<User> DIFF_CALLBACK = new DiffUtil.ItemCallback<User>() {
        @Override
        public boolean areItemsTheSame(@NonNull User oldItem, @NonNull User newItem) {
            return oldItem.userId.equals(newItem.userId);
        }

        @Override
        public boolean areContentsTheSame(@NonNull User oldItem, @NonNull User newItem) {
            return oldItem.equals(newItem);
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(userId, user.userId) && Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, email);
    }
}
