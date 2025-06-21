package com.example.flashscoreapp.data.api;

import com.example.flashscoreapp.data.model.remote.ApiLeaguesResponse;
import com.example.flashscoreapp.data.model.remote.ApiMatch;
import com.example.flashscoreapp.data.model.remote.ApiPlayerResponse;
import com.example.flashscoreapp.data.model.remote.ApiResponse;
import com.example.flashscoreapp.data.model.remote.ApiStandingsResponse;
import com.example.flashscoreapp.data.model.remote.ApiStatisticsResponse;
import com.example.flashscoreapp.data.model.remote.ApiTopScorerData;
import com.example.flashscoreapp.data.model.remote.ApiTeamResponse;
import com.example.flashscoreapp.data.model.remote.ApiLineup;
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

    @GET("fixtures")
    Call<ApiResponse<ApiMatch>> getFixturesByDateRange(
            @Query("from") String fromDate,
            @Query("to") String toDate,
            @Header("x-rapidapi-key") String apiKey,
            @Header("x-rapidapi-host") String apiHost
    );

    @GET("fixtures")
    Call<ApiResponse<ApiMatch>> getLiveFixtures(
            @Query("live") String live,
            @Header("x-rapidapi-key") String apiKey,
            @Header("x-rapidapi-host") String apiHost
    );
    @GET("teams")
    Call<ApiResponse<ApiTeamResponse>> searchTeams(
            @Query("search") String name,
            @Header("x-rapidapi-key") String apiKey,
            @Header("x-rapidapi-host") String apiHost
    );

    @GET("leagues")
    Call<ApiLeaguesResponse> searchLeagues(
            @Query("search") String name,
            @Header("x-rapidapi-key") String apiKey,
            @Header("x-rapidapi-host") String apiHost
    );

    @GET("fixtures/lineups")
    Call<ApiResponse<ApiLineup>> getLineups(
            @Query("fixture") int fixtureId,
            @Header("x-rapidapi-key") String apiKey,
            @Header("x-rapidapi-host") String apiHost
    );
    @GET("fixtures")
    Call<ApiResponse<ApiMatch>> getLastFixturesForTeam(
            @Query("team") int teamId,
            @Query("last") int count,
            @Header("x-rapidapi-key") String apiKey,
            @Header("x-rapidapi-host") String apiHost
    );

    @GET("players")
    Call<ApiResponse<ApiPlayerResponse>> getPlayersForTeam(
            @Query("team") int teamId,
            @Query("season") int season,
            @Header("x-rapidapi-key") String apiKey,
            @Header("x-rapidapi-host") String apiHost
    );

    @GET("fixtures")
    Call<ApiResponse<ApiMatch>> getFixturesByLeagueAndSeason(
            @Query("league") int leagueId,
            @Query("season") int season,
            @Query("status") String status,
            @Header("x-rapidapi-key") String apiKey,
            @Header("x-rapidapi-host") String apiHost
    );

    @GET("fixtures")
    Call<ApiResponse<ApiMatch>> getFixturesForTeam(
            @Query("team") int teamId,
            @Query("season") int season,
            @Header("x-rapidapi-key") String apiKey,
            @Header("x-rapidapi-host") String apiHost
    );

    @GET("teams")
    Call<ApiResponse<ApiTeamResponse>> getTeamDetails(
            @Query("id") int teamId,
            @Header("x-rapidapi-key") String apiKey,
            @Header("x-rapidapi-host") String apiHost
    );

    @GET("fixtures")
    Call<ApiResponse<ApiMatch>> getFixturesForTeamByStatus(
            @Query("team") int teamId,
            @Query("season") int season,
            @Query("status") String status,
            @Header("x-rapidapi-key") String apiKey,
            @Header("x-rapidapi-host") String apiHost
    );

    @GET("fixtures")
    Call<ApiResponse<ApiMatch>> getNextFixturesForTeam(
            @Query("team") int teamId,
            @Query("next") int count,
            @Header("x-rapidapi-key") String apiKey,
            @Header("x-rapidapi-host") String apiHost
    );
}