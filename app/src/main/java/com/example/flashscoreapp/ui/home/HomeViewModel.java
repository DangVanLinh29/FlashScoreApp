package com.example.flashscoreapp.ui.home;

import android.app.Application;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData; // Thêm import này
import com.example.flashscoreapp.data.model.domain.Match;
import com.example.flashscoreapp.data.model.domain.Team;
import com.example.flashscoreapp.data.model.local.FavoriteTeam;
import com.example.flashscoreapp.data.repository.MatchRepository;

import java.util.List;

public class HomeViewModel extends AndroidViewModel {

    private final MatchRepository matchRepository;
    private final MediatorLiveData<List<Match>> matches = new MediatorLiveData<>();
    private LiveData<List<Match>> currentDataSource;
    private final LiveData<List<Match>> favoriteMatches;
    private final LiveData<List<FavoriteTeam>> favoriteTeams;

    // Thêm LiveData để quản lý trạng thái loading
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public HomeViewModel(@NonNull Application application) {
        super(application);
        matchRepository = MatchRepository.getInstance(application);
        this.favoriteMatches = matchRepository.getAllFavoriteMatches();
        this.favoriteTeams = matchRepository.getAllFavoriteTeams();
    }

    // Thêm hàm getter cho isLoading
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<List<Match>> getFavoriteMatches() {
        return favoriteMatches;
    }

    public void addFavorite(Match match) {
        matchRepository.addFavorite(match);
    }

    public void removeFavorite(Match match) {
        matchRepository.removeFavorite(match);
    }

    public LiveData<List<FavoriteTeam>> getFavoriteTeams() {
        return favoriteTeams;
    }

    public void addFavoriteTeam(Team team) {
        matchRepository.addFavoriteTeam(team);
    }

    public void removeFavoriteTeam(Team team) {
        matchRepository.removeFavoriteTeam(team);
    }

    public void fetchMatchesForDate(String date) {
        // Khi bắt đầu tải, đặt isLoading thành true
        isLoading.setValue(true);

        LiveData<List<Match>> newDataSource = matchRepository.getMatchesByDateFromApi(date);

        if (currentDataSource != null) {
            matches.removeSource(currentDataSource);
        }

        currentDataSource = newDataSource;
        matches.addSource(currentDataSource, matchList -> {
            // Khi có dữ liệu về (kể cả null), đặt isLoading thành false
            isLoading.setValue(false);
            matches.setValue(matchList);
        });
    }

    public LiveData<List<Match>> getMatches() {
        return matches;
    }
}