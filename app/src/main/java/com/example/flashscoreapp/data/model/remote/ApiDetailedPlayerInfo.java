package com.example.flashscoreapp.data.model.remote;

import com.google.gson.annotations.SerializedName;

public class ApiDetailedPlayerInfo {
    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("nationality")
    private String nationality;

    @SerializedName("photo")
    private String photo;

    public int getId() { return id; }
    public String getName() { return name; }
    public String getNationality() { return nationality; }

    public String getPhoto() { return photo; }
}