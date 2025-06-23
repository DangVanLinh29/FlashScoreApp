package com.example.flashscoreapp.ui.leagues.details.fixtures;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.flashscoreapp.data.model.domain.Match; // Sửa import
import com.example.flashscoreapp.data.repository.MatchRepository;
import java.util.List;

public class FixturesViewModel extends AndroidViewModel {
    private final MatchRepository repository;
    // Sửa kiểu dữ liệu của LiveData tại đây
    private final LiveData<List<Match>> fixtures;

    public FixturesViewModel(@NonNull Application application, int leagueId, int season) {
        super(application);
        repository = MatchRepository.getInstance(application);
        // Lời gọi hàm không đổi, nhưng kiểu trả về của nó đã thay đổi
        fixtures = repository.getFixturesForLeague(leagueId, season);
    }

    // Sửa kiểu trả về của hàm getter
    public LiveData<List<Match>> getFixtures() {
        return fixtures;
    }
}