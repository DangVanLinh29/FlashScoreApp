package com.example.flashscoreapp.data.model;
import com.google.gson.annotations.SerializedName;

public class ApiLeague {
    @SerializedName("name")
    private String name;
    @SerializedName("round")
    private String round;

    public String getName() { return name; }
    public String getRound() { return round;}
}