package com.example.flashscoreapp.data.repository;

import android.app.Application;
import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.flashscoreapp.data.api.ApiService;
import com.example.flashscoreapp.data.api.RetrofitClient;
import com.example.flashscoreapp.data.model.ApiEvent;
import com.example.flashscoreapp.data.model.ApiFixture;
import com.example.flashscoreapp.data.model.ApiLeagueData;
import com.example.flashscoreapp.data.model.ApiLeaguesResponse;
import com.example.flashscoreapp.data.model.ApiResponse;
import com.example.flashscoreapp.data.model.ApiMatch;
import com.example.flashscoreapp.data.model.ApiStandingsResponse;
import com.example.flashscoreapp.data.model.ApiStatisticItem;
import com.example.flashscoreapp.data.model.ApiStatisticsResponse;
import com.example.flashscoreapp.data.model.ApiTeamStatistics;
import com.example.flashscoreapp.data.model.League;
import com.example.flashscoreapp.data.model.Match;
import com.example.flashscoreapp.data.model.MatchDetails;
import com.example.flashscoreapp.data.model.Score;
import com.example.flashscoreapp.data.model.StandingItem;
import com.example.flashscoreapp.data.model.Team;
import com.example.flashscoreapp.data.model.ApiLeagueData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.example.flashscoreapp.data.model.MatchDetails;
import com.example.flashscoreapp.data.model.MatchEvent;
import com.example.flashscoreapp.data.model.MatchStatistic;
import com.example.flashscoreapp.data.db.AppDatabase;
import com.example.flashscoreapp.data.db.MatchDao;
import com.example.flashscoreapp.data.model.FavoriteMatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class MatchRepository {
    private final ApiService apiService;
    private final MatchDao matchDao;
    private final ExecutorService executorService;
    private final String API_KEY = "9603cad7a8mshaf2d58ef107a002p1f7706jsn62cf5be4f1d5";
    private final String API_HOST = "api-football-v1.p.rapidapi.com";

    public MatchRepository(Application application) {
        this.apiService = RetrofitClient.getApiService();
        AppDatabase db = AppDatabase.getDatabase(application); // Lấy instance của DB
        this.matchDao = db.matchDao(); // Lấy DAO từ DB
        this.executorService = Executors.newSingleThreadExecutor(); // Tạo một thread để xử lý tác vụ DB
    }

    public LiveData<List<Match>> getAllFavoriteMatches() {
        return matchDao.getAllFavoriteMatches();
    }

    public void addFavorite(Match match) {
        // Truyền thêm match.getMatchTime() vào
        FavoriteMatch favoriteMatch = new FavoriteMatch(match.getMatchId(), match.getMatchTime(), match);
        executorService.execute(() -> matchDao.addFavorite(favoriteMatch));
    }

    public void removeFavorite(Match match) {
        // Truyền thêm match.getMatchTime() vào
        FavoriteMatch favoriteMatch = new FavoriteMatch(match.getMatchId(), match.getMatchTime(), match);
        executorService.execute(() -> matchDao.removeFavorite(favoriteMatch));
    }
    public LiveData<MatchDetails> getMatchDetailsFromApi(int matchId) {
        final MutableLiveData<MatchDetails> detailsData = new MutableLiveData<>();
        final MatchDetails combinedDetails = new MatchDetails(matchId, new ArrayList<>(), new ArrayList<>());

        // Thực hiện cuộc gọi API thứ nhất: Lấy Events
        apiService.getMatchEvents(matchId, API_KEY, API_HOST).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().getResponse().isEmpty()) {
                    ApiMatch apiMatch = response.body().getResponse().get(0);

                    // Lấy dữ liệu events
                    List<MatchEvent> events = convertApiEventsToDomain(apiMatch.getEvents());
                    combinedDetails.setEvents(events);

                    ApiFixture fixture = apiMatch.getFixture();
                    if (fixture != null) {
                        combinedDetails.setReferee(fixture.getReferee());
                        if (fixture.getVenue() != null) {
                            combinedDetails.setStadium(fixture.getVenue().getName());
                        }
                    }

                    detailsData.postValue(combinedDetails);
                }
            }
            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                android.util.Log.e("MatchRepository", "Events request failed", t);
            }
        });

        // Thực hiện cuộc gọi API thứ hai: Lấy Statistics
        apiService.getMatchStatistics(matchId, API_KEY, API_HOST).enqueue(new Callback<ApiStatisticsResponse>() {
            @Override
            public void onResponse(Call<ApiStatisticsResponse> call, Response<ApiStatisticsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<MatchStatistic> statistics = convertApiStatisticsToDomain(response.body().getResponse());
                    combinedDetails.setStatistics(statistics); // Cập nhật statistics vào đối tượng chung
                    detailsData.postValue(combinedDetails); // Cập nhật UI lần 2
                }
            }
            @Override
            public void onFailure(Call<ApiStatisticsResponse> call, Throwable t) {
                android.util.Log.e("MatchRepository", "Statistics request failed", t);
            }
        });

        return detailsData;
    }
    private List<MatchStatistic> convertApiStatisticsToDomain(List<ApiTeamStatistics> apiStats) {
        List<MatchStatistic> domainStats = new ArrayList<>();
        if (apiStats == null || apiStats.size() < 2) return domainStats;

        ApiTeamStatistics homeTeamStats = apiStats.get(0);
        ApiTeamStatistics awayTeamStats = apiStats.get(1);

        // Dùng Map để dễ dàng kết hợp thống kê của 2 đội
        Map<String, MatchStatistic> statsMap = new LinkedHashMap<>();

        // Xử lý đội nhà trước
        for (ApiStatisticItem item : homeTeamStats.getStatistics()) {
            String type = item.getType();
            String value = item.getValue() != null ? String.valueOf(item.getValue()) : "0";
            statsMap.put(type, new MatchStatistic(type, value, "0"));
        }

        // Xử lý đội khách
        for (ApiStatisticItem item : awayTeamStats.getStatistics()) {
            String type = item.getType();
            if (statsMap.containsKey(type)) {
                String value = item.getValue() != null ? String.valueOf(item.getValue()) : "0";
                statsMap.get(type).setAwayValue(value);

            }
        }
        domainStats.addAll(statsMap.values());
        return domainStats;
    }

    // --- HÀM PHỤ ĐỂ CHUYỂN ĐỔI DỮ LIỆU SỰ KIỆN ---
    private List<MatchEvent> convertApiEventsToDomain(List<ApiEvent> apiEvents) {
        List<MatchEvent> domainEvents = new ArrayList<>();
        if (apiEvents == null) return domainEvents;

        for (ApiEvent apiEvent : apiEvents) {
            String eventType = apiEvent.getType(); // "Card", "Goal", "subst"
            String eventDetail = apiEvent.getDetail(); // "Yellow Card", "Normal Goal", etc.

            // Có thể bạn muốn xử lý để tên sự kiện đẹp hơn
            String finalEventType = eventType.equals("Card") ? eventDetail : eventType;

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

        // In ra log để biết cuộc gọi API sắp được thực hiện
        android.util.Log.d("MatchRepository", "Attempting to fetch matches for date: " + date);

        apiService.getFixturesByDate(date, API_KEY, API_HOST).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                // Log này sẽ LUÔN chạy khi có phản hồi từ server
                android.util.Log.d("MatchRepository", "onResponse - Code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    List<Match> convertedMatches = convertApiMatchesToDomain(response.body().getResponse());
                    android.util.Log.i("MatchRepository", "Successfully fetched and converted " + convertedMatches.size() + " matches.");
                    data.postValue(convertedMatches);
                } else {
                    // Nếu isSuccessful() là false, log lỗi chi tiết
                    android.util.Log.e("MatchRepository", "API Error - Message: " + response.message());
                    try {
                        // Cố gắng đọc nội dung lỗi từ server để biết chi tiết
                        String errorBody = response.errorBody().string();
                        android.util.Log.e("MatchRepository", "Error Body: " + errorBody);
                    } catch (Exception e) {
                        android.util.Log.e("MatchRepository", "Error parsing error body", e);
                    }
                    data.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                // Nếu có lỗi mạng, log chi tiết lỗi đó
                android.util.Log.e("MatchRepository", "Network Request Failed", t);
                data.postValue(null);
            }
        });
        return data;
    }

    // --- HÀM CHUYỂN ĐỔI DỮ LIỆU ---
    private List<Match> convertApiMatchesToDomain(List<ApiMatch> apiMatches) {
        List<Match> domainMatches = new ArrayList<>();
        if (apiMatches == null) return domainMatches;

        // Định dạng để chuyển đổi chuỗi ngày tháng từ API
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

        for (ApiMatch apiMatch : apiMatches) {
            // Chuyển đổi các lớp con
            League league = new League(apiMatch.getLeague().getName());
            Team homeTeam = new Team(apiMatch.getTeams().getHome().getId(), apiMatch.getTeams().getHome().getName(), apiMatch.getTeams().getHome().getLogo());
            Team awayTeam = new Team(apiMatch.getTeams().getAway().getId(), apiMatch.getTeams().getAway().getName(), apiMatch.getTeams().getAway().getLogo());

            Integer homeGoals = apiMatch.getGoals().getHome();
            Integer awayGoals = apiMatch.getGoals().getAway();
            Score score = new Score(homeGoals != null ? homeGoals : 0, awayGoals != null ? awayGoals : 0);

            long matchTime = 0;
            try {
                Date parsedDate = sdf.parse(apiMatch.getFixture().getDate());
                if (parsedDate != null) {
                    matchTime = parsedDate.getTime();
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            // Tạo đối tượng Match mà ứng dụng của bạn sử dụng
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
        }
        return domainMatches;
    }

    public LiveData<List<Match>> getMatchesByDateFromMock(Context context, String date) {
        // ... giữ nguyên code cũ
        final MutableLiveData<List<Match>> data = new MutableLiveData<>();
        String jsonString = loadJSONFromAsset(context, "mock_database.json");

        if (jsonString != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, List<Match>>>(){}.getType();
            Map<String, List<Match>> allMatches = gson.fromJson(jsonString, type);
            List<Match> matchesForDate = allMatches.get(date);
            data.postValue(matchesForDate);
        } else {
            data.postValue(null);
        }
        return data;
    }

    public LiveData<MatchDetails> getMatchDetailsFromMock(Context context, int matchId) {
        final MutableLiveData<MatchDetails> data = new MutableLiveData<>();
        String fileName = "mock_match_details_" + matchId + ".json";
        String jsonString = loadJSONFromAsset(context, fileName);

        if (jsonString != null) {
            Gson gson = new Gson();
            MatchDetails details = gson.fromJson(jsonString, MatchDetails.class);
            data.postValue(details);
        } else {
            data.postValue(null);
        }
        return data;
    }

    private String loadJSONFromAsset(Context context, String fileName) {
        // ... giữ nguyên code cũ
        String json;
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public LiveData<List<League>> getLeagues() {
        final MutableLiveData<List<League>> data = new MutableLiveData<>();

        apiService.getLeagues(API_KEY, API_HOST).enqueue(new Callback<ApiLeaguesResponse>() {
            @Override
            public void onResponse(Call<ApiLeaguesResponse> call, Response<ApiLeaguesResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<League> leagues = new ArrayList<>();
                    for (ApiLeagueData leagueData : response.body().getResponse()) {
                        leagues.add(leagueData.getLeague());
                    }
                    data.postValue(leagues);
                } else {
                    android.util.Log.e("MatchRepository", "Leagues API Error: " + response.code());
                    data.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<ApiLeaguesResponse> call, Throwable t) {
                android.util.Log.e("MatchRepository", "Leagues Network Failure: " + t.getMessage());
                data.postValue(null);
            }
        });
        return data;
    }

    public LiveData<List<StandingItem>> getStandings(int leagueId, int season) {
        final MutableLiveData<List<StandingItem>> data = new MutableLiveData<>();

        apiService.getStandings(leagueId, season, API_KEY, API_HOST).enqueue(new Callback<ApiStandingsResponse>() {
            @Override
            public void onResponse(Call<ApiStandingsResponse> call, Response<ApiStandingsResponse> response) {
                if (response.isSuccessful() && response.body() != null
                        && !response.body().getResponse().isEmpty()) {

                    // API trả về cấu trúc list trong list, ta lấy ra bảng xếp hạng đầu tiên
                    List<StandingItem> standings = response.body().getResponse().get(0).getLeague().getStandings().get(0);
                    data.postValue(standings);
                } else {
                    android.util.Log.e("MatchRepository", "Standings API Error: " + response.code());
                    data.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<ApiStandingsResponse> call, Throwable t) {
                android.util.Log.e("MatchRepository", "Standings Network Failure: " + t.getMessage());
                data.postValue(null);
            }
        });

        return data;
    }

    public LiveData<List<ApiLeagueData>> getLeaguesWithSeasons() {
        final MutableLiveData<List<ApiLeagueData>> data = new MutableLiveData<>();

        apiService.getLeagues(API_KEY, API_HOST).enqueue(new Callback<ApiLeaguesResponse>() {
            @Override
            public void onResponse(Call<ApiLeaguesResponse> call, Response<ApiLeaguesResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Trả về toàn bộ ApiLeagueData
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

    public LiveData<List<Match>> getResultsForLeague(int leagueId, int season) {
        final MutableLiveData<List<Match>> data = new MutableLiveData<>();
        apiService.getFixturesByLeagueAndSeason(leagueId, season, API_KEY, API_HOST).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Match> allMatches = convertApiMatchesToDomain(response.body().getResponse());
                    // Lọc ra các trận đã kết thúc (FT - Full Time)
                    List<Match> finishedMatches = allMatches.stream()
                            .filter(match -> match.getStatus().equals("FT"))
                            .collect(Collectors.toList());
                    data.postValue(finishedMatches);
                } else {
                    data.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                data.postValue(null);
            }
        });
        return data;
    }

}