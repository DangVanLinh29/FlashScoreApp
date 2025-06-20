package com.example.flashscoreapp.ui.match_details.lineups;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.flashscoreapp.R;
import com.example.flashscoreapp.data.model.domain.LineupDisplayData;
import com.example.flashscoreapp.data.model.domain.PlayerDisplay;
import com.example.flashscoreapp.ui.match_details.MatchDetailsViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LineupsFragment extends Fragment {
    private static final String TAG = "LineupsFragment";

    private MatchDetailsViewModel viewModel;
    private TextView homeTeamName, awayTeamName, homeFormation, awayFormation;
    private RecyclerView homeSubsRecycler, awaySubsRecycler;
    private LineupPlayerAdapter homeSubsAdapter, awaySubsAdapter;
    private ConstraintLayout pitchLayoutHome, pitchLayoutAway;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lineups, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Ánh xạ views
        homeTeamName = view.findViewById(R.id.text_home_team_name);
        awayTeamName = view.findViewById(R.id.text_away_team_name);
        homeFormation = view.findViewById(R.id.text_home_formation);
        awayFormation = view.findViewById(R.id.text_away_formation);
        pitchLayoutHome = view.findViewById(R.id.pitch_layout_home);
        pitchLayoutAway = view.findViewById(R.id.pitch_layout_away);
        homeSubsRecycler = view.findViewById(R.id.recycler_home_subs);
        awaySubsRecycler = view.findViewById(R.id.recycler_away_subs);

        setupRecyclerViews();

        viewModel = new ViewModelProvider(requireActivity()).get(MatchDetailsViewModel.class);

        // Chỉ cần lắng nghe LiveData duy nhất này
        viewModel.getEnrichedLineupData().observe(getViewLifecycleOwner(), pair -> {
            if (pair != null) {
                // `pair.first` là đội nhà, `pair.second` là đội khách
                updateUi(pair.first, pair.second);
            }
        });
    }

    private void setupRecyclerViews() {
        homeSubsAdapter = new LineupPlayerAdapter();
        awaySubsAdapter = new LineupPlayerAdapter();
        homeSubsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        homeSubsRecycler.setAdapter(homeSubsAdapter);
        awaySubsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        awaySubsRecycler.setAdapter(awaySubsAdapter);
    }

    private void updateUi(LineupDisplayData homeData, LineupDisplayData awayData) {
        // Cập nhật đội nhà
        if (homeData != null) {
            homeTeamName.setText(homeData.getTeamName());
            homeFormation.setText("Chiến thuật: " + homeData.getFormation());
            drawPlayersOnPitch(pitchLayoutHome, homeData.getStarters(), homeData.getFormation(), true);
            homeSubsAdapter.setPlayers(homeData.getSubstitutes());
        }

        // Cập nhật đội khách
        if (awayData != null) {
            awayTeamName.setText(awayData.getTeamName());
            awayFormation.setText("Chiến thuật: " + awayData.getFormation());
            drawPlayersOnPitch(pitchLayoutAway, awayData.getStarters(), awayData.getFormation(), false);
            awaySubsAdapter.setPlayers(awayData.getSubstitutes());
        }
    }

    private void drawPlayersOnPitch(ConstraintLayout pitchLayout, List<PlayerDisplay> starters, String formation, boolean isHomeTeam) {
        if (pitchLayout == null || starters == null || formation == null || starters.isEmpty() || getContext() == null) {
            return;
        }

        pitchLayout.removeAllViews();
        ConstraintSet constraintSet = new ConstraintSet();

        PlayerDisplay goalkeeper = starters.get(0);
        List<PlayerDisplay> fieldPlayers = new ArrayList<>(starters.subList(1, starters.size()));

        List<Integer> formationLines = new ArrayList<>();
        try {
            for (String part : formation.split("-")) {
                formationLines.add(Integer.parseInt(part));
            }
        } catch (Exception e) {
            Log.e(TAG, "Formation không hợp lệ: " + formation);
            return; // Không vẽ nếu formation sai
        }

        // Nếu là đội nhà, các hàng được vẽ từ dưới lên (hậu vệ -> tiền đạo)
        // Nếu là đội khách, ta đảo ngược lại để vẽ từ trên xuống
        if (!isHomeTeam) {
            Collections.reverse(fieldPlayers);
            Collections.reverse(formationLines);
        }

        // --- Vẽ Thủ Môn ---
        View gkView = createPlayerView(goalkeeper);
        int gkId = View.generateViewId();
        gkView.setId(gkId);
        pitchLayout.addView(gkView);

        // --- Vẽ Các Cầu Thủ Khác ---
        List<List<Integer>> allLineIds = new ArrayList<>();
        int playerIndex = 0;
        for (Integer playersInLine : formationLines) {
            List<Integer> currentLineIds = new ArrayList<>();
            for (int i = 0; i < playersInLine; i++) {
                if (playerIndex >= fieldPlayers.size()) break;
                View playerView = createPlayerView(fieldPlayers.get(playerIndex));
                int viewId = View.generateViewId();
                playerView.setId(viewId);
                pitchLayout.addView(playerView);
                currentLineIds.add(viewId);
                playerIndex++;
            }
            allLineIds.add(currentLineIds);
        }

        // --- Áp Dụng Constraints ---
        constraintSet.clone(pitchLayout);

        // Ràng buộc cho thủ môn
        constraintSet.constrainWidth(gkId, ConstraintSet.WRAP_CONTENT);
        constraintSet.constrainHeight(gkId, ConstraintSet.WRAP_CONTENT);
        constraintSet.connect(gkId, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
        constraintSet.connect(gkId, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
        if (isHomeTeam) {
            constraintSet.connect(gkId, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 16);
        } else {
            constraintSet.connect(gkId, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 16);
        }

        // Ràng buộc cho các hàng cầu thủ
        float verticalBiasStep = 0.8f / (allLineIds.size() + 1);
        for (int i = 0; i < allLineIds.size(); i++) {
            List<Integer> lineIds = allLineIds.get(i);
            if (lineIds.isEmpty()) continue;

            int[] idsArray = lineIds.stream().mapToInt(id -> id).toArray();

            constraintSet.createHorizontalChain(ConstraintSet.PARENT_ID, ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, idsArray, null, ConstraintSet.CHAIN_SPREAD);

            float verticalBias = isHomeTeam ? (0.95f - (i + 1) * verticalBiasStep) : (0.05f + (i + 1) * verticalBiasStep);

            for (int viewId : idsArray) {
                constraintSet.constrainWidth(viewId, ConstraintSet.WRAP_CONTENT);
                constraintSet.constrainHeight(viewId, ConstraintSet.WRAP_CONTENT);
                constraintSet.connect(viewId, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
                constraintSet.connect(viewId, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
                constraintSet.setVerticalBias(viewId, verticalBias);
            }
        }

        constraintSet.applyTo(pitchLayout);
    }

    private View createPlayerView(PlayerDisplay player) {
        View playerView = LayoutInflater.from(getContext()).inflate(R.layout.item_player_on_pitch, (ViewGroup) getView(), false);

        TextView number = playerView.findViewById(R.id.text_player_number);
        TextView name = playerView.findViewById(R.id.text_player_name);

        // Không cần tìm ImageView cho ảnh thật và rating nữa

        // Gán dữ liệu
        number.setText(String.valueOf(player.getNumber()));

        // Lấy họ của cầu thủ cho ngắn gọn
        String fullName = player.getName();
        String[] nameParts = fullName.split(" ");
        String lastName = nameParts.length > 1 ? nameParts[nameParts.length - 1] : fullName;
        name.setText(lastName);

        // Không cần gọi Glide nữa

        return playerView;
    }
}