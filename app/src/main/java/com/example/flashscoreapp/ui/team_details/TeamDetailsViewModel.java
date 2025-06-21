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

    public TeamDetailsViewModel(@NonNull Application application, int teamId) {
        super(application);
        MatchRepository repository = new MatchRepository(application);

        // Tính toán mùa giải gần nhất để lấy Kết quả và Đội hình
        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR);
        int month = now.get(Calendar.MONTH);
        int footballSeasonYear = (month < 7) ? (year - 1) : year;

        // Lấy thông tin chi tiết của đội
        teamDetails = repository.getTeamDetails(teamId);

        // Lấy Đội hình và Kết quả theo mùa giải gần nhất
        squad = repository.getPlayersForTeam(teamId, footballSeasonYear);
        pastMatches = repository.getPastResultsForTeam(teamId, footballSeasonYear);

        // LẤY LỊCH THI ĐẤU THEO CÁCH MỚI: Lấy 10 trận sắp tới, không phụ thuộc mùa giải
        // Đảm bảo bạn có hàm getNextFixturesForTeam trong Repository và ApiService
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