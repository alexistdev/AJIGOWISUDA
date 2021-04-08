package com.ajikartiko.gowisuda;

public class userInformation {


    public String name;
    public String judulskripsi;
    public String semester;

    userInformation(){

    }



    public userInformation(String name, String judulskripsi, String semester){
        this.name = name;
        this.judulskripsi = judulskripsi;
        this.semester = semester;
    }

    public String getName() {
        return name;
    }

    public String getJudulskripsi() {
        return judulskripsi;
    }

    public String getSemester() {
        return semester;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setJudulskripsi(String judulskripsi) {
        this.judulskripsi = judulskripsi;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }
}
