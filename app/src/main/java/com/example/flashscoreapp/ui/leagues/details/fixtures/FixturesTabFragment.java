package com.example.flashscoreapp.ui.leagues.details.fixtures;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.flashscoreapp.ui.home.HomeViewModel;
import com.example.flashscoreapp.ui.home.MatchAdapter;
import com.example.flashscoreapp.ui.leaguedetails.results.ResultsAdapter;
import com.example.flashscoreapp.ui.match_details.MatchDetailsActivity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class FixturesTabFragment extends Fragment {

    private FixturesViewModel fixturesViewModel;
    private HomeViewModel homeViewModel;
    private ResultsAdapter adapter;

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

        int leagueId = getArguments() != null ? getArguments().getInt("LEAGUE_ID") : 0;
        int seasonYear = getArguments() != null ? getArguments().getInt("SEASON_YEAR") : 0;

        RecyclerView recyclerView = view.findViewById(R.id.main_recycler_view);
        adapter = new ResultsAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        if (leagueId != 0 && seasonYear != 0) {
            FixturesViewModelFactory factory = new FixturesViewModelFactory(requireActivity().getApplication(), leagueId, seasonYear);
            fixturesViewModel = new ViewModelProvider(this, factory).get(FixturesViewModel.class);

            // Lắng nghe danh sách lịch thi đấu
            fixturesViewModel.getFixtures().observe(getViewLifecycleOwner(), matches -> {
                if (matches != null) {
                    // --- BẮT ĐẦU SỬA LỖI ---
                    // 1. Gọi hàm để nhóm các trận đấu lại theo vòng,
                    // hàm này sẽ trả về đúng kiểu List<Object> mà adapter cần.
                    List<Object> displayList = groupMatchesByRound(matches);

                    // 2. Truyền danh sách đã được xử lý vào adapter.
                    adapter.setItems(displayList);
                    // --- KẾT THÚC SỬA LỖI ---
                }
            });
        }

        // Lắng nghe danh sách các trận đã yêu thích để cập nhật UI
        homeViewModel.getFavoriteMatches().observe(getViewLifecycleOwner(), favoriteMatches -> {
            if (favoriteMatches != null) {
                Set<Integer> favoriteIds = favoriteMatches.stream()
                        .map(Match::getMatchId)
                        .collect(Collectors.toSet());
                adapter.setFavoriteMatchIds(favoriteIds);
            }
        });

        // Cài đặt listener cho adapter
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
    }

    // Hàm để nhóm các trận đấu theo từng vòng
    private List<Object> groupMatchesByRound(List<Match> matches) {
        if (matches == null || matches.isEmpty()) {
            return new ArrayList<>();
        }

        // Sắp xếp các trận đấu theo thời gian tăng dần (trận nào đá trước xếp trước)
        matches.sort(Comparator.comparingLong(Match::getMatchTime));

        Map<String, List<Match>> groupedByRound = matches.stream()
                .collect(Collectors.groupingBy(
                        Match::getRound,
                        LinkedHashMap::new, // Giữ đúng thứ tự các vòng đấu
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
}