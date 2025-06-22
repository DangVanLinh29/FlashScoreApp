package com.example.flashscoreapp.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.flashscoreapp.R;
import com.example.flashscoreapp.data.model.domain.League;
import com.example.flashscoreapp.data.model.domain.Match;
import com.example.flashscoreapp.data.model.domain.Team;
import com.example.flashscoreapp.ui.match_details.MatchDetailsActivity;
import com.example.flashscoreapp.ui.team_details.TeamDetailsActivity;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class HomeFragment extends Fragment implements MatchAdapter.OnItemClickListener {

    private HomeViewModel homeViewModel;
    private RecyclerView recyclerViewMatches;
    private RecyclerView recyclerViewDates;
    private HomeGroupedAdapter homeGroupedAdapter;
    private DateAdapter dateAdapter;
    private ProgressBar progressBar;
    private TextView textNoMatches;

    private final SimpleDateFormat apiDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressBar = view.findViewById(R.id.progress_bar);
        textNoMatches = view.findViewById(R.id.text_no_matches);
        recyclerViewDates = view.findViewById(R.id.recycler_view_dates);
        recyclerViewMatches = view.findViewById(R.id.recycler_view_matches);

        setupRecyclerView();
        setupDateRecyclerView();

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        observeViewModel();

        homeViewModel.fetchMatchesForDate(apiDateFormat.format(Calendar.getInstance().getTime()));
    }

    private void setupDateRecyclerView() {
        List<Calendar> dates = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -30);

        for (int i = 0; i < 60; i++) {
            dates.add((Calendar) calendar.clone());
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        int todayPosition = 30;

        dateAdapter = new DateAdapter(dates, todayPosition, selectedDate -> {
            String dateForApi = apiDateFormat.format(selectedDate.getTime());
            homeViewModel.fetchMatchesForDate(dateForApi);
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewDates.setLayoutManager(layoutManager);
        recyclerViewDates.setAdapter(dateAdapter);

        recyclerViewDates.post(() -> {
            layoutManager.scrollToPosition(todayPosition);
            recyclerViewDates.post(() -> {
                View v = layoutManager.findViewByPosition(todayPosition);
                if (v != null) {
                    int offset = recyclerViewDates.getWidth() / 2 - v.getWidth() / 2;
                    layoutManager.scrollToPositionWithOffset(todayPosition, offset);
                }
            });
        });
    }

    private void setupRecyclerView() {
        homeGroupedAdapter = new HomeGroupedAdapter();
        homeGroupedAdapter.setOnItemClickListener(this);
        recyclerViewMatches.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewMatches.setAdapter(homeGroupedAdapter);
    }

    private void observeViewModel() {
        homeViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) {
                progressBar.setVisibility(View.VISIBLE);
                recyclerViewMatches.setVisibility(View.GONE);
                textNoMatches.setVisibility(View.GONE);
            } else {
                progressBar.setVisibility(View.GONE);
            }
        });

        homeViewModel.getMatches().observe(getViewLifecycleOwner(), matches -> {
            if (homeViewModel.getIsLoading().getValue() != null && !homeViewModel.getIsLoading().getValue()) {
                if (matches != null && !matches.isEmpty()) {
                    recyclerViewMatches.setVisibility(View.VISIBLE);
                    textNoMatches.setVisibility(View.GONE);
                    List<Object> groupedList = groupMatchesByLeague(matches);
                    homeGroupedAdapter.setDisplayList(groupedList);
                } else {
                    recyclerViewMatches.setVisibility(View.GONE);
                    textNoMatches.setVisibility(View.VISIBLE);
                    homeGroupedAdapter.setDisplayList(new ArrayList<>());
                }
            }
        });

        homeViewModel.getFavoriteMatches().observe(getViewLifecycleOwner(), favoriteMatches -> {
            if (favoriteMatches != null) {
                Set<Integer> favoriteIds = favoriteMatches.stream()
                        .map(Match::getMatchId)
                        .collect(Collectors.toSet());
                homeGroupedAdapter.setFavoriteMatchIds(favoriteIds);
            }
        });
    }

    private List<Object> groupMatchesByLeague(List<Match> matches) {
        Map<League, List<Match>> groupedMap = matches.stream()
                .collect(Collectors.groupingBy(
                        Match::getLeague,
                        LinkedHashMap::new,
                        Collectors.toList()
                ));
        List<Object> displayList = new ArrayList<>();
        for (Map.Entry<League, List<Match>> entry : groupedMap.entrySet()) {
            displayList.add(entry.getKey());
            displayList.addAll(entry.getValue());
        }
        return displayList;
    }

    @Override
    public void onItemClick(Match match) {
        Intent intent = new Intent(getActivity(), MatchDetailsActivity.class);
        intent.putExtra("EXTRA_MATCH", match);
        startActivity(intent);
    }

    @Override
    public void onFavoriteClick(Match match, boolean isFavorite) {
        if (isFavorite) {
            homeViewModel.removeFavorite(match);
        } else {
            homeViewModel.addFavorite(match);
        }
    }

    @Override
    public void onTeamClick(Team team, Match matchContext) {
        Intent intent = new Intent(getActivity(), TeamDetailsActivity.class);
        intent.putExtra(TeamDetailsActivity.EXTRA_TEAM, team);
        if (matchContext != null && matchContext.getLeague() != null) {
            intent.putExtra(TeamDetailsActivity.EXTRA_LEAGUE_ID, matchContext.getLeague().getId());
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(matchContext.getMatchTime());
            intent.putExtra(TeamDetailsActivity.EXTRA_SEASON_YEAR, cal.get(Calendar.YEAR));
        }
        startActivity(intent);
    }
}