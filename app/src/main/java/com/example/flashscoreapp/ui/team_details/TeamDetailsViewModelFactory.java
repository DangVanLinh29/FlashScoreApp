package com.example.flashscoreapp.ui.team_details;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class TeamDetailsViewModelFactory implements ViewModelProvider.Factory {
    private final Application application;
    private final int teamId;

    // Constructor giờ chỉ cần 2 tham số
    public TeamDetailsViewModelFactory(Application application, int teamId) {
        this.application = application;
        this.teamId = teamId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(TeamDetailsViewModel.class)) {
            // Gọi constructor của ViewModel với 2 tham số
            return (T) new TeamDetailsViewModel(application, teamId);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}