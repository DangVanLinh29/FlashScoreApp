package com.example.flashscoreapp.ui.match_details.doidau;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.flashscoreapp.ui.match_details.MatchDetailsViewModel;
import com.example.flashscoreapp.ui.team_details.TeamDetailsActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class H2HFragment extends Fragment implements MatchAdapter.OnItemClickListener {
    private MatchDetailsViewModel viewModel;
    private RecentMatchesAdapter recentMatchesAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recycler_view_only, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = view.findViewById(R.id.main_recycler_view);

        recentMatchesAdapter = new RecentMatchesAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(recentMatchesAdapter);
        // Gán listener là chính Fragment này
        recentMatchesAdapter.setOnItemClickListener(this);

        // Lấy ViewModel chung từ Activity
        viewModel = new ViewModelProvider(requireActivity()).get(MatchDetailsViewModel.class);

        // Lắng nghe cặp danh sách trận đấu
        viewModel.getRecentMatchesPair().observe(getViewLifecycleOwner(), pair -> {
            if (pair != null && pair.first != null && pair.second != null) {
                Match currentMatch = ((MatchDetailsActivity) requireActivity()).getMatch();
                if (currentMatch == null) return;

                List<Object> displayList = new ArrayList<>();
                displayList.add("5 trận gần nhất của " + currentMatch.getHomeTeam().getName());
                displayList.addAll(pair.first);
                displayList.add("5 trận gần nhất của " + currentMatch.getAwayTeam().getName());
                displayList.addAll(pair.second);

                recentMatchesAdapter.setItems(displayList);
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
        // Tab H2H không cần xử lý yêu thích, có thể để trống
    }

    @Override
    public void onTeamClick(Team team, Match matchContext) {
        Intent intent = new Intent(getActivity(), TeamDetailsActivity.class);
        intent.putExtra(TeamDetailsActivity.EXTRA_TEAM, team);
        if (matchContext != null && matchContext.getLeague() != null) {
            intent.putExtra(TeamDetailsActivity.EXTRA_LEAGUE_ID, matchContext.getLeague().getId());
            intent.putExtra(TeamDetailsActivity.EXTRA_SEASON_YEAR, matchContext.getSeason());
        }
        startActivity(intent);
    }
}