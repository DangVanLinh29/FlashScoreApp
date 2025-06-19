package com.example.flashscoreapp.data.model.remote;

import com.google.gson.annotations.SerializedName;

// Lớp này làm khuôn để đọc dữ liệu từ API tìm kiếm đội bóng
public class ApiTeamResponse {
    @SerializedName("team")
    private ApiTeamInfo team;

    @SerializedName("venue")
    private ApiVenue venue;

    public ApiTeamInfo getTeam() { return team; }
    public ApiVenue getVenue() { return venue; }
}