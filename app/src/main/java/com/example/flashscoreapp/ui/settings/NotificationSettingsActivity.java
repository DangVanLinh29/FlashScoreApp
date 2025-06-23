package com.example.flashscoreapp.ui.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.flashscoreapp.R;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class NotificationSettingsActivity extends AppCompatActivity {

    private SharedPreferences prefs;
    private static final String PREFS_NAME = "NotificationPrefs";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_settings);

        Toolbar toolbar = findViewById(R.id.toolbar_notification_settings);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        setupSwitch(R.id.switch_notify_before_start, "notify_before_start", false);
        setupSwitch(R.id.switch_lineups, "lineups", true);
        setupSwitch(R.id.switch_match_start, "match_start", false);
        setupSwitch(R.id.switch_goals, "goals", true);
        setupSwitch(R.id.switch_goal_scorers, "goal_scorers", true);
        setupSwitch(R.id.switch_red_cards, "red_cards", true);
        setupSwitch(R.id.switch_half_time_score, "half_time_score", false);
        setupSwitch(R.id.switch_second_half_start, "second_half_start", false);
        setupSwitch(R.id.switch_final_result, "final_result", true);
        setupSwitch(R.id.switch_reports, "reports", true);
    }

    private void setupSwitch(int switchId, final String prefKey, boolean defaultValue) {
        SwitchMaterial switchView = findViewById(switchId);
        // Tải trạng thái đã lưu và đặt cho switch
        switchView.setChecked(prefs.getBoolean(prefKey, defaultValue));
        // Gán sự kiện listener
        switchView.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Lưu trạng thái mới khi người dùng thay đổi
            prefs.edit().putBoolean(prefKey, isChecked).apply();
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}