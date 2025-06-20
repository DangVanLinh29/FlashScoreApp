package com.example.flashscoreapp.ui.match_details.lineups;

import android.os.Bundle;
import android.util.Log; // THÊM IMPORT NÀY
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flashscoreapp.R;
import com.example.flashscoreapp.data.model.remote.ApiDetailedPlayerInfo;
import com.example.flashscoreapp.data.model.remote.ApiLineup;
import com.example.flashscoreapp.data.model.remote.ApiPlayerResponse;
import com.example.flashscoreapp.ui.match_details.MatchDetailsViewModel;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LineupsFragment extends Fragment {
    // Tag để lọc log cho dễ
    private static final String TAG = "LineupsFragmentDebug";

    private MatchDetailsViewModel viewModel;
    private TextView homeTeamName, awayTeamName;
    private RecyclerView homeStartersRecycler, homeSubsRecycler, awayStartersRecycler, awaySubsRecycler;
    private LineupPlayerAdapter homeStartersAdapter, homeSubsAdapter, awayStartersAdapter, awaySubsAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lineups, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        homeTeamName = view.findViewById(R.id.text_home_team_name);
        awayTeamName = view.findViewById(R.id.text_away_team_name);

        setupRecyclerViews(view);

        viewModel = new ViewModelProvider(requireActivity()).get(MatchDetailsViewModel.class);

        // Lắng nghe dữ liệu và in ra log để debug
        viewModel.getLineups().observe(getViewLifecycleOwner(), lineups -> {
            if (lineups != null && !lineups.isEmpty()) {
                Log.d(TAG, "SUCCESS: Dữ liệu Đội hình (Lineups) đã về. Số lượng: " + lineups.size());
            } else {
                Log.e(TAG, "ERROR or EMPTY: Dữ liệu Đội hình (Lineups) bị rỗng hoặc lỗi.");
            }
            updateUi();
        });

        viewModel.getHomeTeamPlayers().observe(getViewLifecycleOwner(), players -> {
            if (players != null && !players.isEmpty()) {
                Log.d(TAG, "SUCCESS: Dữ liệu Cầu thủ đội nhà đã về. Số lượng: " + players.size());
            } else {
                Log.e(TAG, "ERROR or EMPTY: Dữ liệu Cầu thủ đội nhà bị rỗng hoặc lỗi.");
            }
            updateUi();
        });

        viewModel.getAwayTeamPlayers().observe(getViewLifecycleOwner(), players -> {
            if (players != null && !players.isEmpty()) {
                Log.d(TAG, "SUCCESS: Dữ liệu Cầu thủ đội khách đã về. Số lượng: " + players.size());
            } else {
                Log.e(TAG, "ERROR or EMPTY: Dữ liệu Cầu thủ đội khách bị rỗng hoặc lỗi.");
            }
            updateUi();
        });
    }

    private void setupRecyclerViews(View view) {
        homeStartersRecycler = view.findViewById(R.id.recycler_home_starters);
        homeSubsRecycler = view.findViewById(R.id.recycler_home_subs);
        awayStartersRecycler = view.findViewById(R.id.recycler_away_starters);
        awaySubsRecycler = view.findViewById(R.id.recycler_away_subs);

        homeStartersAdapter = new LineupPlayerAdapter();
        homeSubsAdapter = new LineupPlayerAdapter();
        awayStartersAdapter = new LineupPlayerAdapter();
        awaySubsAdapter = new LineupPlayerAdapter();

        homeStartersRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        homeStartersRecycler.setAdapter(homeStartersAdapter);
        homeSubsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        homeSubsRecycler.setAdapter(homeSubsAdapter);
        awayStartersRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        awayStartersRecycler.setAdapter(awayStartersAdapter);
        awaySubsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        awaySubsRecycler.setAdapter(awaySubsAdapter);
    }

    private void updateUi() {
        List<ApiLineup> lineups = viewModel.getLineups().getValue();
        List<ApiPlayerResponse> homePlayers = viewModel.getHomeTeamPlayers().getValue();
        List<ApiPlayerResponse> awayPlayers = viewModel.getAwayTeamPlayers().getValue();

        // Kiểm tra xem nguồn dữ liệu nào chưa sẵn sàng
        if (lineups == null || lineups.isEmpty()) {
            Log.d(TAG, "Bỏ qua updateUI: Dữ liệu đội hình chưa sẵn sàng.");
            return;
        }
        if (homePlayers == null) {
            Log.d(TAG, "Bỏ qua updateUI: Dữ liệu cầu thủ đội nhà chưa sẵn sàng.");
            return;
        }
        if (awayPlayers == null) {
            Log.d(TAG, "Bỏ qua updateUI: Dữ liệu cầu thủ đội khách chưa sẵn sàng.");
            return;
        }

        Log.d(TAG, "TẤT CẢ DỮ LIỆU ĐÃ SẴN SÀNG. Bắt đầu cập nhật giao diện.");

        Map<Integer, String> homeNationalityMap = homePlayers.stream()
                .map(ApiPlayerResponse::getPlayer)
                .collect(Collectors.toMap(ApiDetailedPlayerInfo::getId, ApiDetailedPlayerInfo::getNationality, (a, b) -> a));

        Map<Integer, String> awayNationalityMap = awayPlayers.stream()
                .map(ApiPlayerResponse::getPlayer)
                .collect(Collectors.toMap(ApiDetailedPlayerInfo::getId, ApiDetailedPlayerInfo::getNationality, (a, b) -> a));

        if (lineups.size() > 0) {
            ApiLineup homeLineup = lineups.get(0);
            homeTeamName.setText(homeLineup.getTeam().getName());
            homeStartersAdapter.setNationalityMap(homeNationalityMap);
            homeStartersAdapter.setPlayers(homeLineup.getStartXI());
            homeSubsAdapter.setNationalityMap(homeNationalityMap);
            homeSubsAdapter.setPlayers(homeLineup.getSubstitutes());
        }

        if (lineups.size() > 1) {
            ApiLineup awayLineup = lineups.get(1);
            awayTeamName.setText(awayLineup.getTeam().getName());
            awayStartersAdapter.setNationalityMap(awayNationalityMap);
            awayStartersAdapter.setPlayers(awayLineup.getStartXI());
            awaySubsAdapter.setNationalityMap(awayNationalityMap);
            awaySubsAdapter.setPlayers(awayLineup.getSubstitutes());
        }
    }
}