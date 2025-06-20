package com.example.flashscoreapp.ui.match_details;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.flashscoreapp.ui.leaguedetails.standings.StandingsContainerFragment; // Import class này
import com.example.flashscoreapp.ui.match_details.doidau.H2HFragment;
import com.example.flashscoreapp.ui.match_details.lineups.LineupsFragment;
import com.example.flashscoreapp.ui.match_details.statistics.MatchStatisticsFragment;
import com.example.flashscoreapp.ui.match_details.summary.MatchSummaryFragment;

public class MatchDetailsPagerAdapter extends FragmentStateAdapter {
    // Thêm các biến để lưu thông tin cần thiết
    private final int leagueId;
    private final int seasonYear;

    // Sửa constructor để nhận thêm tham số
    public MatchDetailsPagerAdapter(@NonNull FragmentActivity fragmentActivity, int leagueId, int seasonYear) {
        super(fragmentActivity);
        this.leagueId = leagueId;
        this.seasonYear = seasonYear;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return new MatchStatisticsFragment();
            case 2:
                return new LineupsFragment();
            case 3:
                return new H2HFragment();
            case 4:
                return StandingsContainerFragment.newInstance(leagueId, seasonYear);
            case 0:
            default:
                return new MatchSummaryFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 5; // Vẫn là 5 tab
    }
}