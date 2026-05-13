package com.example.csharpmanualv2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.csharpmanualv2.SharedPreferences.UserPrefs;
import com.example.csharpmanualv2.network.ApiService;
import com.example.csharpmanualv2.network.RetrofitClient;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private CheckBox cbRemember;
    private Button btnLogin;
    private TextView tvToRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Проверяем, авторизован ли пользователь
        UserPrefs prefs = new UserPrefs(this);
        if (prefs.isLoggedIn()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        cbRemember = findViewById(R.id.cbRemember);
        btnLogin = findViewById(R.id.btnLogin);
        tvToRegister = findViewById(R.id.tvToRegister);

        btnLogin.setOnClickListener(v -> doLogin());
        tvToRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void doLogin() {
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Заполните email и пароль", Toast.LENGTH_SHORT).show();
            return;
        }

        btnLogin.setEnabled(false);

        ApiService.LoginRequest req = new ApiService.LoginRequest(email, password);

        RetrofitClient.getApi().login(req).enqueue(new Callback<ApiService.LoginResponse>() {
            @Override
            public void onResponse(Call<ApiService.LoginResponse> call, Response<ApiService.LoginResponse> response) {
                btnLogin.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    ApiService.LoginResponse loginResponse = response.body();



                    // Сохраняем данные пользователя
                    UserPrefs prefs = new UserPrefs(LoginActivity.this);
                    prefs.saveUser(
                            loginResponse.token,
                            loginResponse.user.id,
                            loginResponse.user.name,
                            loginResponse.user.email
                    );

                    Toast.makeText(LoginActivity.this,
                            "Привет, " + loginResponse.user.name,
                            Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else if (response.code() == 403) {
                    // Email не подтверждён
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "";
                        JSONObject json = new JSONObject(errorBody);

                        if (json.has("emailNotVerified") && json.getBoolean("emailNotVerified")) {
                            Toast.makeText(LoginActivity.this, "Email не подтверждён", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(LoginActivity.this, VerifyEmailActivity.class);
                            intent.putExtra("email", email);
                            startActivity(intent);
                        } else {
                            Toast.makeText(LoginActivity.this, "Доступ запрещён", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e("LOGIN", "Ошибка парсинга ответа: " + e.getMessage());
                        Toast.makeText(LoginActivity.this, "Email не подтверждён. Проверьте почту.", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(LoginActivity.this, VerifyEmailActivity.class);
                        intent.putExtra("email", email);
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(LoginActivity.this,
                            "Неверный email или пароль",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiService.LoginResponse> call, Throwable t) {
                btnLogin.setEnabled(true);
                Log.e("LOGIN", "Ошибка: " + t.getMessage(), t);
                Toast.makeText(LoginActivity.this,
                        "Сеть/сервер: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
