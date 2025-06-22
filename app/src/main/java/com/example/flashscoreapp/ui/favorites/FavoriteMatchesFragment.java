package com.example.flashscoreapp.ui.favorites;

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
import com.example.flashscoreapp.ui.home.MatchAdapter;
import com.example.flashscoreapp.ui.match_details.MatchDetailsActivity;
import com.example.flashscoreapp.ui.team_details.TeamDetailsActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class FavoriteMatchesFragment extends Fragment implements MatchAdapter.OnItemClickListener {

    private FavoritesViewModel viewModel;
    private MatchAdapter matchAdapter;
    private RecyclerView recyclerView;
    private TextView textNoItems;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list_with_empty_text, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.main_recycler_view);
        textNoItems = view.findViewById(R.id.text_empty_message);
        textNoItems.setText("Chưa có trận đấu nào được yêu thích.");

        matchAdapter = new MatchAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(matchAdapter);
        matchAdapter.setOnItemClickListener(this);

        viewModel = new ViewModelProvider(this).get(FavoritesViewModel.class);
        viewModel.getFavoriteMatches().observe(getViewLifecycleOwner(), favoriteMatches -> {
            if (favoriteMatches != null && !favoriteMatches.isEmpty()) {
                recyclerView.setVisibility(View.VISIBLE);
                textNoItems.setVisibility(View.GONE);
                matchAdapter.setMatches(favoriteMatches);
                Set<Integer> favoriteIds = favoriteMatches.stream()
                        .map(Match::getMatchId)
                        .collect(Collectors.toSet());
                matchAdapter.setFavoriteMatchIds(favoriteIds);
            } else {
                recyclerView.setVisibility(View.GONE);
                textNoItems.setVisibility(View.VISIBLE);
                matchAdapter.setMatches(new ArrayList<>());
            }
        });
    }

    @Override
    public void onItemClick(Match match) {
        Intent intent = new Intent(getActivity(), MatchDetailsActivity.class);
        intent.putExtra("EXTRA_MATCH", match);
        startActivity(intent);
    }

    @Override
    public void onFavoriteClick(Match match, boolean isFavorite) {
        // Màn hình này chỉ hiển thị các trận đã yêu thích,
        // nên isFavorite luôn là true. Click vào chỉ có thể là xóa.
        if(isFavorite) {
            viewModel.removeFavorite(match);
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