package com.example.flashscoreapp.ui.match_details.statistics;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.flashscoreapp.R;
import com.example.flashscoreapp.data.model.domain.MatchStatistic;
import com.example.flashscoreapp.ui.match_details.MatchDetailsViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MatchStatisticsFragment extends Fragment {

    private MatchDetailsViewModel viewModel;
    private MatchStatisticsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recycler_view_only, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.main_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MatchStatisticsAdapter();
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(requireActivity()).get(MatchDetailsViewModel.class);

        viewModel.getMatchDetails().observe(getViewLifecycleOwner(), details -> {
            if (details != null && details.getStatistics() != null) {
                List<Object> groupedList = groupStatistics(details.getStatistics());
                adapter.setDisplayList(groupedList);
            }
        });
    }

    private List<Object> groupStatistics(List<MatchStatistic> stats) {
        // Định nghĩa các nhóm và thứ tự xuất hiện
        Map<String, List<MatchStatistic>> groupedData = new LinkedHashMap<>();
        String[] categories = {"Cú sút", "Tấn công", "Chuyền bóng", "Phòng ngự", "Bảo vệ khung thành"};
        for (String category : categories) {
            groupedData.put(category, new ArrayList<>());
        }

        Map<String, String> typeToCategoryMap = new HashMap<>();
        typeToCategoryMap.put("Shots on Goal", "Cú sút");
        typeToCategoryMap.put("Shots off Goal", "Cú sút");
        typeToCategoryMap.put("Total Shots", "Cú sút");
        typeToCategoryMap.put("Blocked Shots", "Cú sút");
        typeToCategoryMap.put("Shots insidebox", "Cú sút");
        typeToCategoryMap.put("Shots outsidebox", "Cú sút");
        typeToCategoryMap.put("Corner Kicks", "Tấn công");
        typeToCategoryMap.put("Offsides", "Tấn công");
        typeToCategoryMap.put("Ball Possession", "Chuyền bóng");
        typeToCategoryMap.put("Passes %", "Chuyền bóng");
        typeToCategoryMap.put("Total passes", "Chuyền bóng");
        typeToCategoryMap.put("Passes accurate", "Chuyền bóng");
        typeToCategoryMap.put("Fouls", "Phòng ngự");
        typeToCategoryMap.put("Yellow Cards", "Phòng ngự");
        typeToCategoryMap.put("Red Cards", "Phòng ngự");
        typeToCategoryMap.put("Goalkeeper Saves", "Bảo vệ khung thành");


        for (MatchStatistic stat : stats) {
            if (stat.getHomeValue() == null || stat.getAwayValue() == null ||
                    stat.getHomeValue().equalsIgnoreCase("null") ||
                    stat.getAwayValue().equalsIgnoreCase("null")) {
                continue;
            }

            String category = typeToCategoryMap.get(stat.getType());

            if (category != null) {
                groupedData.get(category).add(stat);
            }
        }


        List<Object> finalList = new ArrayList<>();
        for (String category : categories) {
            List<MatchStatistic> statsForCategory = groupedData.get(category);
            if (statsForCategory != null && !statsForCategory.isEmpty()) {
                finalList.add(new StatisticHeader(category));
                finalList.addAll(statsForCategory);
            }
        }
        return finalList;
    }
}