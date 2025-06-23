package com.example.flashscoreapp.ui.leaguedetails.topscorers;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.flashscoreapp.data.model.remote.ApiTopScorerData;
import com.example.flashscoreapp.data.repository.MatchRepository;
import java.util.List;

public class TopScorersViewModel extends AndroidViewModel {
    private final LiveData<List<ApiTopScorerData>> topScorers;

    public TopScorersViewModel(@NonNull Application application, int leagueId, int seasonYear) {
        super(application);
        MatchRepository repository = MatchRepository.getInstance(application);
        topScorers = repository.getTopScorers(leagueId, seasonYear);
    }

    public LiveData<List<ApiTopScorerData>> getTopScorers() {
        return topScorers;
    }
}