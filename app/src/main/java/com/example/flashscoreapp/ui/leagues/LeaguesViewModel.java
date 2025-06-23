package com.example.flashscoreapp.ui.leagues;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.flashscoreapp.data.repository.MatchRepository;
import java.util.List;
import com.example.flashscoreapp.data.model.remote.ApiLeagueData;

public class LeaguesViewModel extends AndroidViewModel {
    private final MatchRepository repository;
    private final LiveData<List<ApiLeagueData>> leaguesData;

    public LeaguesViewModel(@NonNull Application application) {
        super(application);
        repository = MatchRepository.getInstance(application);
        // THAY ĐỔI LỜI GỌI HÀM Ở ĐÂY
        leaguesData = repository.getLeaguesWithSeasons();
    }

    public LiveData<List<ApiLeagueData>> getLeaguesData() {
        return leaguesData;
    }
}