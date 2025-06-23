package com.example.flashscoreapp.data.repository;

import android.app.Application;
import com.example.flashscoreapp.data.db.AppDatabase;
import com.example.flashscoreapp.data.db.UserDao;
import com.example.flashscoreapp.data.model.local.User;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserRepository {
    private static volatile UserRepository INSTANCE;
    private UserDao userDao;
    private ExecutorService executorService;

    public UserRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        userDao = db.userDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public static UserRepository getInstance(Application application) {
        if (INSTANCE == null) {
            synchronized (UserRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new UserRepository(application);
                }
            }
        }
        return INSTANCE;
    }
    public void insert(User user) {
        executorService.execute(() -> {
            userDao.insert(user);
        });
    }

    public void findByEmail(String email, final OnUserFoundListener listener) {
        executorService.execute(() -> {
            User user = userDao.findByEmail(email);
            listener.onUserFound(user);
        });
    }

    public interface OnUserFoundListener {
        void onUserFound(User user);
    }
}