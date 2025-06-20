package com.example.flashscoreapp.ui.leagues.details.fixtures;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class FixturesViewModelFactory implements ViewModelProvider.Factory {
    private final Application application;
    private final int leagueId;
    private final int season;

    public FixturesViewModelFactory(Application application, int leagueId, int season) {
        this.application = application;
        this.leagueId = leagueId;
        this.season = season;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(FixturesViewModel.class)) {
            return (T) new FixturesViewModel(application, leagueId, season);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}