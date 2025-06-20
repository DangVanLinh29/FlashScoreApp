package com.example.flashscoreapp.data.model.remote;

import com.google.gson.annotations.SerializedName;

public class ApiLineupPlayer {
    @SerializedName("player")
    private ApiPlayerDetail player;

    public ApiPlayerDetail getPlayer() { return player; }
}