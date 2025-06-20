package com.example.flashscoreapp.data.model.remote;

import com.google.gson.annotations.SerializedName;

public class ApiPlayerDetail {
    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("number")
    private int number;
    @SerializedName("pos")
    private String pos; // Vị trí: G, D, M, F

    public int getId() { return id; }
    public String getName() { return name; }
    public int getNumber() { return number; }
    public String getPos() { return pos; }
}