package com.example.flashscoreapp.ui;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.flashscoreapp.ui.home.StandingsTabFragment;

public class LeagueDetailsPagerAdapter extends FragmentStateAdapter {
    private final int leagueId;
    private final int season;

    public LeagueDetailsPagerAdapter(@NonNull Fragment fragment, int leagueId, int season) {
        super(fragment);
        this.leagueId = leagueId;
        this.season = season;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return ResultsTabFragment.newInstance(leagueId, season);
            case 2:
                return new FixturesTabFragment();
            case 0:
            default:
                return StandingsTabFragment.newInstance(leagueId, season);
        }
    }

    @Override
    public int getItemCount() {
        return 3; // Chúng ta có 3 tab
    }
}
