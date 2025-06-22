package com.example.flashscoreapp.data.repository;

import android.app.Application;
import android.util.Log;

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
import com.example.flashscoreapp.data.model.remote.*;
import com.example.flashscoreapp.util.SessionManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
    private final SessionManager sessionManager;
    private final String API_KEY = "5e88b7e40emsh79a567711143f87p119b30jsnbc4b0f951a57";
    private final String API_HOST = "api-football-v1.p.rapidapi.com";

    public MatchRepository(Application application) {
        this.apiService = RetrofitClient.getApiService();
        AppDatabase database     = AppDatabase.getDatabase(application);
        this.matchDao = database.matchDao();
        this.teamDao = database.teamDao();
        this.executorService = Executors.newSingleThreadExecutor();
        this.sessionManager = new SessionManager(application);
    }

    public LiveData<List<Match>> getAllFavoriteMatches() {
        String email = sessionManager.getUserEmail();
        if (email == null) {
            return new MutableLiveData<>(new ArrayList<>()); // Trả về danh sách rỗng nếu chưa đăng nhập
        }
        return matchDao.getAllFavoriteMatches(email);
    }

    public void addFavorite(Match match) {
        String email = sessionManager.getUserEmail();
        if (email == null) return; // Không làm gì nếu chưa đăng nhập
        FavoriteMatch favoriteMatch = new FavoriteMatch(match.getMatchId(), email, match.getMatchTime(), match);
        executorService.execute(() -> matchDao.addFavorite(favoriteMatch));
    }

    public void removeFavorite(Match match) {
        String email = sessionManager.getUserEmail();
        if (email == null) return;
        FavoriteMatch favoriteMatch = new FavoriteMatch(match.getMatchId(), email, match.getMatchTime(), match);
        executorService.execute(() -> matchDao.removeFavorite(favoriteMatch));
    }

    public LiveData<List<FavoriteTeam>> getAllFavoriteTeams() {
        String email = sessionManager.getUserEmail();
        if (email == null) {
            return new MutableLiveData<>(new ArrayList<>());
        }
        return teamDao.getAllFavoriteTeams(email);
    }


    public void addFavoriteTeam(Team team) {
        String email = sessionManager.getUserEmail();
        if (email == null) return;
        FavoriteTeam favoriteTeam = new FavoriteTeam(team.getId(), email);
        executorService.execute(() -> teamDao.addFavorite(favoriteTeam));
    }

    public void removeFavoriteTeam(Team team) {
        String email = sessionManager.getUserEmail();
        if (email == null) return;
        FavoriteTeam favoriteTeam = new FavoriteTeam(team.getId(), email);
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
                if (apiMatch.getLeague() == null || apiMatch.getTeams() == null || apiMatch.getFixture() == null || apiMatch.getGoals() == null) {
                    continue; // Bỏ qua nếu dữ liệu cơ bản không đầy đủ
                }

                League league = new League(
                        apiMatch.getLeague().getId(),
                        apiMatch.getLeague().getName(),
                        apiMatch.getLeague().getLogo(),
                        apiMatch.getLeague().getCountry()
                );

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


                // Kiểm tra xem giá trị tỷ số có phải là null không, nếu có thì mặc định là 0
                Integer homeScore = apiMatch.getGoals().getHome();
                Integer awayScore = apiMatch.getGoals().getAway();
                Score score = new Score(
                        homeScore != null ? homeScore : 0,
                        awayScore != null ? awayScore : 0
                );

                long matchTime = sdf.parse(apiMatch.getFixture().getDate()).getTime();

                // Giữ lại phiên bản constructor cũ hơn để tương thích
                Match match = new Match(
                        apiMatch.getFixture().getId(),
                        league,
                        homeTeam,
                        awayTeam,
                        matchTime,
                        apiMatch.getFixture().getStatus().getShortStatus(),
                        score,
                        apiMatch.getLeague().getRound(),
                        apiMatch.getLeague().getSeason()
                );
                domainMatches.add(match);
            } catch (Exception e) {
                Log.e("MatchRepository", "Error converting ApiMatch to Domain Match", e);
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
    // ... trong class MatchRepository
    public LiveData<List<ApiLineup>> getLineups(int fixtureId) {
        final MutableLiveData<List<ApiLineup>> data = new MutableLiveData<>();
        apiService.getLineups(fixtureId, API_KEY, API_HOST).enqueue(new Callback<ApiResponse<ApiLineup>>() {
            @Override
            public void onResponse(Call<ApiResponse<ApiLineup>> call, Response<ApiResponse<ApiLineup>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.postValue(response.body().getResponse());
                } else {
                    data.postValue(null);
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<ApiLineup>> call, Throwable t) {
                data.postValue(null);
            }
        });
        return data;
    }

    public LiveData<List<ApiPlayerResponse>> getPlayersForTeam(int teamId, int season) {
        final MutableLiveData<List<ApiPlayerResponse>> data = new MutableLiveData<>();

        // Không dùng năm hiện tại nữa, mà dùng season được truyền vào
        apiService.getPlayersForTeam(teamId, season, API_KEY, API_HOST).enqueue(new Callback<ApiResponse<ApiPlayerResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<ApiPlayerResponse>> call, Response<ApiResponse<ApiPlayerResponse>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().getResponse().isEmpty()) {
                    data.postValue(response.body().getResponse());
                } else {
                    // Nếu không tìm thấy, không cần thử lại năm trước nữa vì chúng ta đã có season cụ thể
                    data.postValue(new ArrayList<>()); // Trả về danh sách rỗng
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ApiPlayerResponse>> call, Throwable t) {
                data.postValue(null);
            }
        });
        return data;
    }

    public LiveData<List<Match>> getFixturesForLeague(int leagueId, int season) {
        MutableLiveData<List<Match>> data = new MutableLiveData<>();
        apiService.getFixturesByLeagueAndSeason(leagueId, season, "NS", API_KEY, API_HOST).enqueue(new Callback<ApiResponse<ApiMatch>>() {
            @Override
            public void onResponse(Call<ApiResponse<ApiMatch>> call, Response<ApiResponse<ApiMatch>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // SỬA Ở ĐÂY: Chuyển đổi dữ liệu ngay tại Repository
                    List<Match> domainMatches = convertApiMatchesToDomain(response.body().getResponse());
                    data.postValue(domainMatches);
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

    public LiveData<List<Match>> getAllMatchesForTeam(int teamId, int season) {
        final MutableLiveData<List<Match>> data = new MutableLiveData<>();
        apiService.getFixturesForTeam(teamId, season, API_KEY, API_HOST).enqueue(new Callback<ApiResponse<ApiMatch>>() {
            @Override
            public void onResponse(Call<ApiResponse<ApiMatch>> call, Response<ApiResponse<ApiMatch>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Dùng lại hàm chuyển đổi đã có
                    Log.d("TeamDetailsDebug", "API trả về " + response.body().getResponse().size() + " trận đấu cho teamId=" + teamId + ", season=" + season);
                    data.postValue(convertApiMatchesToDomain(response.body().getResponse()));
                } else {
                    Log.e("TeamDetailsDebug", "Gọi API thất bại cho teamId=" + teamId);
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

    public LiveData<ApiTeamResponse> getTeamDetails(int teamId) {
        final MutableLiveData<ApiTeamResponse> data = new MutableLiveData<>();
        apiService.getTeamDetails(teamId, API_KEY, API_HOST).enqueue(new Callback<ApiResponse<ApiTeamResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<ApiTeamResponse>> call, Response<ApiResponse<ApiTeamResponse>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().getResponse().isEmpty()) {
                    data.postValue(response.body().getResponse().get(0));
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

    // Hàm này lấy TẤT CẢ các trận ĐÃ KẾT THÚC
    public LiveData<List<Match>> getLastMatchesForTeam(int teamId, int count) {
        final MutableLiveData<List<Match>> data = new MutableLiveData<>();
        apiService.getLastFixturesForTeam(teamId, count, API_KEY, API_HOST).enqueue(new Callback<ApiResponse<ApiMatch>>() {
            @Override
            public void onResponse(Call<ApiResponse<ApiMatch>> call, Response<ApiResponse<ApiMatch>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.postValue(convertApiMatchesToDomain(response.body().getResponse()));
                } else {
                    data.postValue(new ArrayList<>());
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<ApiMatch>> call, Throwable t) {
                data.postValue(null);
            }
        });
        return data;
    }

    // Hàm này lấy TẤT CẢ các trận SẮP DIỄN RA của đội trong mùa giải
    public LiveData<List<Match>> getUpcomingFixturesForTeam(int teamId, int count) {
        final MutableLiveData<List<Match>> data = new MutableLiveData<>();
        // Gọi API với tham số `next` thay vì `status` và `season`
        apiService.getNextFixturesForTeam(teamId, count, API_KEY, API_HOST).enqueue(new Callback<ApiResponse<ApiMatch>>() {
            @Override
            public void onResponse(Call<ApiResponse<ApiMatch>> call, Response<ApiResponse<ApiMatch>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.postValue(convertApiMatchesToDomain(response.body().getResponse()));
                } else {
                    data.postValue(new ArrayList<>());
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<ApiMatch>> call, Throwable t) {
                data.postValue(null);
            }
        });
        return data;
    }

    public LiveData<List<Match>> getPastResultsForTeam(int teamId, int season) {
        final MutableLiveData<List<Match>> data = new MutableLiveData<>();
        // Các trạng thái đã kết thúc: FT (Full Time), AET (After Extra Time), PEN (Penalty)
        String finishedStatuses = "FT-AET-PEN";

        // Đảm bảo bạn có phương thức getFixturesForTeamByStatus trong ApiService
        apiService.getFixturesForTeamByStatus(teamId, season, finishedStatuses, API_KEY, API_HOST).enqueue(new Callback<ApiResponse<ApiMatch>>() {
            @Override
            public void onResponse(Call<ApiResponse<ApiMatch>> call, Response<ApiResponse<ApiMatch>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Chuyển đổi dữ liệu thô sang dữ liệu sạch và gửi đi
                    data.postValue(convertApiMatchesToDomain(response.body().getResponse()));
                } else {
                    // Trả về danh sách rỗng nếu không có kết quả
                    data.postValue(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ApiMatch>> call, Throwable t) {
                // Trả về null nếu có lỗi mạng
                data.postValue(null);
            }
        });
        return data;
    }

    public LiveData<List<Match>> getNextFixturesForTeam(int teamId, int count) {
        final MutableLiveData<List<Match>> data = new MutableLiveData<>();

        // Gọi phương thức trong ApiService với tham số 'next'
        apiService.getNextFixturesForTeam(teamId, count, API_KEY, API_HOST).enqueue(new Callback<ApiResponse<ApiMatch>>() {
            @Override
            public void onResponse(Call<ApiResponse<ApiMatch>> call, Response<ApiResponse<ApiMatch>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Dùng lại hàm convert có sẵn để chuyển đổi dữ liệu và gửi về cho ViewModel
                    data.postValue(convertApiMatchesToDomain(response.body().getResponse()));
                } else {
                    // Trả về danh sách rỗng nếu API không có kết quả
                    data.postValue(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ApiMatch>> call, Throwable t) {
                // Trả về null nếu có lỗi mạng
                data.postValue(null);
            }
        });

        return data;
    }
}