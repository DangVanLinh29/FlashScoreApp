package com.example.flashscoreapp.ui.match_details;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class MatchDetailsViewModelFactory implements ViewModelProvider.Factory {
    private final Application application;
    private final int matchId;
    private final int homeTeamId;
    private final int awayTeamId;
    private final int seasonYear; // Thêm trường seasonYear

    // Sửa constructor để nhận 5 tham số
    public MatchDetailsViewModelFactory(Application application, int matchId, int homeTeamId, int awayTeamId, int seasonYear) {
        this.application = application;
        this.matchId = matchId;
        this.homeTeamId = homeTeamId;
        this.awayTeamId = awayTeamId;
        this.seasonYear = seasonYear; // Gán giá trị
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MatchDetailsViewModel.class)) {

            return (T) new MatchDetailsViewModel(application, matchId, homeTeamId, awayTeamId, seasonYear);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}