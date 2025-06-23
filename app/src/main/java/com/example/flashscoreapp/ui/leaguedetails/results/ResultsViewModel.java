package com.example.flashscoreapp.ui.leaguedetails.results;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import com.example.flashscoreapp.data.model.domain.Match;
import com.example.flashscoreapp.data.model.domain.RoundHeader;
import com.example.flashscoreapp.data.repository.MatchRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ResultsViewModel extends AndroidViewModel {
    // ViewModel sẽ trả về LiveData<List<Object>> đã được xử lý
    private final LiveData<List<Object>> groupedResults;

    public ResultsViewModel(@NonNull Application application, int leagueId, int seasonYear) {
        super(application);
        MatchRepository repository = MatchRepository.getInstance(application);

        // Lấy danh sách các trận đã kết thúc
        LiveData<List<Match>> resultsSource = repository.getResultsForLeague(leagueId, seasonYear);

        // Dùng Transformations.map để tự động chuyển đổi List<Match> thành List<Object>
        groupedResults = Transformations.map(resultsSource, this::groupAndSortMatches);
    }

    public LiveData<List<Object>> getGroupedResults() {
        return groupedResults;
    }

    private List<Object> groupAndSortMatches(List<Match> matches) {
        if (matches == null || matches.isEmpty()) {
            return new ArrayList<>();
        }

        Map<String, List<Match>> groupedByRound = matches.stream()
                .collect(Collectors.groupingBy(Match::getRound, LinkedHashMap::new, Collectors.toList()));

        List<String> sortedRounds = new ArrayList<>(groupedByRound.keySet());

        Collections.sort(sortedRounds, (round1, round2) -> {
            try {
                int r1 = Integer.parseInt(round1.replaceAll("\\D+", ""));
                int r2 = Integer.parseInt(round2.replaceAll("\\D+", ""));
                return Integer.compare(r2, r1);
            } catch (NumberFormatException e) {
                return round2.compareTo(round1);
            }
        });

        List<Object> flattenedList = new ArrayList<>();
        for (String round : sortedRounds) {
            String translatedRound = round.replace("Regular Season -", "Vòng");
            flattenedList.add(new RoundHeader(translatedRound.trim()));
            List<Match> matchesInRound = groupedByRound.get(round);
            if (matchesInRound != null) {
                matchesInRound.sort(Comparator.comparingLong(Match::getMatchTime).reversed());
                flattenedList.addAll(matchesInRound);
            }
        }
        return flattenedList;
    }
}