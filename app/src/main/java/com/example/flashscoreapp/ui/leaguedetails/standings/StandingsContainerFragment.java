// app/src/main/java/com/example/flashscoreapp/ui/StandingsContainerFragment.java
package com.example.flashscoreapp.ui.leaguedetails.standings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.example.flashscoreapp.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class StandingsContainerFragment extends Fragment {
    private int leagueId;
    private int seasonYear;

    public static StandingsContainerFragment newInstance(int leagueId, int seasonYear) {
        StandingsContainerFragment fragment = new StandingsContainerFragment();
        Bundle args = new Bundle();
        args.putInt("LEAGUE_ID", leagueId);
        args.putInt("SEASON_YEAR", seasonYear);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            leagueId = getArguments().getInt("LEAGUE_ID");
            seasonYear = getArguments().getInt("SEASON_YEAR");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup c, @Nullable Bundle s) {
        return inflater.inflate(R.layout.fragment_container_standings, c, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TabLayout tabLayout = view.findViewById(R.id.tab_layout_standings_container);
        ViewPager2 viewPager = view.findViewById(R.id.view_pager_standings_container);

        viewPager.setAdapter(new StandingsContainerPagerAdapter(this, leagueId, seasonYear));

        viewPager.setOffscreenPageLimit(4);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0: tab.setText("TOÀN THỂ"); break;
                case 1: tab.setText("VUA PHÁ LƯỚI"); break;
                case 2: tab.setText("SÂN NHÀ"); break;
                case 3: tab.setText("SÂN KHÁCH"); break;
            }
        }).attach();
    }
}