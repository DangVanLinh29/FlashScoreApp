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
import androidx.fragment.app.Fragment;
import com.example.flashscoreapp.R;
import com.example.flashscoreapp.ui.auth.LoginActivity;
import com.example.flashscoreapp.util.SessionManager; // Import SessionManager

public class SettingsFragment extends Fragment {

    private TextView loginStatusText;
    private Button loginButton;
    private View userSection;
    private TextView logoutButton;
    private SessionManager sessionManager;

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

        // Khởi tạo SessionManager
        sessionManager = new SessionManager(requireContext());

        // Ánh xạ các view
        userSection = view.findViewById(R.id.user_section);
        loginStatusText = userSection.findViewById(R.id.text_login_status);
        loginButton = userSection.findViewById(R.id.button_login);
        logoutButton = view.findViewById(R.id.button_logout);

        // Gán sự kiện cho các nút
        loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            loginLauncher.launch(intent);
        });

        logoutButton.setOnClickListener(v -> {
            sessionManager.logoutUser();
            updateUI(); // Cập nhật lại giao diện sau khi đăng xuất
        });

        // Cập nhật giao diện ngay khi fragment được tạo
        updateUI();
    }

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
}