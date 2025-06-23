package com.example.flashscoreapp.ui.leaguedetails;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.flashscoreapp.R;
import com.example.flashscoreapp.data.model.domain.Season;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LeagueDetailsFragment extends Fragment {

    private int leagueId;
    private String leagueName;
    private String leagueLogoUrl;
    private List<Season> allSeasons;
    private Season selectedSeasonObject;

    public static LeagueDetailsFragment newInstance(int leagueId, String leagueName, String leagueLogoUrl, List<Season> seasons) {
        LeagueDetailsFragment fragment = new LeagueDetailsFragment();
        Bundle args = new Bundle();
        args.putInt("LEAGUE_ID", leagueId);
        args.putString("LEAGUE_NAME", leagueName);
        args.putString("LEAGUE_LOGO_URL", leagueLogoUrl);
        args.putSerializable("SEASONS_LIST", (Serializable) seasons);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            leagueId = getArguments().getInt("LEAGUE_ID");
            leagueName = getArguments().getString("LEAGUE_NAME");
            leagueLogoUrl = getArguments().getString("LEAGUE_LOGO_URL");
            allSeasons = (List<Season>) getArguments().getSerializable("SEASONS_LIST");

            if (allSeasons != null && !allSeasons.isEmpty()) {
                // Giữ nguyên logic chọn mùa giải mới nhất của bạn
                selectedSeasonObject = allSeasons.get(allSeasons.size() - 1);
            }
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup c, @Nullable Bundle s) {
        return inflater.inflate(R.layout.fragment_league_details, c, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView leagueLogoImageView = view.findViewById(R.id.image_league_logo);
        TextView leagueNameTextView = view.findViewById(R.id.text_league_name_header);
        Spinner seasonSpinner = view.findViewById(R.id.spinner_season);
        TabLayout tabLayout = view.findViewById(R.id.tab_layout_main);
        ViewPager2 viewPager = view.findViewById(R.id.view_pager_main);

        leagueNameTextView.setText(leagueName);
        Glide.with(this).load(leagueLogoUrl)
                .placeholder(R.drawable.ic_leagues_24)
                .error(R.drawable.ic_leagues_24)
                .into(leagueLogoImageView);

        // Setup Spinner trước
        setupSeasonSpinner(view, seasonSpinner, viewPager);

        // Cập nhật giao diện và các tab dựa trên mùa giải được chọn ban đầu
        updateForSelectedSeason(view, viewPager, tabLayout);
    }

    // Tách ra một hàm để cập nhật giao diện và các tab
    private void updateForSelectedSeason(View rootView, ViewPager2 viewPager, TabLayout tabLayout) {
        boolean isFinished = checkSeasonFinished(selectedSeasonObject);
        updateSeasonUI(rootView, selectedSeasonObject, isFinished);

        viewPager.setAdapter(new LeagueDetailsPagerAdapter(this, leagueId, selectedSeasonObject, isFinished));
        viewPager.setOffscreenPageLimit(3);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("BẢNG XẾP HẠNG");
                    break;
                case 1:
                    tab.setText("KẾT QUẢ");
                    break;
                case 2:
                    tab.setText("LỊCH THI ĐẤU");
                    break;
            }
        }).attach();
    }


    private void setupSeasonSpinner(View rootView, Spinner spinner, ViewPager2 viewPager) {
        if (allSeasons == null || allSeasons.isEmpty()) return;

        // Giữ nguyên logic tạo danh sách năm và chọn index của bạn
        List<String> seasonYears = new ArrayList<>();
        int selectionIndex = 0;
        for (int i = 0; i < allSeasons.size(); i++) {
            Season s = allSeasons.get(i);
            String seasonString;
            try {
                String startYearStr = s.getStart().substring(0, 4);
                String endYearStr = s.getEnd().substring(0, 4);
                if (startYearStr.equals(endYearStr)) {
                    seasonString = startYearStr;
                } else {
                    seasonString = s.getYear() + "/" + (s.getYear() + 1);
                }
            } catch (Exception e) {
                seasonString = s.getYear() + "/" + (s.getYear() + 1);
            }
            seasonYears.add(seasonString);
            if (selectedSeasonObject != null && s.getYear() == selectedSeasonObject.getYear()) {
                selectionIndex = i;
            }
        }
        Collections.reverse(seasonYears);
        selectionIndex = seasonYears.size() - 1 - selectionIndex;

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, seasonYears);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(selectionIndex);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Giữ nguyên logic chọn mùa giải của bạn
                selectedSeasonObject = allSeasons.get(allSeasons.size() - 1 - position);

                // Khi một mùa giải mới được chọn, cập nhật lại toàn bộ giao diện và các tab
                updateForSelectedSeason(rootView, viewPager, (TabLayout) rootView.findViewById(R.id.tab_layout_main));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void updateSeasonUI(View rootView, Season season, boolean isFinished) {
        View seasonProgressContainer = rootView.findViewById(R.id.season_progress_container);
        if (season == null) return;

        if (isFinished) {
            seasonProgressContainer.setVisibility(View.GONE);
            return;
        }

        seasonProgressContainer.setVisibility(View.VISIBLE);
        TextView textStartDate = rootView.findViewById(R.id.text_season_start_date);
        TextView textEndDate = rootView.findViewById(R.id.text_season_end_date);
        ProgressBar seasonProgressBar = rootView.findViewById(R.id.progress_bar_season);
        // ... (phần còn lại của hàm giữ nguyên) ...
    }

    private boolean checkSeasonFinished(Season season) {
        // Nếu không có thông tin mùa giải hoặc ngày kết thúc, ta mặc định là nó chưa kết thúc
        if (season == null || season.getEnd() == null) {
            return false;
        }

        try {
            // Định dạng ngày tháng từ API
            SimpleDateFormat apiFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            // Lấy ngày kết thúc của mùa giải
            Date endDate = apiFormat.parse(season.getEnd());
            // Lấy ngày giờ hiện tại
            Date today = new Date();

            // Mùa giải CHỈ được coi là đã kết thúc NẾU ngày hôm nay đã trôi qua ngày kết thúc của nó.
            // Logic này đúng cho cả mùa trong quá khứ, hiện tại và tương lai.
            return today.after(endDate);

        } catch (ParseException e) {
            e.printStackTrace();
            // Nếu có lỗi parse ngày tháng, an toàn nhất là không ẩn gì cả
            return false;
        }
    }
}