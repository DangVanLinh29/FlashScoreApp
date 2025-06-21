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
import com.example.flashscoreapp.data.model.domain.League;
import com.example.flashscoreapp.data.model.domain.Match;
import com.example.flashscoreapp.data.model.domain.Team;
import com.example.flashscoreapp.ui.home.HomeGroupedAdapter;
import com.example.flashscoreapp.ui.home.HomeViewModel;
import com.example.flashscoreapp.ui.home.MatchAdapter;
import com.example.flashscoreapp.ui.match_details.MatchDetailsActivity;
import com.example.flashscoreapp.ui.team_details.TeamDetailsActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class FavoriteTeamsFragment extends Fragment implements MatchAdapter.OnItemClickListener {

    private FavoritesViewModel favoritesViewModel;
    private HomeViewModel homeViewModel;
    private HomeGroupedAdapter groupedAdapter;
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
        textNoItems.setText("Chưa có trận đấu nào của đội yêu thích.");

        groupedAdapter = new HomeGroupedAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(groupedAdapter);
        // Gán listener là chính Fragment này
        groupedAdapter.setOnItemClickListener(this);

        favoritesViewModel = new ViewModelProvider(this).get(FavoritesViewModel.class);
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        observeViewModel();
    }

    private void observeViewModel() {
        // Lắng nghe danh sách các trận của đội yêu thích
        favoritesViewModel.getFavoriteTeamMatches().observe(getViewLifecycleOwner(), matches -> {
            if (matches != null && !matches.isEmpty()) {
                recyclerView.setVisibility(View.VISIBLE);
                textNoItems.setVisibility(View.GONE);
                List<Object> groupedList = groupMatchesByLeague(matches);
                groupedAdapter.setDisplayList(groupedList);
            } else {
                recyclerView.setVisibility(View.GONE);
                textNoItems.setVisibility(View.VISIBLE);
                groupedAdapter.setDisplayList(new ArrayList<>());
            }
        });

        // Lắng nghe danh sách các trận được yêu thích để cập nhật trạng thái ngôi sao
        homeViewModel.getFavoriteMatches().observe(getViewLifecycleOwner(), favoriteMatches -> {
            if (favoriteMatches != null) {
                Set<Integer> favoriteIds = favoriteMatches.stream()
                        .map(Match::getMatchId)
                        .collect(Collectors.toSet());
                groupedAdapter.setFavoriteMatchIds(favoriteIds);
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

}