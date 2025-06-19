package com.example.flashscoreapp.data.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.flashscoreapp.data.api.ApiService;
import com.example.flashscoreapp.data.api.RetrofitClient;
import com.example.flashscoreapp.data.db.AppDatabase;
import com.example.flashscoreapp.data.db.MatchDao;
import com.example.flashscoreapp.data.db.TeamDao;
import com.example.flashscoreapp.data.model.domain.League;
import com.example.flashscoreapp.data.model.domain.Match;
import com.example.flashscoreapp.data.model.domain.MatchDetails;
import com.example.flashscoreapp.data.model.domain.MatchEvent;
import com.example.flashscoreapp.data.model.domain.MatchStatistic;
import com.example.flashscoreapp.data.model.domain.Score;
import com.example.flashscoreapp.data.model.domain.StandingItem;
import com.example.flashscoreapp.data.model.domain.Team;
import com.example.flashscoreapp.data.model.local.FavoriteMatch;
import com.example.flashscoreapp.data.model.local.FavoriteTeam;
import com.example.flashscoreapp.data.model.remote.ApiEvent;
import com.example.flashscoreapp.data.model.remote.ApiLeagueData;
import com.example.flashscoreapp.data.model.remote.ApiLeaguesResponse;
import com.example.flashscoreapp.data.model.remote.ApiMatch;
import com.example.flashscoreapp.data.model.remote.ApiResponse;
import com.example.flashscoreapp.data.model.remote.ApiStandingsResponse;
import com.example.flashscoreapp.data.model.remote.ApiStatisticItem;
import com.example.flashscoreapp.data.model.remote.ApiStatisticsResponse;
import com.example.flashscoreapp.data.model.remote.ApiTeamStatistics;
import com.example.flashscoreapp.data.model.remote.ApiTopScorerData;
import com.example.flashscoreapp.data.model.remote.ApiTeamResponse;
import com.example.flashscoreapp.data.model.remote.ApiTeamInfo;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MatchRepository {
    private final ApiService apiService;
    private final MatchDao matchDao;
    private final TeamDao teamDao;
    private final ExecutorService executorService;
    private final String API_KEY = "5acb3c9129msh55387e600567f2fp11700djsnc70a2511d9c5";
    private final String API_HOST = "api-football-v1.p.rapidapi.com";

    public MatchRepository(Application application) {
        this.apiService = RetrofitClient.getApiService();
        AppDatabase database = AppDatabase.getDatabase(application);
        this.matchDao = database.matchDao();
        this.teamDao = database.teamDao();
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<Match>> getAllFavoriteMatches() {
        return matchDao.getAllFavoriteMatches();
    }

    public void addFavorite(Match match) {
        FavoriteMatch favoriteMatch = new FavoriteMatch(match.getMatchId(), match.getMatchTime(), match);
        executorService.execute(() -> matchDao.addFavorite(favoriteMatch));
    }

    public void removeFavorite(Match match) {
        FavoriteMatch favoriteMatch = new FavoriteMatch(match.getMatchId(), match.getMatchTime(), match);
        executorService.execute(() -> matchDao.removeFavorite(favoriteMatch));
    }

    public LiveData<List<FavoriteTeam>> getAllFavoriteTeams() {
        return teamDao.getAllFavoriteTeams();
    }

    public void addFavoriteTeam(Team team) {
        FavoriteTeam favoriteTeam = new FavoriteTeam(team.getId());
        executorService.execute(() -> teamDao.addFavorite(favoriteTeam));
    }

    public void removeFavoriteTeam(Team team) {
        FavoriteTeam favoriteTeam = new FavoriteTeam(team.getId());
        executorService.execute(() -> teamDao.removeFavorite(favoriteTeam));
    }
    public LiveData<MatchDetails> getMatchDetailsFromApi(int matchId) {
        final MutableLiveData<MatchDetails> detailsData = new MutableLiveData<>();
        final MatchDetails combinedDetails = new MatchDetails(matchId, new ArrayList<>(), new ArrayList<>());

        apiService.getFixtures(matchId, null, null, API_KEY, API_HOST).enqueue(new Callback<ApiResponse<ApiMatch>>() {
            @Override
            public void onResponse(Call<ApiResponse<ApiMatch>> call, Response<ApiResponse<ApiMatch>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().getResponse().isEmpty()) {
                    ApiMatch apiMatch = response.body().getResponse().get(0);
                    combinedDetails.setEvents(convertApiEventsToDomain(apiMatch.getEvents()));
                    if (apiMatch.getFixture() != null) {
                        combinedDetails.setReferee(apiMatch.getFixture().getReferee());
                        if (apiMatch.getFixture().getVenue() != null) {
                            combinedDetails.setStadium(apiMatch.getFixture().getVenue().getName());
                        }
                    }
                    detailsData.postValue(combinedDetails);
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<ApiMatch>> call, Throwable t) {}
        });

        apiService.getMatchStatistics(matchId, API_KEY, API_HOST).enqueue(new Callback<ApiStatisticsResponse>() {
            @Override
            public void onResponse(Call<ApiStatisticsResponse> call, Response<ApiStatisticsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    combinedDetails.setStatistics(convertApiStatisticsToDomain(response.body().getResponse()));
                    detailsData.postValue(combinedDetails);
                }
            }
            @Override
            public void onFailure(Call<ApiStatisticsResponse> call, Throwable t) {}
        });

        return detailsData;
    }

    private List<MatchStatistic> convertApiStatisticsToDomain(List<ApiTeamStatistics> apiStats) {
        List<MatchStatistic> domainStats = new ArrayList<>();
        if (apiStats == null || apiStats.size() < 2) return domainStats;
        ApiTeamStatistics homeTeamStats = apiStats.get(0);
        ApiTeamStatistics awayTeamStats = apiStats.get(1);
        Map<String, MatchStatistic> statsMap = new LinkedHashMap<>();
        for (ApiStatisticItem item : homeTeamStats.getStatistics()) {
            statsMap.put(item.getType(), new MatchStatistic(item.getType(), String.valueOf(item.getValue()), "0"));
        }
        for (ApiStatisticItem item : awayTeamStats.getStatistics()) {
            if (statsMap.containsKey(item.getType())) {
                statsMap.get(item.getType()).setAwayValue(String.valueOf(item.getValue()));
            }
        }
        domainStats.addAll(statsMap.values());
        return domainStats;
    }

    private List<MatchEvent> convertApiEventsToDomain(List<ApiEvent> apiEvents) {
        List<MatchEvent> domainEvents = new ArrayList<>();
        if (apiEvents == null) return domainEvents;
        for (ApiEvent apiEvent : apiEvents) {
            String finalEventType = "Card".equals(apiEvent.getType()) ? apiEvent.getDetail() : apiEvent.getType();
            MatchEvent matchEvent = new MatchEvent(
                    apiEvent.getTeam().getId(),
                    apiEvent.getTime().getElapsed(),
                    apiEvent.getTeam().getName(),
                    apiEvent.getPlayer().getName(),
                    finalEventType
            );
            domainEvents.add(matchEvent);
        }
        return domainEvents;
    }

    public LiveData<List<Match>> getMatchesByDateFromApi(String date) {
        final MutableLiveData<List<Match>> data = new MutableLiveData<>();
        apiService.getFixturesByDate(date, API_KEY, API_HOST).enqueue(new Callback<ApiResponse<ApiMatch>>() {
            @Override
            public void onResponse(Call<ApiResponse<ApiMatch>> call, Response<ApiResponse<ApiMatch>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.postValue(convertApiMatchesToDomain(response.body().getResponse()));
                } else {
                    data.postValue(null);
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<ApiMatch>> call, Throwable t) {
                data.postValue(null);
            }
        });
        return data;
    }

    private List<Match> convertApiMatchesToDomain(List<ApiMatch> apiMatches) {
        List<Match> domainMatches = new ArrayList<>();
        if (apiMatches == null) return domainMatches;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

        for (ApiMatch apiMatch : apiMatches) {
            try {
                // Tạo đối tượng League với đầy đủ thông tin
                League league = new League(
                        apiMatch.getLeague().getId(),
                        apiMatch.getLeague().getName(),
                        apiMatch.getLeague().getLogo(),
                        apiMatch.getLeague().getCountry()
                );

                // Tạo đối tượng Team với đầy đủ thông tin
                Team homeTeam = new Team(
                        apiMatch.getTeams().getHome().getId(),
                        apiMatch.getTeams().getHome().getName(),
                        apiMatch.getTeams().getHome().getLogo()
                );
                Team awayTeam = new Team(
                        apiMatch.getTeams().getAway().getId(),
                        apiMatch.getTeams().getAway().getName(),
                        apiMatch.getTeams().getAway().getLogo()
                );

                Score score = new Score(apiMatch.getGoals().getHome(), apiMatch.getGoals().getAway());

                long matchTime = sdf.parse(apiMatch.getFixture().getDate()).getTime();

                // SỬA LẠI LỜI GỌI CONSTRUCTOR ĐỂ TRUYỀN ĐỦ 8 THAM SỐ
                Match match = new Match(
                        apiMatch.getFixture().getId(),
                        league,
                        homeTeam,
                        awayTeam,
                        matchTime,
                        apiMatch.getFixture().getStatus().getShortStatus(),
                        score,
                        apiMatch.getLeague().getRound()
                );
                domainMatches.add(match);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return domainMatches;
    }

    public LiveData<List<ApiLeagueData>> getLeaguesWithSeasons() {
        final MutableLiveData<List<ApiLeagueData>> data = new MutableLiveData<>();
        apiService.getLeagues(API_KEY, API_HOST).enqueue(new Callback<ApiLeaguesResponse>() {
            @Override
            public void onResponse(Call<ApiLeaguesResponse> call, Response<ApiLeaguesResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.postValue(response.body().getResponse());
                } else {
                    data.postValue(null);
                }
            }
            @Override
            public void onFailure(Call<ApiLeaguesResponse> call, Throwable t) {
                data.postValue(null);
            }
        });
        return data;
    }

    public LiveData<List<Match>> getResultsForLeague(int leagueId, int seasonYear) {
        final MutableLiveData<List<Match>> data = new MutableLiveData<>();

        // Gọi đến endpoint "fixtures" với các tham số league và season
        // Ta truyền null cho fixtureId vì không cần tìm theo trận đấu cụ thể
        apiService.getFixtures(null, leagueId, seasonYear, API_KEY, API_HOST).enqueue(new Callback<ApiResponse<ApiMatch>>() {
            @Override
            public void onResponse(Call<ApiResponse<ApiMatch>> call, Response<ApiResponse<ApiMatch>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Match> allMatches = convertApiMatchesToDomain(response.body().getResponse());

                    // Lọc ra các trận đã kết thúc (có trạng thái là "FT" - Full Time)
                    List<Match> finishedMatches = allMatches.stream()
                            .filter(match -> "FT".equals(match.getStatus()))
                            .collect(Collectors.toList());
                    data.postValue(finishedMatches);
                } else {
                    data.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ApiMatch>> call, Throwable t) {
                data.postValue(null);
            }
        });
        return data;
    }

    public LiveData<List<List<StandingItem>>> getStandings(int leagueId, int season) {
        final MutableLiveData<List<List<StandingItem>>> data = new MutableLiveData<>();
        apiService.getStandings(leagueId, season, API_KEY, API_HOST).enqueue(new Callback<ApiStandingsResponse>() {
            @Override
            public void onResponse(Call<ApiStandingsResponse> call, Response<ApiStandingsResponse> response) {
                if (response.isSuccessful() && response.body() != null
                        && !response.body().getResponse().isEmpty()
                        && response.body().getResponse().get(0).getLeague() != null
                        && !response.body().getResponse().get(0).getLeague().getStandings().isEmpty()) {

                    // Lấy về toàn bộ danh sách các bảng đấu
                    List<List<StandingItem>> allStandings = response.body().getResponse().get(0).getLeague().getStandings();
                    data.postValue(allStandings);
                } else {
                    data.postValue(null);
                }
            }
            @Override
            public void onFailure(Call<ApiStandingsResponse> call, Throwable t) {
                data.postValue(null);
            }
        });
        return data;
    }

    public LiveData<List<ApiTopScorerData>> getTopScorers(int leagueId, int seasonYear) {
        final MutableLiveData<List<ApiTopScorerData>> data = new MutableLiveData<>();
        apiService.getTopScorers(seasonYear, leagueId, API_KEY, API_HOST).enqueue(new Callback<ApiResponse<ApiTopScorerData>>() {
            @Override
            public void onResponse(Call<ApiResponse<ApiTopScorerData>> call, Response<ApiResponse<ApiTopScorerData>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.postValue(response.body().getResponse());
                } else {
                    data.postValue(new ArrayList<>());
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<ApiTopScorerData>> call, Throwable t) {
                data.postValue(null);
            }
        });
        return data;
    }

    public LiveData<List<Match>> getLiveMatchesFromApi() {
        final MutableLiveData<List<Match>> data = new MutableLiveData<>();
        // Gọi API với tham số live=all để lấy tất cả các trận đang diễn ra
        apiService.getLiveFixtures("all", API_KEY, API_HOST).enqueue(new Callback<ApiResponse<ApiMatch>>() {
            @Override
            public void onResponse(Call<ApiResponse<ApiMatch>> call, Response<ApiResponse<ApiMatch>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.postValue(convertApiMatchesToDomain(response.body().getResponse()));
                } else {
                    data.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ApiMatch>> call, Throwable t) {
                data.postValue(null);
            }
        });
        return data;
    }

    public LiveData<List<Match>> getMatchesByDateRange(String fromDate, String toDate) {
        final MutableLiveData<List<Match>> data = new MutableLiveData<>();
        apiService.getFixturesByDateRange(fromDate, toDate, API_KEY, API_HOST).enqueue(new Callback<ApiResponse<ApiMatch>>() {
            @Override
            public void onResponse(Call<ApiResponse<ApiMatch>> call, Response<ApiResponse<ApiMatch>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.postValue(convertApiMatchesToDomain(response.body().getResponse()));
                } else {
                    data.postValue(null);
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<ApiMatch>> call, Throwable t) {
                data.postValue(null);
            }
        });
        return data;
    }
    public LiveData<List<Team>> searchTeams(String name) {
        final MutableLiveData<List<Team>> data = new MutableLiveData<>();
        apiService.searchTeams(name, API_KEY, API_HOST).enqueue(new Callback<ApiResponse<ApiTeamResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<ApiTeamResponse>> call, Response<ApiResponse<ApiTeamResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Team> teams = new ArrayList<>();
                    for (ApiTeamResponse apiTeamResponse : response.body().getResponse()) {
                        ApiTeamInfo teamInfo = apiTeamResponse.getTeam();
                        if (teamInfo != null) {
                            teams.add(new Team(
                                    teamInfo.getId(),
                                    teamInfo.getName(),
                                    teamInfo.getLogo()
                            ));
                        }
                    }
                    data.postValue(teams);
                } else {
                    data.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ApiTeamResponse>> call, Throwable t) {
                data.postValue(null);
            }
        });
        return data;
    }

    public LiveData<List<ApiLeagueData>> searchLeagues(String name) {
        final MutableLiveData<List<ApiLeagueData>> data = new MutableLiveData<>();
        apiService.searchLeagues(name, API_KEY, API_HOST).enqueue(new Callback<ApiLeaguesResponse>() {
            @Override
            public void onResponse(Call<ApiLeaguesResponse> call, Response<ApiLeaguesResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.postValue(response.body().getResponse());
                } else {
                    data.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<ApiLeaguesResponse> call, Throwable t) {
                data.postValue(null);
            }
        });
        return data;
    }

}