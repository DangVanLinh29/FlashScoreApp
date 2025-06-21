package com.example.flashscoreapp.ui.leaguedetails.results;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.flashscoreapp.R;
import com.example.flashscoreapp.data.model.domain.Match;
import com.example.flashscoreapp.data.model.domain.Team;
import com.example.flashscoreapp.ui.home.HomeViewModel;
import com.example.flashscoreapp.ui.home.MatchAdapter;
import com.example.flashscoreapp.ui.match_details.MatchDetailsActivity;
import com.example.flashscoreapp.ui.team_details.TeamDetailsActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Set;
import java.util.stream.Collectors;

public class ResultsTabFragment extends Fragment implements MatchAdapter.OnItemClickListener {

    private ResultsViewModel resultsViewModel;
    private HomeViewModel homeViewModel;
    private ResultsAdapter adapter;
    private int leagueId = 0;
    private int seasonYear = 0;

    public static ResultsTabFragment newInstance(int leagueId, int seasonYear) {
        ResultsTabFragment fragment = new ResultsTabFragment();
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

        // Gán listener là chính Fragment này
        adapter.setOnItemClickListener(this);

        if (leagueId != 0 && seasonYear != 0) {
            homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
            ResultsViewModelFactory factory = new ResultsViewModelFactory(requireActivity().getApplication(), leagueId, seasonYear);
            resultsViewModel = new ViewModelProvider(this, factory).get(ResultsViewModel.class);

            resultsViewModel.getGroupedResults().observe(getViewLifecycleOwner(), items -> {
                if (items != null && !items.isEmpty()) {
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyText.setVisibility(View.GONE);
                    adapter.setItems(items);
                } else {
                    recyclerView.setVisibility(View.GONE);
                    emptyText.setVisibility(View.VISIBLE);
                    emptyText.setText("Không có kết quả nào cho mùa giải này.");
                    adapter.setItems(new ArrayList<>());
                }
            });

            homeViewModel.getFavoriteMatches().observe(getViewLifecycleOwner(), favoriteMatches -> {
                if (favoriteMatches != null) {
                    Set<Integer> favoriteIds = favoriteMatches.stream()
                            .map(Match::getMatchId)
                            .collect(Collectors.toSet());
                    adapter.setFavoriteMatchIds(favoriteIds);
                }
            });
        }
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



}