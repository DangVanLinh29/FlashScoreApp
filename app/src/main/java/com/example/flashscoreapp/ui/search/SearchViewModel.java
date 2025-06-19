package com.example.flashscoreapp.ui.search;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import com.example.flashscoreapp.data.model.domain.Team;
import com.example.flashscoreapp.data.model.remote.ApiLeagueData;
import com.example.flashscoreapp.data.repository.MatchRepository;
import java.util.ArrayList;
import java.util.List;

public class SearchViewModel extends AndroidViewModel {
    private final MatchRepository repository;
    private final MutableLiveData<String> queryLiveData = new MutableLiveData<>();
    private final MediatorLiveData<List<Object>> searchResults = new MediatorLiveData<>();

    public SearchViewModel(@NonNull Application application) {
        super(application);
        repository = new MatchRepository(application);

        LiveData<List<Object>> combinedResults = Transformations.switchMap(queryLiveData, query -> {
            MediatorLiveData<List<Object>> mediator = new MediatorLiveData<>();
            if (query == null || query.trim().length() < 3) {
                mediator.setValue(new ArrayList<>());
                return mediator;
            }

            LiveData<List<Team>> teamsSource = repository.searchTeams(query);
            LiveData<List<ApiLeagueData>> leaguesSource = repository.searchLeagues(query);

            mediator.addSource(teamsSource, teams -> combine(mediator, teams, leaguesSource.getValue()));
            mediator.addSource(leaguesSource, leagues -> combine(mediator, teamsSource.getValue(), leagues));

            return mediator;
        });

        searchResults.addSource(combinedResults, searchResults::setValue);
    }

    private void combine(MediatorLiveData<List<Object>> mediator, List<Team> teams, List<ApiLeagueData> leagues) {
        List<Object> combinedList = new ArrayList<>();
        if (leagues != null && !leagues.isEmpty()) {
            combinedList.add("Giải đấu");
            combinedList.addAll(leagues);
        }
        if (teams != null && !teams.isEmpty()) {
            combinedList.add("Đội bóng");
            combinedList.addAll(teams);
        }
        mediator.setValue(combinedList);
    }

    public void setSearchQuery(String query) {
        queryLiveData.setValue(query);
    }

    public LiveData<List<Object>> getSearchResults() {
        return searchResults;
    }
}