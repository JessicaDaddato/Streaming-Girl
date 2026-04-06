package com.pim.streamingmobile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button btnConteudos, btnPlaylists, btnPerfil;
    Button btnLogin, btnLogout;
    Button btnSalvarApi;
    EditText edtEmail, edtSenha, edtApiBaseUrl;
    TextView txtStatusLogin, txtApiAtual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnConteudos = findViewById(R.id.btnConteudos);
        btnPlaylists = findViewById(R.id.btnPlaylists);
        btnPerfil = findViewById(R.id.btnPerfil);
        btnLogin = findViewById(R.id.btnLogin);
        btnLogout = findViewById(R.id.btnLogout);
        btnSalvarApi = findViewById(R.id.btnSalvarApi);
        edtEmail = findViewById(R.id.edtEmail);
        edtSenha = findViewById(R.id.edtSenha);
        edtApiBaseUrl = findViewById(R.id.edtApiBaseUrl);
        txtStatusLogin = findViewById(R.id.txtStatusLogin);
        txtApiAtual = findViewById(R.id.txtApiAtual);

        edtEmail.setText("admin@streaming.com");
        edtSenha.setText("123456");
        edtApiBaseUrl.setText(ApiSettingsManager.getBaseUrl(this));

        btnConteudos.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ConteudosActivity.class);
            startActivity(intent);
        });

        btnPlaylists.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PlaylistsActivity.class);
            startActivity(intent);
        });

        btnPerfil.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PerfilActivity.class);
            startActivity(intent);
        });

        btnLogin.setOnClickListener(v -> realizarLogin());
        btnSalvarApi.setOnClickListener(v -> salvarApiBaseUrl());
        btnLogout.setOnClickListener(v -> {
            AuthManager.clearToken(this);
            atualizarEstadoAutenticacao();
            Toast.makeText(this, "Sessao encerrada.", Toast.LENGTH_SHORT).show();
        });

        atualizarEstadoAutenticacao();
    }

    private void realizarLogin() {
        String email = edtEmail.getText().toString().trim();
        String senha = edtSenha.getText().toString().trim();

        if (email.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this, "Informe e-mail e senha.", Toast.LENGTH_SHORT).show();
            return;
        }

        txtStatusLogin.setText("Fazendo login...");
        btnLogin.setEnabled(false);

        ApiClient.login(email, senha, new ApiClient.ApiCallback<LoginResponse>() {
            @Override
            public void onSuccess(LoginResponse result) {
                runOnUiThread(() -> {
                    btnLogin.setEnabled(true);

                    if (result == null || result.token == null || result.token.isBlank()) {
                        txtStatusLogin.setText("Login falhou.");
                        Toast.makeText(MainActivity.this,
                                "Token nao recebido do backend.",
                                Toast.LENGTH_LONG).show();
                        return;
                    }

                    AuthManager.saveToken(MainActivity.this, result.token);
                    atualizarEstadoAutenticacao();
                    Toast.makeText(MainActivity.this, "Login realizado com sucesso.", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    btnLogin.setEnabled(true);
                    txtStatusLogin.setText("Falha no login.");
                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void atualizarEstadoAutenticacao() {
        boolean logado = AuthManager.isLoggedIn(this);
        txtStatusLogin.setText(logado
                ? "JWT ativo. CRUD autenticado liberado."
                : "Faça login para liberar POST, PUT e DELETE.");
        txtApiAtual.setText("API atual: " + ApiSettingsManager.getBaseUrl(this));
        btnLogout.setEnabled(logado);
    }

    private void salvarApiBaseUrl() {
        String baseUrl = edtApiBaseUrl.getText().toString();
        String normalized = ApiSettingsManager.normalize(baseUrl);
        ApiSettingsManager.saveBaseUrl(this, normalized);
        txtApiAtual.setText("API atual: " + normalized);
        Toast.makeText(this, "Endereco da API salvo.", Toast.LENGTH_SHORT).show();
    }
}
