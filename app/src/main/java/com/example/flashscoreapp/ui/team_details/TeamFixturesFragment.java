package com.example.flashscoreapp.ui.team_details;

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
import com.example.flashscoreapp.ui.match_details.MatchDetailsActivity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TeamFixturesFragment extends Fragment implements TeamFixturesAdapter.OnFixtureClickListener {
    private TeamDetailsViewModel viewModel;
    private TeamFixturesAdapter adapter;

    public static TeamFixturesFragment newInstance(int teamId) {
        TeamFixturesFragment fragment = new TeamFixturesFragment();
        Bundle args = new Bundle();
        args.putInt("TEAM_ID", teamId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(TeamDetailsViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recycler_view_only, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = view.findViewById(R.id.main_recycler_view);
        TextView emptyText = view.findViewById(R.id.text_empty_message);

        adapter = new TeamFixturesAdapter();
        adapter.setOnFixtureClickListener(this); // Gán listener
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        viewModel.getUpcomingFixtures().observe(getViewLifecycleOwner(), fixtures -> {
            if (fixtures != null && !fixtures.isEmpty()) {
                recyclerView.setVisibility(View.VISIBLE);
                emptyText.setVisibility(View.GONE);

                // Sắp xếp các trận đấu theo thời gian tăng dần
                fixtures.sort(Comparator.comparingLong(Match::getMatchTime));
                adapter.setItems(groupMatchesByLeague(fixtures));
            } else {
                recyclerView.setVisibility(View.GONE);
                emptyText.setVisibility(View.VISIBLE);
                emptyText.setText("Không có lịch thi đấu nào.");
                adapter.setItems(new ArrayList<>());
            }
        });
    }

    // Hàm nhóm các trận đấu theo giải đấu
    private List<Object> groupMatchesByLeague(List<Match> matches) {
        Map<League, List<Match>> groupedMap = matches.stream()
                .collect(Collectors.groupingBy(Match::getLeague, LinkedHashMap::new, Collectors.toList()));

        List<Object> displayList = new ArrayList<>();
        for (Map.Entry<League, List<Match>> entry : groupedMap.entrySet()) {
            displayList.add(entry.getKey());
            displayList.addAll(entry.getValue());
        }
        return displayList;
    }

    @Override
    public void onFixtureClicked(Match match) {
        Intent intent = new Intent(getActivity(), MatchDetailsActivity.class);
        intent.putExtra("EXTRA_MATCH", match);
        startActivity(intent);
    }
}