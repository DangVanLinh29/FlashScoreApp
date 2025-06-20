package com.example.flashscoreapp.ui.match_details;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.flashscoreapp.ui.match_details.doidau.H2HFragment;
import com.example.flashscoreapp.ui.match_details.lineups.LineupsFragment;
import com.example.flashscoreapp.ui.match_details.statistics.MatchStatisticsFragment;
import com.example.flashscoreapp.ui.match_details.summary.MatchSummaryFragment;

public class MatchDetailsPagerAdapter extends FragmentStateAdapter {

    public MatchDetailsPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                // Tab "SỐ LIỆU"
                return new MatchStatisticsFragment();
            case 2:
                // Tab "ĐỘI HÌNH"
                return new LineupsFragment();
            case 3:
                // Tab "ĐỐI ĐẦU" - Đảm bảo trả về H2HFragment
                return new H2HFragment();
            case 4:
                // Tab "BẢNG XẾP HẠNG" - Tạm thời để trống
                return new Fragment();
            case 0:
            default:
                // Tab "TÓM TẮT"
                return new MatchSummaryFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 5; // Tổng số tab
    }
}