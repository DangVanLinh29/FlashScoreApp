package com.example.flashscoreapp.ui.live;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.flashscoreapp.data.model.domain.Match;
import com.example.flashscoreapp.data.repository.MatchRepository;
import java.util.List;

public class LiveMatchesViewModel extends AndroidViewModel {
    private final MatchRepository repository;
    private final LiveData<List<Match>> liveMatches;

    public LiveMatchesViewModel(@NonNull Application application) {
        super(application);
        repository = MatchRepository.getInstance(application);
        liveMatches = repository.getLiveMatchesFromApi();
    }

    public LiveData<List<Match>> getLiveMatches() {
        return liveMatches;
    }
}