package com.example.csharpmanualv2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.csharpmanualv2.SharedPreferences.UserPrefs;
import com.example.csharpmanualv2.network.ApiService;
import com.example.csharpmanualv2.network.RetrofitClient;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etName, etEmailReg, etPasswordReg, etPasswordRepeat;
    private CheckBox cbAgree;
    private Button btnRegister;
    private TextView tvToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etName = findViewById(R.id.etName);
        etEmailReg = findViewById(R.id.etEmailReg);
        etPasswordReg = findViewById(R.id.etPasswordReg);
        etPasswordRepeat = findViewById(R.id.etPasswordRepeat);
        cbAgree = findViewById(R.id.cbAgree);
        btnRegister = findViewById(R.id.btnRegister);
        tvToLogin = findViewById(R.id.tvToLogin);

        btnRegister.setOnClickListener(v -> doRegister());
        tvToLogin.setOnClickListener(v ->
                startActivity(new Intent(this, LoginActivity.class)));
    }

    private String textOf(TextInputEditText et) {
        return et.getText() == null ? "" : et.getText().toString().trim();
    }

    private boolean isValidEmail(String email) {
        return email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void doRegister() {
        String name = textOf(etName);
        String email = textOf(etEmailReg);
        String pass = textOf(etPasswordReg);
        String pass2 = textOf(etPasswordRepeat);

        if (name.isEmpty() || email.isEmpty() || pass.isEmpty() || pass2.isEmpty()) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidEmail(email)) {
            Toast.makeText(this, "Введите корректный email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!pass.equals(pass2)) {
            Toast.makeText(this, "Пароли не совпадают", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!cbAgree.isChecked()) {
            Toast.makeText(this, "Нужно согласиться с обработкой данных", Toast.LENGTH_SHORT).show();
            return;
        }

        btnRegister.setEnabled(false);

        ApiService.RegisterRequest req = new ApiService.RegisterRequest(name, email, pass);

        RetrofitClient.getApi().register(req).enqueue(new Callback<ApiService.SimpleMessage>() {
            @Override
            public void onResponse(Call<ApiService.SimpleMessage> call, Response<ApiService.SimpleMessage> response) {
                btnRegister.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(RegisterActivity.this,
                            "Код отправлен на почту!",
                            Toast.LENGTH_SHORT).show();

                    // Переходим на экран ввода кода
                    Intent intent = new Intent(RegisterActivity.this, VerifyEmailActivity.class);
                    intent.putExtra("email", email);
                    startActivity(intent);
                    finish();
                } else {
                    try {
                        String errorMsg = response.errorBody() != null ?
                                response.errorBody().string() : "Ошибка регистрации";
                        Toast.makeText(RegisterActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(RegisterActivity.this, "Ошибка регистрации", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiService.SimpleMessage> call, Throwable t) {
                btnRegister.setEnabled(true);
                Log.e("REGISTER", "Ошибка: " + t.getMessage(), t);
                Toast.makeText(RegisterActivity.this,
                        "Сеть/сервер: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
