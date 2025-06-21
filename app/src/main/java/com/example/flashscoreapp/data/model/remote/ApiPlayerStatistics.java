package com.example.flashscoreapp.data.model.remote;

import com.google.gson.annotations.SerializedName;

public class ApiPlayerStatistics {
    @SerializedName("games")
    private ApiPlayerGames games;

    public ApiPlayerGames getGames() { return games; }
}