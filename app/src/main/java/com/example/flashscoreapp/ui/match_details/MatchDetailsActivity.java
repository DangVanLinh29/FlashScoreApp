package com.example.flashscoreapp.ui.match_details;

import android.content.Intent;
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
import com.example.flashscoreapp.ui.team_details.TeamDetailsActivity;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Calendar;
import java.util.Set;
import java.util.stream.Collectors;

public class MatchDetailsActivity extends AppCompatActivity {
    private MatchDetailsViewModel viewModel;
    private HomeViewModel homeViewModel;
    private Match match;
    private ImageView favoriteIconHome, favoriteIconAway;
    private ImageView homeLogo, awayLogo;
    private boolean isHomeFavorite = false;
    private boolean isAwayFavorite = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_details);

        match = (Match) getIntent().getSerializableExtra("EXTRA_MATCH");
        if (match == null) {
            Toast.makeText(this, "Lỗi: không có dữ liệu trận đấu", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // SỬA LẠI TẠI ĐÂY: Khởi tạo Factory chỉ với 4 tham số
        MatchDetailsViewModelFactory factory = new MatchDetailsViewModelFactory(
                getApplication(),
                match.getMatchId(),
                match.getHomeTeam().getId(),
                match.getAwayTeam().getId()
        );
        viewModel = new ViewModelProvider(this, factory).get(MatchDetailsViewModel.class);
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        setupToolbar();
        setupScoreboard();
        setupClickListeners();
        setupViewPager();
        observeFavoriteStatus();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_details);
        if (match.getLeague() != null && match.getLeague().getName() != null) {
            toolbar.setTitle(match.getLeague().getName());
        } else {
            toolbar.setTitle("Chi tiết trận đấu");
        }
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void setupScoreboard() {
        TextView homeName = findViewById(R.id.text_home_name_details);
        TextView awayName = findViewById(R.id.text_away_name_details);
        TextView score = findViewById(R.id.text_score_details);
        TextView status = findViewById(R.id.text_status_details);
        homeLogo = findViewById(R.id.image_home_logo_details);
        awayLogo = findViewById(R.id.image_away_logo_details);
        favoriteIconHome = findViewById(R.id.image_favorite_home);
        favoriteIconAway = findViewById(R.id.image_favorite_away);

        homeName.setText(match.getHomeTeam().getName());
        awayName.setText(match.getAwayTeam().getName());
        score.setText(match.getScore().getHome() + " - " + match.getScore().getAway());
        status.setText(match.getStatus());

        Glide.with(this).load(match.getHomeTeam().getLogoUrl()).into(homeLogo);
        Glide.with(this).load(match.getAwayTeam().getLogoUrl()).into(awayLogo);
    }

    private void setupClickListeners() {
        favoriteIconHome.setOnClickListener(v -> {
            if (isHomeFavorite) {
                homeViewModel.removeFavoriteTeam(match.getHomeTeam());
            } else {
                homeViewModel.addFavoriteTeam(match.getHomeTeam());
            }
        });

        favoriteIconAway.setOnClickListener(v -> {
            if (isAwayFavorite) {
                homeViewModel.removeFavoriteTeam(match.getAwayTeam());
            } else {
                homeViewModel.addFavoriteTeam(match.getAwayTeam());
            }
        });

        homeLogo.setOnClickListener(v -> navigateToTeamDetails(match.getHomeTeam()));
        awayLogo.setOnClickListener(v -> navigateToTeamDetails(match.getAwayTeam()));
    }

    private void setupViewPager() {
        ViewPager2 viewPager = findViewById(R.id.view_pager_match_details);
        TabLayout tabLayout = findViewById(R.id.tab_layout_match_details);

        // Lấy leagueId và seasonYear trực tiếp từ đối tượng Match
        int leagueId = match.getLeague().getId();
        int seasonYear = match.getSeason();

        viewPager.setAdapter(new MatchDetailsPagerAdapter(this, leagueId, seasonYear));

        viewPager.setOffscreenPageLimit(5);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0: tab.setText("TÓM TẮT"); break;
                case 1: tab.setText("SỐ LIỆU"); break;
                case 2: tab.setText("ĐỘI HÌNH"); break;
                case 3: tab.setText("ĐỐI ĐẦU"); break;
                case 4: tab.setText("BẢNG XẾP HẠNG"); break;
            }
        }).attach();
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

    private void updateFavoriteIcons() {
        favoriteIconHome.setImageResource(isHomeFavorite ? R.drawable.ic_star_filled : R.drawable.ic_star_empty);
        favoriteIconAway.setImageResource(isAwayFavorite ? R.drawable.ic_star_filled : R.drawable.ic_star_empty);
    }

    private void navigateToTeamDetails(Team team) {
        Intent intent = new Intent(this, TeamDetailsActivity.class);
        intent.putExtra(TeamDetailsActivity.EXTRA_TEAM, team);
        intent.putExtra(TeamDetailsActivity.EXTRA_LEAGUE_ID, match.getLeague().getId());
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(match.getMatchTime());
        intent.putExtra(TeamDetailsActivity.EXTRA_SEASON_YEAR, cal.get(Calendar.YEAR));
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public Match getMatch() { return this.match; }
}