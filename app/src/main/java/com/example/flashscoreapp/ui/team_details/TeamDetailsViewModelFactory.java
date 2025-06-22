package com.example.flashscoreapp.ui.team_details;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class TeamDetailsViewModelFactory implements ViewModelProvider.Factory {
    private final Application application;
    private final int teamId;
    private final int seasonYear;

    // Sửa constructor để nhận 3 tham số
    public TeamDetailsViewModelFactory(Application application, int teamId, int seasonYear) {
        this.application = application;
        this.teamId = teamId;
        this.seasonYear = seasonYear;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(TeamDetailsViewModel.class)) {
            // Gọi constructor của ViewModel với 3 tham số
            return (T) new TeamDetailsViewModel(application, teamId, seasonYear);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}