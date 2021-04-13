package com.ajikartiko.gowisuda.model;

import com.ajikartiko.gowisuda.utils.UserType;

import java.io.Serializable;

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
}
