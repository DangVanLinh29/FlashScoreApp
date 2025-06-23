package com.example.flashscoreapp.ui.team_details;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.flashscoreapp.data.model.domain.Match;
import com.example.flashscoreapp.data.model.remote.ApiPlayerResponse;
import com.example.flashscoreapp.data.repository.MatchRepository;
import com.example.flashscoreapp.data.model.remote.ApiTeamResponse;

import java.util.Calendar;
import java.util.List;

public class TeamDetailsViewModel extends AndroidViewModel {

    private final LiveData<List<Match>> pastMatches;
    private final LiveData<List<Match>> upcomingFixtures;
    private final LiveData<ApiTeamResponse> teamDetails;
    private final LiveData<List<ApiPlayerResponse>> squad;

    // Sửa constructor để nhận seasonYear
    public TeamDetailsViewModel(@NonNull Application application, int teamId, int seasonYear) {
        super(application);
        MatchRepository repository = new MatchRepository(application);


        // Lấy thông tin chi tiết của đội
        teamDetails = repository.getTeamDetails(teamId);

        // Lấy Đội hình và Kết quả theo mùa giải được truyền vào
        squad = repository.getPlayersForTeam(teamId, seasonYear);
        pastMatches = repository.getLastMatchesForTeam(teamId, 30);

        // Lấy lịch thi đấu (giữ nguyên)
        upcomingFixtures = repository.getNextFixturesForTeam(teamId, 10);
    }

    public LiveData<ApiTeamResponse> getTeamDetails() {
        return teamDetails;
    }

    public LiveData<List<Match>> getPastMatches() {
        return pastMatches;
    }

    public LiveData<List<Match>> getUpcomingFixtures() {
        return upcomingFixtures;
    }

    public LiveData<List<ApiPlayerResponse>> getSquad() {
        return squad;
    }
}