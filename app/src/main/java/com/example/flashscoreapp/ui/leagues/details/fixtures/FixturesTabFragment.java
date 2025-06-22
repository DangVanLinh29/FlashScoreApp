package com.example.flashscoreapp.ui.leagues.details.fixtures;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flashscoreapp.R;
import com.example.flashscoreapp.data.model.domain.Match;
import com.example.flashscoreapp.data.model.domain.RoundHeader;
import com.example.flashscoreapp.data.model.domain.Team;
import com.example.flashscoreapp.ui.home.HomeViewModel;
import com.example.flashscoreapp.ui.home.MatchAdapter;
import com.example.flashscoreapp.ui.leaguedetails.results.ResultsAdapter;
import com.example.flashscoreapp.ui.match_details.MatchDetailsActivity;
import com.example.flashscoreapp.ui.team_details.TeamDetailsActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class FixturesTabFragment extends Fragment implements MatchAdapter.OnItemClickListener {

    private FixturesViewModel fixturesViewModel;
    private HomeViewModel homeViewModel;
    private ResultsAdapter adapter;
    private int leagueId = 0;
    private int seasonYear = 0;

    public static FixturesTabFragment newInstance(int leagueId, int seasonYear) {
        FixturesTabFragment fragment = new FixturesTabFragment();
        Bundle args = new Bundle();
        args.putInt("LEAGUE_ID", leagueId);
        args.putInt("SEASON_YEAR", seasonYear);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recycler_view_only, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        leagueId = getArguments() != null ? getArguments().getInt("LEAGUE_ID") : 0;
        seasonYear = getArguments() != null ? getArguments().getInt("SEASON_YEAR") : 0;

        RecyclerView recyclerView = view.findViewById(R.id.main_recycler_view);
        TextView emptyText = view.findViewById(R.id.text_empty_message);
        adapter = new ResultsAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        adapter.setOnItemClickListener(this);

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        if (leagueId != 0 && seasonYear != 0) {
            FixturesViewModelFactory factory = new FixturesViewModelFactory(requireActivity().getApplication(), leagueId, seasonYear);
            fixturesViewModel = new ViewModelProvider(this, factory).get(FixturesViewModel.class);

            fixturesViewModel.getFixtures().observe(getViewLifecycleOwner(), matches -> {
                if (matches != null && !matches.isEmpty()) {
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyText.setVisibility(View.GONE);
                    List<Object> displayList = groupMatchesByRound(matches);
                    adapter.setItems(displayList);
                } else {
                    recyclerView.setVisibility(View.GONE);
                    emptyText.setVisibility(View.VISIBLE);
                    emptyText.setText("Không có lịch thi đấu nào.");
                    adapter.setItems(new ArrayList<>());
                }
            });
        }

        homeViewModel.getFavoriteMatches().observe(getViewLifecycleOwner(), favoriteMatches -> {
            if (favoriteMatches != null) {
                Set<Integer> favoriteIds = favoriteMatches.stream()
                        .map(Match::getMatchId)
                        .collect(Collectors.toSet());
                adapter.setFavoriteMatchIds(favoriteIds);
            }
        });
    }

    private List<Object> groupMatchesByRound(List<Match> matches) {
        if (matches == null || matches.isEmpty()) {
            return new ArrayList<>();
        }
        matches.sort(Comparator.comparingLong(Match::getMatchTime));
        Map<String, List<Match>> groupedByRound = matches.stream()
                .collect(Collectors.groupingBy(
                        Match::getRound,
                        LinkedHashMap::new,
                        Collectors.toList()
                ));
        List<Object> flattenedList = new ArrayList<>();
        for (String round : groupedByRound.keySet()) {
            String translatedRound = round.replace("Regular Season -", "Vòng");
            flattenedList.add(new RoundHeader(translatedRound.trim()));
            flattenedList.addAll(groupedByRound.get(round));
        }
        return flattenedList;
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
        intent.putExtra(TeamDetailsActivity.EXTRA_LEAGUE_ID, this.leagueId);
        intent.putExtra(TeamDetailsActivity.EXTRA_SEASON_YEAR, this.seasonYear);
        startActivity(intent);
    }
}