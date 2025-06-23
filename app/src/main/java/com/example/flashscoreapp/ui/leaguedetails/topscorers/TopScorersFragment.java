package com.example.flashscoreapp.ui.leaguedetails.topscorers;

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

public class TopScorersFragment extends Fragment {
    private TopScorersViewModel viewModel;
    private TopScorersAdapter adapter;

    public static TopScorersFragment newInstance(int leagueId, int seasonYear) {
        TopScorersFragment fragment = new TopScorersFragment();
        Bundle args = new Bundle();
        args.putInt("LEAGUE_ID", leagueId);
        args.putInt("SEASON_YEAR", seasonYear);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Tái sử dụng layout chỉ chứa RecyclerView
        return inflater.inflate(R.layout.fragment_top_scorers, container, false);
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

        RecyclerView recyclerView = view.findViewById(R.id.main_recycler_view);
        adapter = new TopScorersAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        if (leagueId != 0 && seasonYear != 0) {
            TopScorersViewModelFactory factory = new TopScorersViewModelFactory(requireActivity().getApplication(), leagueId, seasonYear);
            viewModel = new ViewModelProvider(this, factory).get(TopScorersViewModel.class);

            viewModel.getTopScorers().observe(getViewLifecycleOwner(), scorers -> {
                if (scorers != null) {
                    adapter.setTopScorers(scorers);
                }
            });
        }
    }
}