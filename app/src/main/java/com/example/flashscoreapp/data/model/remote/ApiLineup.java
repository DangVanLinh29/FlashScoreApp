package com.example.flashscoreapp.data.model.remote;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ApiLineup {
    @SerializedName("team")
    private ApiTeamInfo team;
    @SerializedName("formation")
    private String formation;
    @SerializedName("startXI")
    private List<ApiLineupPlayer> startXI;
    @SerializedName("substitutes")
    private List<ApiLineupPlayer> substitutes;

    public ApiTeamInfo getTeam() { return team; }
    public String getFormation() { return formation; }
    public List<ApiLineupPlayer> getStartXI() { return startXI; }
    public List<ApiLineupPlayer> getSubstitutes() { return substitutes; }
}