package com.example.flashscoreapp.ui.match_details;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import com.example.flashscoreapp.data.model.domain.Match;
import com.example.flashscoreapp.data.model.domain.MatchDetails;
import com.example.flashscoreapp.data.model.remote.ApiLineup;
import com.example.flashscoreapp.data.model.remote.ApiPlayerResponse;
import com.example.flashscoreapp.data.repository.MatchRepository;
import java.util.List;

public class MatchDetailsViewModel extends AndroidViewModel {

    private final MatchRepository repository;
    private final LiveData<MatchDetails> matchDetails;
    private final LiveData<List<ApiLineup>> lineups;
    private final MediatorLiveData<Pair<List<Match>, List<Match>>> recentMatchesPair = new MediatorLiveData<>();
    private final LiveData<List<ApiPlayerResponse>> homeTeamPlayers;
    private final LiveData<List<ApiPlayerResponse>> awayTeamPlayers;

    // Constructor nhận 4 tham số
    public MatchDetailsViewModel(@NonNull Application application, int matchId, int homeTeamId, int awayTeamId) {
        super(application);
        repository = new MatchRepository(application);

        // Lấy dữ liệu cho các tab Tóm tắt, Số liệu, Đội hình
        matchDetails = repository.getMatchDetailsFromApi(matchId);
        lineups = repository.getLineups(matchId);

        // Lấy danh sách cầu thủ của mỗi đội (dùng cho tab Đội hình cũ)
        // Lưu ý: Cần có season để lấy đúng danh sách cầu thủ
        int currentSeason = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
        homeTeamPlayers = repository.getPlayersForTeam(homeTeamId, currentSeason);
        awayTeamPlayers = repository.getPlayersForTeam(awayTeamId, currentSeason);

        // Lấy dữ liệu cho tab Đối đầu (H2H)
        LiveData<List<Match>> homeMatchesSource = repository.getLastMatchesForTeam(homeTeamId, 5);
        LiveData<List<Match>> awayMatchesSource = repository.getLastMatchesForTeam(awayTeamId, 5);

        recentMatchesPair.addSource(homeMatchesSource, homeMatches -> {
            if (homeMatches != null && awayMatchesSource.getValue() != null) {
                recentMatchesPair.setValue(new Pair<>(homeMatches, awayMatchesSource.getValue()));
            }
        });
        recentMatchesPair.addSource(awayMatchesSource, awayMatches -> {
            if (awayMatches != null && homeMatchesSource.getValue() != null) {
                recentMatchesPair.setValue(new Pair<>(homeMatchesSource.getValue(), awayMatches));
            }
        });
    }

    public LiveData<MatchDetails> getMatchDetails() { return matchDetails; }
    public LiveData<List<ApiLineup>> getLineups() { return lineups; }
    public LiveData<Pair<List<Match>, List<Match>>> getRecentMatchesPair() { return recentMatchesPair; }
    public LiveData<List<ApiPlayerResponse>> getHomeTeamPlayers() { return homeTeamPlayers; }
    public LiveData<List<ApiPlayerResponse>> getAwayTeamPlayers() { return awayTeamPlayers; }
}