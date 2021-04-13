package com.ajikartiko.go_wisuda_dosen.model;


import com.ajikartiko.go_wisuda_dosen.utils.UserType;

public class Mahasiswa extends User {
    private String judulSkripsi = "";
    private Integer semester = 0;
    private String pembimbing = "";
    public Mahasiswa() {
        super();
    }

    public Mahasiswa(String userId,String name, String email, String image, UserType type) {
        super(userId, name, email, image, type);
    }

    public Mahasiswa(String userId, String name, String email, String image, UserType type, String judulSkripsi, Integer semester, String pembimbing) {
        super(userId, name, email, image, type);
        this.judulSkripsi = judulSkripsi;
        this.semester = semester;
        this.pembimbing = pembimbing;
    }

    public String getJudulSkripsi() {
        return judulSkripsi;
    }

    public void setJudulSkripsi(String judulSkripsi) {
        this.judulSkripsi = judulSkripsi;
    }

    public Integer getSemester() {
        return semester;
    }

    public void setSemester(Integer semester) {
        this.semester = semester;
    }

    public String getPembimbing() {
        return pembimbing;
    }

    public void setPembimbing(String pembimbing) {
        this.pembimbing = pembimbing;
    }
}
