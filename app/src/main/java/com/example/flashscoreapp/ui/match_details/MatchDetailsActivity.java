package com.example.flashscoreapp.ui.match_details;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.flashscoreapp.R;
import com.example.flashscoreapp.data.model.domain.Match;
import com.example.flashscoreapp.data.model.domain.Team;
import com.example.flashscoreapp.ui.home.HomeViewModel;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Set;
import java.util.stream.Collectors;

public class MatchDetailsActivity extends AppCompatActivity {
    private MatchDetailsViewModel viewModel;
    private HomeViewModel homeViewModel;
    private Match match;
    private ImageView favoriteIconHome, favoriteIconAway;
    private boolean isHomeFavorite = false;
    private boolean isAwayFavorite = false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_details);

        Toolbar toolbar = findViewById(R.id.toolbar_details);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


        match = (Match) getIntent().getSerializableExtra("EXTRA_MATCH");
        if (match == null) {
            Toast.makeText(this, "Lỗi: không có dữ liệu trận đấu", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // ======================= SỬA LỖI TẠI ĐÂY =======================
        // Khởi tạo Factory với đủ 4 tham số: application, matchId, homeId, awayId
        MatchDetailsViewModelFactory factory = new MatchDetailsViewModelFactory(
                getApplication(),
                match.getMatchId(),
                match.getHomeTeam().getId(),
                match.getAwayTeam().getId()
        );
        viewModel = new ViewModelProvider(this, factory).get(MatchDetailsViewModel.class);
        // ===============================================================

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        // Ánh xạ các biểu tượng yêu thích
        favoriteIconHome = findViewById(R.id.image_favorite_home);
        favoriteIconAway = findViewById(R.id.image_favorite_away);
        updateScoreboard();

        ViewPager2 viewPager = findViewById(R.id.view_pager_match_details);
        TabLayout tabLayout = findViewById(R.id.tab_layout_match_details);
        viewPager.setAdapter(new MatchDetailsPagerAdapter(this));

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0: tab.setText("TÓM TẮT"); break;
                case 1: tab.setText("SỐ LIỆU"); break;
                case 2: tab.setText("ĐỘI HÌNH"); break;
                case 3: tab.setText("ĐỐI ĐẦU"); break;
                case 4: tab.setText("BẢNG XẾP HẠNG"); break;
            }
        }).attach();
        observeFavoriteStatus();

        favoriteIconHome.setOnClickListener(v -> {
            Team homeTeam = match.getHomeTeam();
            if (isHomeFavorite) {
                homeViewModel.removeFavoriteTeam(homeTeam);
            } else {
                homeViewModel.addFavoriteTeam(homeTeam);
            }
        });

        favoriteIconAway.setOnClickListener(v -> {
            Team awayTeam = match.getAwayTeam();
            if (isAwayFavorite) {
                homeViewModel.removeFavoriteTeam(awayTeam);
            } else {
                homeViewModel.addFavoriteTeam(awayTeam);
            }
        });

    }

    private void updateScoreboard() {
        TextView homeName = findViewById(R.id.text_home_name_details);
        TextView awayName = findViewById(R.id.text_away_name_details);
        TextView score = findViewById(R.id.text_score_details);
        TextView status = findViewById(R.id.text_status_details);
        ImageView homeLogo = findViewById(R.id.image_home_logo_details);
        ImageView awayLogo = findViewById(R.id.image_away_logo_details);

        String originalHomeName = match.getHomeTeam().getName();
        String formattedHomeName = originalHomeName.replaceFirst(" ", "\n");
        homeName.setText(formattedHomeName);

        String originalAwayName = match.getAwayTeam().getName();
        String formattedAwayName = originalAwayName.replaceFirst(" ", "\n");
        awayName.setText(formattedAwayName);

        score.setText(match.getScore().getHome() + " - " + match.getScore().getAway());
        status.setText(match.getStatus());

        Glide.with(this).load(match.getHomeTeam().getLogoUrl()).into(homeLogo);
        Glide.with(this).load(match.getAwayTeam().getLogoUrl()).into(awayLogo);
    }

    private void updateFavoriteIcons() {
        favoriteIconHome.setImageResource(isHomeFavorite ? R.drawable.ic_star_filled : R.drawable.ic_star_empty);
        favoriteIconAway.setImageResource(isAwayFavorite ? R.drawable.ic_star_filled : R.drawable.ic_star_empty);
    }

    private void observeFavoriteStatus() {
        homeViewModel.getFavoriteTeams().observe(this, favoriteTeams -> {
            if (favoriteTeams != null && match != null) {
                Set<Integer> favoriteTeamIds = favoriteTeams.stream()
                        .map(ft -> ft.teamId)
                        .collect(Collectors.toSet());

                isHomeFavorite = favoriteTeamIds.contains(match.getHomeTeam().getId());
                isAwayFavorite = favoriteTeamIds.contains(match.getAwayTeam().getId());

                updateFavoriteIcons();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public Match getMatch() { return this.match; }
}