package com.example.flashscoreapp.ui.leaguedetails.standings;

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
import com.example.flashscoreapp.data.model.domain.StandingItem;
import com.example.flashscoreapp.data.model.domain.Team;
import com.example.flashscoreapp.ui.team_details.TeamDetailsActivity;
import java.util.ArrayList;
import java.util.List;

public class OverallStandingsFragment extends Fragment implements StandingsAdapter.OnTeamClickListener {

    private StandingsViewModel standingsViewModel;
    private StandingsAdapter adapter;

    public static OverallStandingsFragment newInstance(int leagueId, int seasonYear) {
        OverallStandingsFragment fragment = new OverallStandingsFragment();
        Bundle args = new Bundle();
        args.putInt("LEAGUE_ID", leagueId);
        args.putInt("SEASON_YEAR", seasonYear);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_overall_standings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int leagueId = 0;
        int seasonYear = 0;
        if (getArguments() != null) {
            leagueId = getArguments().getInt("LEAGUE_ID");
            seasonYear = getArguments().getInt("SEASON_YEAR");
        }

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_standings);
        adapter = new StandingsAdapter();
        adapter.setOnTeamClickListener(this); // Dòng này giờ sẽ hết báo lỗi
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        if (leagueId != 0 && seasonYear != 0) {
            StandingsViewModelFactory factory = new StandingsViewModelFactory(requireActivity().getApplication(), leagueId, seasonYear);
            standingsViewModel = new ViewModelProvider(this, factory).get(StandingsViewModel.class);
            observeViewModel();
        }
    }

    @Override
    public void onTeamClicked(Team team) {
        Intent intent = new Intent(getActivity(), TeamDetailsActivity.class);
        intent.putExtra(TeamDetailsActivity.EXTRA_TEAM, team);
        // Lấy leagueId và seasonYear của chính Fragment này để truyền đi
        int leagueId = getArguments() != null ? getArguments().getInt("LEAGUE_ID") : 0;
        int seasonYear = getArguments() != null ? getArguments().getInt("SEASON_YEAR") : 0;
        intent.putExtra(TeamDetailsActivity.EXTRA_LEAGUE_ID, leagueId);
        intent.putExtra(TeamDetailsActivity.EXTRA_SEASON_YEAR, seasonYear);
        startActivity(intent);
    }

    private void observeViewModel() {
        standingsViewModel.getStandings().observe(getViewLifecycleOwner(), allGroups -> {
            if (allGroups != null && !allGroups.isEmpty()) {
                List<Object> displayList = new ArrayList<>();
                for (List<StandingItem> group : allGroups) {
                    if (group != null && !group.isEmpty()) {
                        displayList.add(group.get(0).getGroup());
                        displayList.addAll(group);
                    }
                }
                adapter.setDisplayList(displayList);
            } else {
                adapter.setDisplayList(new ArrayList<>());
            }
        });
    }
}