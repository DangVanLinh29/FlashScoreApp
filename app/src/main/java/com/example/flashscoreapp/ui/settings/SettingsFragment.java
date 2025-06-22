package com.example.flashscoreapp.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.example.flashscoreapp.R;
import com.example.flashscoreapp.util.ThemeHelper;

public class SettingsFragment extends Fragment {

    private TextView textCurrentTheme;
    private View layoutThemeSetting;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textCurrentTheme = view.findViewById(R.id.text_current_theme);
        layoutThemeSetting = view.findViewById(R.id.layout_theme_setting);

        // Cập nhật text hiển thị theme hiện tại
        updateThemeTextView();

        // Bắt sự kiện click để mở hộp thoại chọn theme
        layoutThemeSetting.setOnClickListener(v -> showThemeChooserDialog());
    }

    private void updateThemeTextView() {
        int currentTheme = ThemeHelper.getSavedTheme(requireContext());
        if (currentTheme == ThemeHelper.LIGHT_MODE) {
            textCurrentTheme.setText("Sáng");
        } else if (currentTheme == ThemeHelper.DARK_MODE) {
            textCurrentTheme.setText("Tối");
        } else {
            textCurrentTheme.setText("Hệ thống");
        }
    }

    private void showThemeChooserDialog() {
        String[] themes = {"Sáng", "Tối", "Hệ thống"};
        int currentTheme = ThemeHelper.getSavedTheme(requireContext());
        int checkedItem;

        if (currentTheme == ThemeHelper.LIGHT_MODE) {
            checkedItem = 0;
        } else if (currentTheme == ThemeHelper.DARK_MODE) {
            checkedItem = 1;
        } else {
            checkedItem = 2;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Chọn chế độ giao diện")
                .setSingleChoiceItems(themes, checkedItem, (dialog, which) -> {
                    int selectedTheme;
                    if (which == 0) {
                        selectedTheme = ThemeHelper.LIGHT_MODE;
                    } else if (which == 1) {
                        selectedTheme = ThemeHelper.DARK_MODE;
                    } else {
                        selectedTheme = ThemeHelper.SYSTEM_DEFAULT;
                    }
                    // Lưu và áp dụng theme mới
                    ThemeHelper.saveTheme(requireContext(), selectedTheme);
                    ThemeHelper.applyTheme(selectedTheme);
                    dialog.dismiss();
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}