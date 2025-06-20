package com.example.flashscoreapp.ui.match_details;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class MatchDetailsViewModelFactory implements ViewModelProvider.Factory {
    private final Application application;
    private final int matchId;
    // Thêm 2 trường để lưu ID của đội nhà và đội khách
    private final int homeTeamId;
    private final int awayTeamId;

    // Sửa constructor của Factory để nhận đủ 4 tham số
    public MatchDetailsViewModelFactory(Application application, int matchId, int homeTeamId, int awayTeamId) {
        this.application = application;
        this.matchId = matchId;
        this.homeTeamId = homeTeamId;
        this.awayTeamId = awayTeamId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MatchDetailsViewModel.class)) {
            // Quan trọng: Gọi constructor của ViewModel với đủ 4 tham số
            return (T) new MatchDetailsViewModel(application, matchId, homeTeamId, awayTeamId);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}