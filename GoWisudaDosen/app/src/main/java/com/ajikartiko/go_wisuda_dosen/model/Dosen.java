package com.ajikartiko.go_wisuda_dosen.model;


import com.ajikartiko.go_wisuda_dosen.utils.UserType;

public class Dosen extends User {
    public Dosen() {
        super();
    }

    public Dosen(String userId, String name, String email, String image, UserType type) {
        super(userId, name, email, image, type);
    }
}
