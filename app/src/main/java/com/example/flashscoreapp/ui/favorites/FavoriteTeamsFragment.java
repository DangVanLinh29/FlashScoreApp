package com.example.flashscoreapp.ui.favorites;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.example.flashscoreapp.data.model.local.FavoriteTeam;
import com.example.flashscoreapp.ui.home.MatchAdapter;
import com.example.flashscoreapp.ui.match_details.MatchDetailsActivity;
import com.example.flashscoreapp.ui.team_details.TeamDetailsActivity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class FavoriteTeamsFragment extends Fragment implements MatchAdapter.OnItemClickListener {

    private static final String TAG = "FavoriteTeamsFragment";
    private FavoritesViewModel favoritesViewModel;
    private FavoriteTeamsAdapter teamsAdapter;
    private RecyclerView recyclerView;
    private TextView textNoItems;

    private List<FavoriteTeam> currentFavoriteTeams = new ArrayList<>();
    private List<Match> currentFavoriteTeamMatches = new ArrayList<>();

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

        teamsAdapter = new FavoriteTeamsAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(teamsAdapter);
        teamsAdapter.setOnItemClickListener(this);

        favoritesViewModel = new ViewModelProvider(this).get(FavoritesViewModel.class);
        observeViewModel();
    }

    private void observeViewModel() {
        favoritesViewModel.getFavoriteTeams().observe(getViewLifecycleOwner(), favoriteTeams -> {
            Log.d(TAG, "Observer: Danh sách đội yêu thích đã cập nhật với " + (favoriteTeams != null ? favoriteTeams.size() : 0) + " đội.");
            currentFavoriteTeams = favoriteTeams;
            buildAndDisplayList();
        });

        favoritesViewModel.getFavoriteTeamMatches().observe(getViewLifecycleOwner(), matches -> {
            Log.d(TAG, "Observer: Danh sách trận đấu của đội yêu thích đã cập nhật với " + (matches != null ? matches.size() : 0) + " trận.");
            currentFavoriteTeamMatches = matches;
            buildAndDisplayList();
        });

        favoritesViewModel.getFavoriteMatches().observe(getViewLifecycleOwner(), favoriteMatches -> {
            if (favoriteMatches != null) {
                Set<Integer> favoriteIds = favoriteMatches.stream()
                        .map(Match::getMatchId)
                        .collect(Collectors.toSet());
                teamsAdapter.setFavoriteMatchIds(favoriteIds);
            }
        });
    }

    private void buildAndDisplayList() {
        if (currentFavoriteTeams == null || currentFavoriteTeams.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            textNoItems.setVisibility(View.VISIBLE);
            textNoItems.setText("Thêm đội bóng vào Yêu thích để xem lịch thi đấu của họ tại đây.");
            teamsAdapter.setDisplayList(new ArrayList<>());
            Log.d(TAG, "Build bị hủy: Chưa có đội nào được yêu thích.");
            return;
        }

        if (currentFavoriteTeamMatches == null || currentFavoriteTeamMatches.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            textNoItems.setVisibility(View.VISIBLE);
            textNoItems.setText("Các đội bạn yêu thích không có trận đấu nào sắp tới.");
            teamsAdapter.setDisplayList(new ArrayList<>());
            Log.d(TAG, "Build bị hủy: Không có trận đấu nào sắp tới cho các đội yêu thích.");
            return;
        }

        Map<Team, List<Match>> groupedByTeam = new LinkedHashMap<>();
        Set<Integer> favoriteTeamIds = currentFavoriteTeams.stream().map(ft -> ft.teamId).collect(Collectors.toSet());

        // Gom các trận đấu vào map theo đội
        for (Match match : currentFavoriteTeamMatches) {
            Team homeTeam = match.getHomeTeam();
            Team awayTeam = match.getAwayTeam();

            if (favoriteTeamIds.contains(homeTeam.getId())) {
                groupedByTeam.computeIfAbsent(homeTeam, k -> new ArrayList<>()).add(match);
            }
            if (favoriteTeamIds.contains(awayTeam.getId())) {
                groupedByTeam.computeIfAbsent(awayTeam, k -> new ArrayList<>()).add(match);
            }
        }

        List<Object> displayList = new ArrayList<>();
        // Sắp xếp các trận của mỗi đội và tạo danh sách hiển thị
        for (Map.Entry<Team, List<Match>> entry : groupedByTeam.entrySet()) {
            displayList.add(entry.getKey()); // Thêm header đội bóng
            entry.getValue().sort(Comparator.comparingLong(Match::getMatchTime)); // Sắp xếp trận đấu theo thời gian
            displayList.addAll(entry.getValue());
        }

        Log.d(TAG, "Build thành công. Hiển thị " + displayList.size() + " mục (bao gồm " + groupedByTeam.size() + " đội).");
        recyclerView.setVisibility(View.VISIBLE);
        textNoItems.setVisibility(View.GONE);
        teamsAdapter.setDisplayList(displayList);
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
            favoritesViewModel.removeFavorite(match);
        } else {
            favoritesViewModel.addFavorite(match);
        }
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