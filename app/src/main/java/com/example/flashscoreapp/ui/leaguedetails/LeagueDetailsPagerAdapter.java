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

    public LeagueDetailsPagerAdapter(@NonNull Fragment fragment, int leagueId, Season season) {
        super(fragment);
        this.leagueId = leagueId;
        this.season = season;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Lấy ra năm của mùa giải một cách an toàn
        int seasonYear = (season != null) ? season.getYear() : 0;

        switch (position) {
            case 0:
                // Tab Bảng xếp hạng
                return StandingsContainerFragment.newInstance(leagueId, seasonYear);
            case 1:
                // Tab Kết quả
                return ResultsTabFragment.newInstance(leagueId, seasonYear);
            case 2:
                // SỬA TẠI ĐÂY:
                // Tab Lịch thi đấu cũng cần leagueId và seasonYear
                return FixturesTabFragment.newInstance(leagueId, seasonYear);
            default:
                return new Fragment();
        }
    }

    @Override
    public int getItemCount() {
        // SỬA Ở ĐÂY: Chúng ta chỉ có 3 tab chính
        return 3;
    }
}