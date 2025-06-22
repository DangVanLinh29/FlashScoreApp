package com.example.flashscoreapp.ui.auth;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import com.example.flashscoreapp.R;
import com.google.android.material.textfield.TextInputEditText;

public class AuthEmailActivity extends AppCompatActivity {

    private AuthViewModel authViewModel;
    private TextInputEditText emailEditText;
    private TextInputEditText passwordEditText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_email);

        Toolbar toolbar = findViewById(R.id.toolbar_auth_email);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        emailEditText = findViewById(R.id.edit_text_email);
        passwordEditText = findViewById(R.id.edit_text_password);
        Button continueButton = findViewById(R.id.button_auth_continue);

        continueButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            authViewModel.loginOrRegister(email, password);
        });

        authViewModel.getError().observe(this, errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        authViewModel.getNavigateBack().observe(this, navigate -> {
            if (navigate) {
                Toast.makeText(this, "Thành công!", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish(); // Quay lại màn hình trước đó
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}