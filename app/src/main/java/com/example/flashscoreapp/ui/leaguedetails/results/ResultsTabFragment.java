package com.example.flashscoreapp.ui.leaguedetails.results;

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
import com.example.flashscoreapp.data.model.domain.RoundHeader;
import com.example.flashscoreapp.ui.home.HomeViewModel;
import com.example.flashscoreapp.ui.home.MatchAdapter;
import com.example.flashscoreapp.ui.match_details.MatchDetailsActivity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ResultsTabFragment extends Fragment {

    private ResultsViewModel resultsViewModel;
    private HomeViewModel homeViewModel;
    private ResultsAdapter adapter;

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

        int leagueId = getArguments() != null ? getArguments().getInt("LEAGUE_ID") : 0;
        int seasonYear = getArguments() != null ? getArguments().getInt("SEASON_YEAR") : 0;

        RecyclerView recyclerView = view.findViewById(R.id.main_recycler_view);
        adapter = new ResultsAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new MatchAdapter.OnItemClickListener() {
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
        });

        if (leagueId != 0 && seasonYear != 0) {
            homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
            ResultsViewModelFactory factory = new ResultsViewModelFactory(requireActivity().getApplication(), leagueId, seasonYear);
            resultsViewModel = new ViewModelProvider(this, factory).get(ResultsViewModel.class);

            // Giả sử ViewModel trả về List<Match>
            resultsViewModel.getGroupedResults().observe(getViewLifecycleOwner(), matches -> {
                if (matches != null) {
                    // --- BẮT ĐẦU SỬA LỖI ---
                    // 1. Chuyển đổi List<Match> thành List<Object> bằng cách nhóm theo vòng
                    List<Object> displayList = groupResultsByRound(matches);
                    // 2. Truyền danh sách đã xử lý vào adapter
                    adapter.setItems(displayList);
                    // --- KẾT THÚC SỬA LỖI ---
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

    // Hàm để nhóm các trận đã kết thúc theo vòng đấu
    private List<Object> groupResultsByRound(List<Object> rawItems) {
        // Chuyển đổi List<Object> từ ViewModel về List<Match> để xử lý
        List<Match> matches = new ArrayList<>();
        for (Object item : rawItems) {
            if (item instanceof Match) {
                matches.add((Match) item);
            }
        }

        if (matches.isEmpty()) {
            return new ArrayList<>();
        }

        Map<String, List<Match>> groupedByRound = matches.stream()
                .collect(Collectors.groupingBy(
                        Match::getRound,
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        // Sắp xếp các vòng đấu theo thứ tự giảm dần (Vòng 38, Vòng 37...)
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
            if(matchesInRound != null) {
                // Sắp xếp các trận trong cùng một vòng theo thời gian
                matchesInRound.sort(Comparator.comparingLong(Match::getMatchTime).reversed());
                flattenedList.addAll(matchesInRound);
            }
        }
        return flattenedList;
    }
}