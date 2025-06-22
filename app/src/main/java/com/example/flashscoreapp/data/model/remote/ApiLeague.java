package com.example.flashscoreapp.data.model.remote;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class ApiLeague implements Serializable {
    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("logo")
    private String logo;
    @SerializedName("round")
    private String round;
    @SerializedName("country")
    private String country;

    // THÊM TRƯỜNG MỚI TỪ API
    @SerializedName("season")
    private int season;

    public int getId() { return id; }
    public String getName() { return name; }
    public String getLogo() { return logo; }
    public String getRound() { return round; }
    public String getCountry() { return country; }

    // THÊM GETTER MỚI
    public int getSeason() { return season; }
}