package com.example.flashscoreapp.data.model.remote;

import com.google.gson.annotations.SerializedName;

public class ApiPlayerGames {
    @SerializedName("position")
    private String position;

    // SỬA DÒNG NÀY: từ int thành Integer
    @SerializedName("number")
    private Integer number;

    public String getPosition() { return position; }

    // SỬA DÒNG NÀY: từ int thành Integer
    public Integer getNumber() { return number; }
}