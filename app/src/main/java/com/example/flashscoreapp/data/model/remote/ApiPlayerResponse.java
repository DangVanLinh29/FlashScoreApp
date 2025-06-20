package com.example.flashscoreapp.data.model.remote;

import com.google.gson.annotations.SerializedName;

public class ApiPlayerResponse {
    @SerializedName("player")
    private ApiDetailedPlayerInfo player;

    public ApiDetailedPlayerInfo getPlayer() {
        return player;
    }
}