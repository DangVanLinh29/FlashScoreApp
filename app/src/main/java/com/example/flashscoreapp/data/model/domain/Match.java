package com.example.flashscoreapp.data.model.domain;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Match implements Serializable {
    @SerializedName("matchId")
    private int matchId;
    @SerializedName("league")
    private League league;
    @SerializedName("homeTeam")
    private Team homeTeam;
    @SerializedName("awayTeam")
    private Team awayTeam;
    @SerializedName("matchTime")
    private long matchTime;
    @SerializedName("status")
    private String status;
    @SerializedName("score")
    private Score score;
    @SerializedName("round")
    private String round;

    // THÊM TRƯỜNG MỚI ĐỂ LƯU MÙA GIẢI
    @SerializedName("season")
    private int season;

    // SỬA LẠI CONSTRUCTOR ĐỂ NHẬN THÊM `season` (9 tham số)
    public Match(int matchId, League league, Team homeTeam, Team awayTeam, long matchTime, String status, Score score, String round, int season) {
        this.matchId = matchId;
        this.league = league;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.matchTime = matchTime;
        this.status = status;
        this.score = score;
        this.round = round;
        this.season = season; // Gán giá trị
    }

    // Getters for all fields
    public int getMatchId() { return matchId; }
    public League getLeague() { return league; }
    public Team getHomeTeam() { return homeTeam; }
    public Team getAwayTeam() { return awayTeam; }
    public long getMatchTime() { return matchTime; }
    public String getStatus() { return status; }
    public Score getScore() { return score; }
    public String getRound() { return round; }

    // THÊM GETTER MỚI
    public int getSeason() { return season; }
}