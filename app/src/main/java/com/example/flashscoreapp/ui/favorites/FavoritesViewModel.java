package com.example.flashscoreapp.ui.favorites;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import com.example.flashscoreapp.data.model.domain.Match;
import com.example.flashscoreapp.data.model.local.FavoriteTeam;
import com.example.flashscoreapp.data.repository.MatchRepository;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public class FavoritesViewModel extends AndroidViewModel {

    private final MatchRepository repository;
    private final MediatorLiveData<List<Match>> favoriteTeamMatches = new MediatorLiveData<>();
    private final LiveData<List<Match>> favoriteMatches;

    public FavoritesViewModel(@NonNull Application application) {
        super(application);
        repository = new MatchRepository(application);

        favoriteMatches = repository.getAllFavoriteMatches();

        SimpleDateFormat apiDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        Calendar calendar = Calendar.getInstance();
        String fromDate = apiDateFormat.format(calendar.getTime());
        calendar.add(Calendar.DAY_OF_YEAR, 90);
        String toDate = apiDateFormat.format(calendar.getTime());

        final LiveData<List<Match>> upcomingMatches = repository.getMatchesByDateRange(fromDate, toDate);
        final LiveData<List<FavoriteTeam>> favoriteTeams = repository.getAllFavoriteTeams();

        favoriteTeamMatches.addSource(upcomingMatches, matches ->
                filterMatches(matches, favoriteTeams.getValue()));
        favoriteTeamMatches.addSource(favoriteTeams, teams ->
                filterMatches(upcomingMatches.getValue(), teams));
    }

    private void filterMatches(List<Match> allMatches, List<FavoriteTeam> favoriteTeams) {
        if (allMatches == null || favoriteTeams == null) {
            favoriteTeamMatches.setValue(new ArrayList<>());
            return;
        }

        Set<Integer> favoriteTeamIds = favoriteTeams.stream()
                .map(ft -> ft.teamId)
                .collect(Collectors.toSet());

        List<String> finishedStatus = Arrays.asList("FT", "AET", "PEN", "CANC", "ABD", "AWD", "WO");

        List<Match> filteredMatches = allMatches.stream()
                .filter(match -> favoriteTeamIds.contains(match.getHomeTeam().getId()) ||
                        favoriteTeamIds.contains(match.getAwayTeam().getId()))
                .filter(match -> !finishedStatus.contains(match.getStatus()))
                .collect(Collectors.toList());

        favoriteTeamMatches.setValue(filteredMatches);
    }

    public LiveData<List<Match>> getFavoriteMatches() {
        return favoriteMatches;
    }

    public LiveData<List<Match>> getFavoriteTeamMatches() {
        return favoriteTeamMatches;
    }


    public void addFavorite(Match match) {
        repository.addFavorite(match);
    }

    public void removeFavorite(Match match) {
        repository.removeFavorite(match);
    }
}