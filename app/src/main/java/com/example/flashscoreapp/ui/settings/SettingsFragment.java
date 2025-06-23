package com.example.flashscoreapp.ui.settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.example.flashscoreapp.R;
import com.example.flashscoreapp.ui.auth.LoginActivity;
import com.example.flashscoreapp.util.SessionManager;
import com.example.flashscoreapp.util.ThemeHelper;

public class SettingsFragment extends Fragment {

    // Các biến cho tính năng Đăng nhập/Đăng xuất
    private TextView loginStatusText;
    private Button loginButton;
    private View userSection;
    private TextView logoutButton;
    private SessionManager sessionManager;

    // Các biến cho tính năng Chọn Theme
    private TextView textCurrentTheme;
    private View layoutThemeSetting;

    // Trình khởi chạy để nhận kết quả từ LoginActivity
    final ActivityResultLauncher<Intent> loginLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // Khi đăng nhập thành công, cập nhật lại giao diện
                    updateUI();
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // --- KHỞI TẠO VÀ ÁNH XẠ VIEW CHO CẢ HAI TÍNH NĂNG ---

        // Khởi tạo SessionManager
        sessionManager = new SessionManager(requireContext());

        // Ánh xạ các view cho tính năng Đăng nhập/Đăng xuất
        userSection = view.findViewById(R.id.user_section);
        loginStatusText = userSection.findViewById(R.id.text_login_status);
        loginButton = userSection.findViewById(R.id.button_login);
        logoutButton = view.findViewById(R.id.button_logout);

        // Ánh xạ các view cho tính năng Chọn Theme
        textCurrentTheme = view.findViewById(R.id.text_current_theme);
        layoutThemeSetting = view.findViewById(R.id.layout_theme_setting);
        View notificationSetting = view.findViewById(R.id.setting_notifications);
        notificationSetting.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), NotificationSettingsActivity.class);
            startActivity(intent);
        });

        // --- GÁN SỰ KIỆN CLICK CHO CẢ HAI TÍNH NĂNG ---

        // Sự kiện click cho Đăng nhập
        loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            loginLauncher.launch(intent);
        });

        // Sự kiện click cho Đăng xuất
        logoutButton.setOnClickListener(v -> {
            sessionManager.logoutUser();
            updateUI(); // Cập nhật lại giao diện sau khi đăng xuất
        });

        // Sự kiện click để mở hộp thoại chọn theme
        layoutThemeSetting.setOnClickListener(v -> showThemeChooserDialog());


        // --- CẬP NHẬT GIAO DIỆN BAN ĐẦU ---
        updateUI();
        updateThemeTextView();
    }

    // --- CÁC PHƯƠNG THỨC CHO TÍNH NĂNG ĐĂNG NHẬP/ĐĂNG XUẤT ---
    private void updateUI() {
        if (sessionManager.isLoggedIn()) {
            // Trạng thái ĐÃ ĐĂNG NHẬP
            userSection.setVisibility(View.VISIBLE);
            loginStatusText.setText("Đã đăng nhập: " + sessionManager.getUserEmail());
            loginButton.setVisibility(View.GONE);
            logoutButton.setVisibility(View.VISIBLE);
        } else {
            // Trạng thái CHƯA ĐĂNG NHẬP
            userSection.setVisibility(View.VISIBLE);
            loginStatusText.setText("Chưa đăng nhập");
            loginButton.setVisibility(View.VISIBLE);
            logoutButton.setVisibility(View.GONE);
        }
    }

    // --- CÁC PHƯƠNG THỨC CHO TÍNH NĂNG CHỌN THEME ---
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