package com.example.flashscoreapp.ui.leaguedetails;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.example.flashscoreapp.data.model.domain.Season;
import com.example.flashscoreapp.ui.leagues.details.fixtures.FixturesTabFragment;
import com.example.flashscoreapp.ui.leaguedetails.results.ResultsTabFragment;
import com.example.flashscoreapp.ui.leaguedetails.standings.StandingsContainerFragment;

public class LeagueDetailsPagerAdapter extends FragmentStateAdapter {
    private final int leagueId;
    private final Season season;
    private final boolean isSeasonFinished; // Thêm biến này

    // Sửa constructor để nhận thêm biến isSeasonFinished
    public LeagueDetailsPagerAdapter(@NonNull Fragment fragment, int leagueId, Season season, boolean isSeasonFinished) {
        super(fragment);
        this.leagueId = leagueId;
        this.season = season;
        this.isSeasonFinished = isSeasonFinished;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        int seasonYear = (season != null) ? season.getYear() : 0;

        switch (position) {
            case 0:
                return StandingsContainerFragment.newInstance(leagueId, seasonYear);
            case 1:
                return ResultsTabFragment.newInstance(leagueId, seasonYear);
            case 2:
                // Case này sẽ không bao giờ được gọi nếu mùa giải đã kết thúc
                return FixturesTabFragment.newInstance(leagueId, seasonYear);
            default:
                return new Fragment();
        }
    }

    @Override
    public int getItemCount() {
        // Nếu mùa giải đã kết thúc, chỉ hiển thị 2 tab. Nếu chưa, hiển thị 3 tab.
        return isSeasonFinished ? 2 : 3;
    }
}