package com.example.flashscoreapp.ui.auth;

import android.app.Application;
import android.util.Patterns;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.flashscoreapp.data.model.local.User;
import com.example.flashscoreapp.data.repository.UserRepository;
import com.example.flashscoreapp.util.SessionManager;
public class AuthViewModel extends AndroidViewModel {
    private UserRepository userRepository;
    private MutableLiveData<String> error = new MutableLiveData<>();
    private MutableLiveData<Boolean> navigateBack = new MutableLiveData<>();
    private SessionManager sessionManager;
    public AuthViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(application);
        sessionManager = new SessionManager(application);
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<Boolean> getNavigateBack() {
        return navigateBack;
    }

    public void loginOrRegister(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            error.setValue("Email và mật khẩu không được để trống");
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            error.setValue("Địa chỉ email không hợp lệ");
            return;
        }
        if (password.length() < 6) {
            error.setValue("Mật khẩu phải có ít nhất 6 ký tự");
            return;
        }

        userRepository.findByEmail(email, user -> {
            if (user != null) { // Người dùng đã tồn tại -> Đăng nhập
                if (user.password.equals(password)) {
                    // Đăng nhập thành công
                    sessionManager.saveUserSession(user.email);
                    navigateBack.postValue(true);
                } else {
                    error.postValue("Sai mật khẩu");
                }
            } else { // Người dùng chưa tồn tại -> Đăng ký
                User newUser = new User(email, password);
                userRepository.insert(newUser);
                // Đăng ký thành công
                sessionManager.saveUserSession(newUser.email);
                navigateBack.postValue(true);
            }
        });
    }
}