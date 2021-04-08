package com.ajikartiko.go_wisuda_dosen;

public class Users {
    private String image;
    private String email;

    public Users() {
    }

    public Users(String image, String email) {
        this.image = image;
        this.email = email;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
