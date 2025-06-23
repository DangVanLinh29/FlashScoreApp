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

        // Ánh xạ các view từ layout
        ImageView leagueLogoImageView = view.findViewById(R.id.image_league_logo);
        TextView leagueNameTextView = view.findViewById(R.id.text_league_name_header);
        Spinner seasonSpinner = view.findViewById(R.id.spinner_season);
        TabLayout tabLayout = view.findViewById(R.id.tab_layout_main);
        ViewPager2 viewPager = view.findViewById(R.id.view_pager_main);


        // Gán dữ liệu cho header
        leagueNameTextView.setText(leagueName);
        Glide.with(this)
                .load(leagueLogoUrl)
                .placeholder(R.drawable.ic_leagues_24)
                .error(R.drawable.ic_leagues_24)
                .into(leagueLogoImageView);

        // Cập nhật UI cho mùa giải được chọn ban đầu
        updateSeasonUI(view, selectedSeasonObject);

        // Setup Spinner mùa giải
        // SỬA: Truyền vào `view` để phương thức con có thể tìm các view khác
        setupSeasonSpinner(view, seasonSpinner, viewPager);

        // Setup ViewPager và TabLayout
        if (selectedSeasonObject != null) {
            viewPager.setAdapter(new LeagueDetailsPagerAdapter(this, leagueId, selectedSeasonObject));
            viewPager.setOffscreenPageLimit(3);
        }
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

        List<String> seasonYears = new ArrayList<>();
        int selectionIndex = 0;
        for (int i = 0; i < allSeasons.size(); i++) {
            Season s = allSeasons.get(i);
            String seasonString;

            try {
                // Lấy ra chuỗi năm từ ngày bắt đầu và kết thúc
                String startYearStr = s.getStart().substring(0, 4);
                String endYearStr = s.getEnd().substring(0, 4);

                // So sánh hai năm
                if (startYearStr.equals(endYearStr)) {
                    // Nếu cùng năm, chỉ hiển thị một năm (VD: "2024")
                    seasonString = startYearStr;
                } else {
                    // Nếu khác năm, hiển thị dạng YYYY/YYYY+1 (VD: "2024/2025")
                    seasonString = s.getYear() + "/" + (s.getYear() + 1);
                }
            } catch (Exception e) {
                // Phương án dự phòng nếu có lỗi
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
                selectedSeasonObject = allSeasons.get(allSeasons.size() - 1 - position);

                updateSeasonUI(rootView, selectedSeasonObject);

                viewPager.setAdapter(new LeagueDetailsPagerAdapter(LeagueDetailsFragment.this, leagueId, selectedSeasonObject));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }


    private void updateSeasonUI(View rootView, Season season) {
        TextView textStartDate = rootView.findViewById(R.id.text_season_start_date);
        TextView textEndDate = rootView.findViewById(R.id.text_season_end_date);
        ProgressBar seasonProgressBar = rootView.findViewById(R.id.progress_bar_season);

        if (season == null) return;

        SimpleDateFormat apiFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        SimpleDateFormat displayFormat = new SimpleDateFormat("dd.MM.", Locale.US);

        try {
            Date startDate = apiFormat.parse(season.getStart());
            Date endDate = apiFormat.parse(season.getEnd());
            textStartDate.setText(displayFormat.format(startDate));
            textEndDate.setText(displayFormat.format(endDate));

            Date today = new Date();
            if (today.after(startDate) && today.before(endDate)) {
                long totalDuration = endDate.getTime() - startDate.getTime();
                long elapsedDuration = today.getTime() - startDate.getTime();
                int progress = (int) ((double) elapsedDuration / totalDuration * 100);
                seasonProgressBar.setProgress(progress);
            } else if (today.after(endDate) || today.equals(endDate)) {
                seasonProgressBar.setProgress(100);
            } else {
                seasonProgressBar.setProgress(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            textStartDate.setText("N/A");
            textEndDate.setText("N/A");
        }
    }
}