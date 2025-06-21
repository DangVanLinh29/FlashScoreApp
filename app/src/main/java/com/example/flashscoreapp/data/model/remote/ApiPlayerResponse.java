package com.example.flashscoreapp.data.model.remote;

import com.google.gson.annotations.SerializedName;
import java.util.List;
public class ApiPlayerResponse {
    @SerializedName("player")
    private ApiDetailedPlayerInfo player;


    @SerializedName("statistics")
    private List<ApiPlayerStatistics> statistics;

    public ApiDetailedPlayerInfo getPlayer() {
        return player;
    }


    public List<ApiPlayerStatistics> getStatistics() {
        return statistics;
    }
}