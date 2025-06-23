package com.example.flashscoreapp.ui.favorites;

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

public class FavoritesFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorites, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TabLayout tabLayout = view.findViewById(R.id.tab_layout_favorites);
        ViewPager2 viewPager = view.findViewById(R.id.view_pager_favorites);

        FavoritesPagerAdapter adapter = new FavoritesPagerAdapter(this);
        viewPager.setAdapter(adapter);

        viewPager.setOffscreenPageLimit(2);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText("TRẬN ĐẤU");
            } else {
                tab.setText("ĐỘI BÓNG");
            }
        }).attach();
    }
}