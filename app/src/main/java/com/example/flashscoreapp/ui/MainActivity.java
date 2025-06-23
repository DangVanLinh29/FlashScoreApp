package com.example.flashscoreapp.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.flashscoreapp.R;
import com.example.flashscoreapp.ui.favorites.FavoritesFragment;
import com.example.flashscoreapp.ui.home.HomeFragment;
import com.example.flashscoreapp.ui.leagues.LeaguesFragment;
import com.example.flashscoreapp.ui.live.LiveMatchesFragment;
import com.example.flashscoreapp.ui.settings.SettingsActivity;
import com.example.flashscoreapp.ui.settings.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.content.Intent;
import com.example.flashscoreapp.ui.search.SearchActivity;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private int currentNavItemId = R.id.navigation_home;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Người dùng đã cấp quyền
                } else {
                    // Người dùng đã từ chối quyền
                }
            });

    // 2. Thêm phương thức để hỏi xin quyền
    private void askNotificationPermission() {
        // Chỉ chạy trên Android 13 (Tiramisu) trở lên
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {
                // Quyền đã được cấp
            } else {
                // Quyền chưa được cấp, hiển thị hộp thoại xin quyền
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        askNotificationPermission();
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            currentNavItemId = item.getItemId(); // Cập nhật ID của tab được chọn

            if (currentNavItemId == R.id.navigation_home) {
                selectedFragment = new HomeFragment();
                toolbar.setTitle("Today's Matches");
            } else if (currentNavItemId == R.id.navigation_leagues) {
                selectedFragment = new LeaguesFragment();
                toolbar.setTitle("Leagues");
            } else if (currentNavItemId == R.id.navigation_favorites) {
                selectedFragment = new FavoritesFragment();
                toolbar.setTitle("Favorites");
            } else if (currentNavItemId == R.id.navigation_live) {
                selectedFragment = new LiveMatchesFragment();
                toolbar.setTitle("Trực tiếp");
            }

            if (selectedFragment != null) {
                replaceFragment(selectedFragment);
            }

            // Yêu cầu hệ thống vẽ lại menu trên Toolbar
            invalidateOptionsMenu();

            return true;
        });

        if (savedInstanceState == null) {
            replaceFragment(new HomeFragment());
            toolbar.setTitle("Today's Matches");
        }

    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Gắn tệp menu_toolbar.xml vào Toolbar
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean isFavoritesTab = (currentNavItemId == R.id.navigation_favorites);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        if (searchItem != null) {
            searchItem.setVisible(!isFavoritesTab);
        }

        MenuItem settingsItem = menu.findItem(R.id.action_settings);
        if (settingsItem != null) {
            settingsItem.setVisible(!isFavoritesTab);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Xử lý sự kiện khi nhấn vào các nút trên Toolbar
        int itemId = item.getItemId();
        if (itemId == R.id.action_search) {
            // MỞ SEARCH ACTIVITY
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
            return true;
        } else if (itemId == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}