package com.example.flashscoreapp.data.model.local;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "users", indices = {@Index(value = {"email"}, unique = true)})
public class User {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String email;
    public String password; // Lưu ý: Trong thực tế, mật khẩu phải được mã hóa (hashed)

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }
}