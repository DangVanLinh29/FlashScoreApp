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
import com.example.flashscoreapp.data.model.domain.PlayerDisplay;
import com.example.flashscoreapp.data.model.remote.ApiDetailedPlayerInfo;
import com.example.flashscoreapp.data.model.remote.ApiLineup;
import com.example.flashscoreapp.data.model.remote.ApiLineupPlayer;
import com.example.flashscoreapp.data.model.remote.ApiPlayerResponse;
import com.example.flashscoreapp.ui.match_details.MatchDetailsActivity;
import com.example.flashscoreapp.ui.match_details.MatchDetailsViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

        viewModel.getLineups().observe(getViewLifecycleOwner(), lineups -> updateUi());
        viewModel.getHomeTeamPlayers().observe(getViewLifecycleOwner(), players -> updateUi());
        viewModel.getAwayTeamPlayers().observe(getViewLifecycleOwner(), players -> updateUi());
    }

    private void setupRecyclerViews() {
        homeSubsAdapter = new LineupPlayerAdapter();
        awaySubsAdapter = new LineupPlayerAdapter();
        homeSubsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        homeSubsRecycler.setAdapter(homeSubsAdapter);
        awaySubsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        awaySubsRecycler.setAdapter(awaySubsAdapter);
    }

    private void updateUi() {
        List<ApiLineup> lineups = viewModel.getLineups().getValue();
        List<ApiPlayerResponse> homePlayers = viewModel.getHomeTeamPlayers().getValue();
        List<ApiPlayerResponse> awayPlayers = viewModel.getAwayTeamPlayers().getValue();

        if (lineups == null || lineups.isEmpty() || homePlayers == null || awayPlayers == null) {
            return;
        }

        Map<Integer, ApiDetailedPlayerInfo> homeInfoMap = homePlayers.stream()
                .map(ApiPlayerResponse::getPlayer)
                .collect(Collectors.toMap(ApiDetailedPlayerInfo::getId, p -> p, (a, b) -> a));
        Map<Integer, ApiDetailedPlayerInfo> awayInfoMap = awayPlayers.stream()
                .map(ApiPlayerResponse::getPlayer)
                .collect(Collectors.toMap(ApiDetailedPlayerInfo::getId, p -> p, (a, b) -> a));

        ApiLineup homeLineup = lineups.get(0);
        homeTeamName.setText(homeLineup.getTeam().getName());
        homeFormation.setText("Chiến thuật: " + homeLineup.getFormation());
        List<PlayerDisplay> homeStarters = createDisplayList(homeLineup.getStartXI(), homeInfoMap);
        List<PlayerDisplay> homeSubs = createDisplayList(homeLineup.getSubstitutes(), homeInfoMap);
        drawPlayersOnPitch(pitchLayoutHome, homeStarters, homeLineup.getFormation(), true);
        homeSubsAdapter.setPlayers(homeSubs);

        if (lineups.size() > 1) {
            ApiLineup awayLineup = lineups.get(1);
            awayTeamName.setText(awayLineup.getTeam().getName());
            awayFormation.setText("Chiến thuật: " + awayLineup.getFormation());
            List<PlayerDisplay> awayStarters = createDisplayList(awayLineup.getStartXI(), awayInfoMap);
            List<PlayerDisplay> awaySubs = createDisplayList(awayLineup.getSubstitutes(), awayInfoMap);
            drawPlayersOnPitch(pitchLayoutAway, awayStarters, awayLineup.getFormation(), false);
            awaySubsAdapter.setPlayers(awaySubs);
        }
    }

    private List<PlayerDisplay> createDisplayList(List<ApiLineupPlayer> rawPlayers, Map<Integer, ApiDetailedPlayerInfo> infoMap) {
        if (rawPlayers == null) return new ArrayList<>();
        return rawPlayers.stream()
                .map(p -> {
                    ApiDetailedPlayerInfo info = infoMap.get(p.getPlayer().getId());
                    String photoUrl = (info != null) ? info.getPhoto() : null;
                    return new PlayerDisplay(p.getPlayer().getId(), p.getPlayer().getName(), p.getPlayer().getNumber(), p.getPlayer().getPos(), photoUrl);
                }).collect(Collectors.toList());
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
            return;
        }

        if (!isHomeTeam) {
            Collections.reverse(fieldPlayers);
            Collections.reverse(formationLines);
        }

        View gkView = createPlayerView(goalkeeper);
        int gkId = View.generateViewId();
        gkView.setId(gkId);
        pitchLayout.addView(gkView);

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

        constraintSet.clone(pitchLayout);
        constraintSet.constrainWidth(gkId, ConstraintSet.WRAP_CONTENT);
        constraintSet.constrainHeight(gkId, ConstraintSet.WRAP_CONTENT);
        constraintSet.connect(gkId, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
        constraintSet.connect(gkId, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
        if (isHomeTeam) {
            constraintSet.connect(gkId, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 16);
        } else {
            constraintSet.connect(gkId, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 16);
        }

        float verticalBiasStep = 0.8f / (allLineIds.size() + 1);
        for (int i = 0; i < allLineIds.size(); i++) {
            List<Integer> lineIds = allLineIds.get(i);
            if (lineIds.isEmpty()) continue;
            int[] idsArray = lineIds.stream().mapToInt(id -> id).toArray();
            if (idsArray.length > 1) {
                constraintSet.createHorizontalChain(ConstraintSet.PARENT_ID, ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, idsArray, null, ConstraintSet.CHAIN_SPREAD);
            } else if (idsArray.length == 1) {
                constraintSet.connect(idsArray[0], ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
                constraintSet.connect(idsArray[0], ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
            }
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
        // THAY ĐỔI KIỂU DỮ LIỆU TỪ ImageView -> View
        View playerPhoto = playerView.findViewById(R.id.image_player_photo);
        TextView number = playerView.findViewById(R.id.text_player_number_on_pitch);
        TextView name = playerView.findViewById(R.id.text_player_name_on_pitch);

        if (player != null) {

            if (player.getNumber() != null) {
                number.setText(String.valueOf(player.getNumber()));
            } else {
                number.setText("-");
            }
            name.setText(player.getName());
        }
        return playerView;
    }
}