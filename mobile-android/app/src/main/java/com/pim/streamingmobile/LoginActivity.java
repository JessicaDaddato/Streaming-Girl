package com.pim.streamingmobile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText edtEmail;
    private EditText edtSenha;
    private Button btnEntrar;
    private TextView txtStatusLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtEmail = findViewById(R.id.edtEmailLogin);
        edtSenha = findViewById(R.id.edtSenhaLogin);
        btnEntrar = findViewById(R.id.btnEntrarLogin);
        txtStatusLogin = findViewById(R.id.txtStatusLogin);

        edtEmail.setText("admin@streaming.com");
        edtSenha.setText("123456");

        btnEntrar.setOnClickListener(v -> realizarLogin());
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (AuthManager.isLoggedIn(this)) {
            abrirHome();
        }
    }

    private void realizarLogin() {
        String email = edtEmail.getText().toString().trim();
        String senha = edtSenha.getText().toString().trim();

        if (email.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this, "Informe e-mail e senha.", Toast.LENGTH_SHORT).show();
            return;
        }

        txtStatusLogin.setText("Entrando...");
        btnEntrar.setEnabled(false);

        ApiClient.login(email, senha, new ApiClient.ApiCallback<LoginResponse>() {
            @Override
            public void onSuccess(LoginResponse result) {
                runOnUiThread(() -> {
                    btnEntrar.setEnabled(true);

                    if (result == null || result.token == null || result.token.isBlank()) {
                        txtStatusLogin.setText("Nao foi possivel entrar.");
                        Toast.makeText(LoginActivity.this,
                                "Token nao recebido do backend.",
                                Toast.LENGTH_LONG).show();
                        return;
                    }

                    AuthManager.saveToken(LoginActivity.this, result.token);
                    txtStatusLogin.setText("Login realizado com sucesso.");
                    abrirHome();
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    btnEntrar.setEnabled(true);
                    txtStatusLogin.setText("Falha no login.");
                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void abrirHome() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
