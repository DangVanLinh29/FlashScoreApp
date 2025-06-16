package com.example.flashscoreapp.data.api;

import com.example.flashscoreapp.data.model.ApiLeaguesResponse;
import com.example.flashscoreapp.data.model.ApiMatch;
import com.example.flashscoreapp.data.model.ApiResponse;
import com.example.flashscoreapp.data.model.ApiStandingsResponse;
import com.example.flashscoreapp.data.model.ApiStatisticsResponse;
import com.example.flashscoreapp.data.model.ApiTopScorerData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface ApiService {

    // Trả về danh sách các trận đấu
    @GET("fixtures")
    Call<ApiResponse<ApiMatch>> getFixturesByDate(
            @Query("date") String date,
            @Header("x-rapidapi-key") String apiKey,
            @Header("x-rapidapi-host") String apiHost
    );

    // Trả về danh sách các trận đấu (cho chi tiết hoặc kết quả giải đấu)
    @GET("fixtures")
    Call<ApiResponse<ApiMatch>> getFixtures(
            @Query("id") Integer fixtureId,
            @Query("league") Integer leagueId,
            @Query("season") Integer seasonYear,
            @Header("x-rapidapi-key") String apiKey,
            @Header("x-rapidapi-host") String apiHost
    );

    // Trả về danh sách cầu thủ ghi bàn
    @GET("players/topscorers")
    Call<ApiResponse<ApiTopScorerData>> getTopScorers(
            @Query("season") int seasonYear,
            @Query("league") int leagueId,
            @Header("x-rapidapi-key") String apiKey,
            @Header("x-rapidapi-host") String apiHost
    );

    // Các phương thức còn lại giữ nguyên cấu trúc cũ của chúng
    @GET("fixtures/statistics")
    Call<ApiStatisticsResponse> getMatchStatistics(
            @Query("fixture") int fixtureId,
            @Header("x-rapidapi-key") String apiKey,
            @Header("x-rapidapi-host") String apiHost
    );

    @GET("leagues")
    Call<ApiLeaguesResponse> getLeagues(
            @Header("x-rapidapi-key") String apiKey,
            @Header("x-rapidapi-host") String apiHost
    );

    @GET("standings")
    Call<ApiStandingsResponse> getStandings(
            @Query("league") int leagueId,
            @Query("season") int season,
            @Header("x-rapidapi-key") String apiKey,
            @Header("x-rapidapi-host") String apiHost
    );
}