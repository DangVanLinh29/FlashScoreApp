package com.example.flashscoreapp.ui.team_details;

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
import com.example.flashscoreapp.data.model.domain.Team;
import com.example.flashscoreapp.data.model.remote.ApiVenue;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import java.util.Calendar;

public class TeamDetailsActivity extends AppCompatActivity {
    public static final String EXTRA_TEAM = "EXTRA_TEAM";
    public static final String EXTRA_LEAGUE_ID = "EXTRA_LEAGUE_ID";
    public static final String EXTRA_SEASON_YEAR = "EXTRA_SEASON_YEAR";

    private Team team;
    private TeamDetailsViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_details);

        // 1. Lấy dữ liệu từ Intent
        team = (Team) getIntent().getSerializableExtra(EXTRA_TEAM);
        int leagueId = getIntent().getIntExtra(EXTRA_LEAGUE_ID, 0);

        int seasonYear = getIntent().getIntExtra(EXTRA_SEASON_YEAR, Calendar.getInstance().get(Calendar.YEAR));

        // 2. Kiểm tra dữ liệu đầu vào
        if (team == null) {
            Toast.makeText(this, "Thiếu dữ liệu đội bóng.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 3. Khởi tạo ViewModel (dùng Factory không cần season)
        // ViewModel sẽ tự tính toán mùa giải bóng đá phù hợp bên trong nó.
        TeamDetailsViewModelFactory factory = new TeamDetailsViewModelFactory(getApplication(), team.getId(), seasonYear);
        viewModel = new ViewModelProvider(this, factory).get(TeamDetailsViewModel.class);

        // 4. Gọi các hàm setup
        setupToolbar();
        setupHeader();
        // Truyền leagueId và seasonYear cho PagerAdapter để các tab con sử dụng
        setupViewPager(leagueId, seasonYear);
        observeViewModel();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_team_details);
        toolbar.setTitle(team.getName());
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void setupHeader() {
        ImageView teamLogo = findViewById(R.id.image_team_logo);
        TextView teamName = findViewById(R.id.text_team_name);

        teamName.setText(team.getName());
        Glide.with(this).load(team.getLogoUrl()).into(teamLogo);
    }

    private void setupViewPager(int leagueId, int seasonYear) {
        ViewPager2 viewPager = findViewById(R.id.view_pager_team);
        TabLayout tabLayout = findViewById(R.id.tab_layout_team);

        // Truyền các tham số vào PagerAdapter
        viewPager.setAdapter(new TeamDetailsPagerAdapter(this, team.getId(), leagueId, seasonYear));

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0: tab.setText("KẾT QUẢ"); break;
                case 1: tab.setText("LỊCH THI ĐẤU"); break;
                case 2: tab.setText("BẢNG XẾP HẠNG"); break;
                case 3: tab.setText("ĐỘI HÌNH"); break;
            }
        }).attach();
    }

    private void observeViewModel() {
        TextView stadiumName = findViewById(R.id.text_stadium_name);
        TextView stadiumCapacity = findViewById(R.id.text_stadium_capacity);

        viewModel.getTeamDetails().observe(this, teamDetailsResponse -> {
            if (teamDetailsResponse != null && teamDetailsResponse.getVenue() != null) {
                ApiVenue venue = teamDetailsResponse.getVenue();
                String stadiumInfo = venue.getName() + " (" + venue.getCity() + ")";
                stadiumName.setText(stadiumInfo);

                String capacityInfo = "Sức chứa: " + venue.getCapacity();
                stadiumCapacity.setText(capacityInfo);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}