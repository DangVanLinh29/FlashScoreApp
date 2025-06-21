package com.example.flashscoreapp.ui.team_details;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.example.flashscoreapp.ui.leaguedetails.standings.OverallStandingsFragment; // Import fragment này

public class TeamDetailsPagerAdapter extends FragmentStateAdapter {
    private final int teamId;
    private final int leagueId;
    private final int seasonYear;

    public TeamDetailsPagerAdapter(@NonNull FragmentActivity fragmentActivity, int teamId, int leagueId, int seasonYear) {
        super(fragmentActivity);
        this.teamId = teamId;
        this.leagueId = leagueId;
        this.seasonYear = seasonYear;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return TeamFixturesFragment.newInstance(teamId);
            case 2:
                return OverallStandingsFragment.newInstance(leagueId, seasonYear);
            case 3:
                return TeamSquadFragment.newInstance();
            case 0:
            default:
                return TeamResultsFragment.newInstance(teamId);
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}