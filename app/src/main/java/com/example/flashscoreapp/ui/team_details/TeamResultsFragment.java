package com.example.flashscoreapp.ui.team_details;

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
import com.example.flashscoreapp.data.model.domain.League;
import com.example.flashscoreapp.data.model.domain.Match;
import com.example.flashscoreapp.ui.match_details.MatchDetailsActivity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TeamResultsFragment extends Fragment implements TeamResultsAdapter.OnMatchClickListener {
    private TeamDetailsViewModel viewModel;
    private TeamResultsAdapter adapter;
    private int teamId;

    public static TeamResultsFragment newInstance(int teamId) {
        TeamResultsFragment fragment = new TeamResultsFragment();
        Bundle args = new Bundle();
        args.putInt("TEAM_ID", teamId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            teamId = getArguments().getInt("TEAM_ID");
        }
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
        adapter = new TeamResultsAdapter(teamId);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        // 2. Gán listener cho adapter
        adapter.setOnMatchClickListener(this);

        viewModel.getPastMatches().observe(getViewLifecycleOwner(), matches -> {
            if (matches != null) {
                matches.sort(Comparator.comparingLong(Match::getMatchTime).reversed());
                adapter.setItems(groupMatchesByLeague(matches));
            }
        });
    }

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
    public void onMatchClicked(Match match) {
        Intent intent = new Intent(getActivity(), MatchDetailsActivity.class);
        intent.putExtra("EXTRA_MATCH", match);
        startActivity(intent);
    }
}