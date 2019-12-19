package com.example.smartsecuritysystem;

public class ModelRelay {
    String id;
    String nama;
    int kondisi;

    public ModelRelay(String id, String nama, int kondisi){
        this.id = id;
        this.nama = nama;
        this.kondisi = kondisi;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public int getKondisi() {
        return kondisi;
    }

    public void setKondisi(int kondisi) {
        this.kondisi = kondisi;
    }
}
