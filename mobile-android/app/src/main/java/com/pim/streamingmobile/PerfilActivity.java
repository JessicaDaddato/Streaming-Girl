package com.pim.streamingmobile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class PerfilActivity extends AppCompatActivity {

    Button btnVoltarPerfil, btnEditarPerfil, btnVerCurtidas, btnSairPerfil;
    TextView txtQuantidadeCurtidas, txtQuantidadePlaylists, txtQuantidadeCriadores, txtQuantidadeConteudosEnviados;
    TextView txtNomePerfil, txtEmailPerfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        btnVoltarPerfil = findViewById(R.id.btnVoltarPerfil);
        btnEditarPerfil = findViewById(R.id.btnEditarPerfil);
        btnVerCurtidas = findViewById(R.id.btnVerCurtidas);
        btnSairPerfil = findViewById(R.id.btnSairPerfil);

        txtQuantidadeCurtidas = findViewById(R.id.txtQuantidadeCurtidas);
        txtQuantidadePlaylists = findViewById(R.id.txtQuantidadePlaylists);
        txtQuantidadeCriadores = findViewById(R.id.txtQuantidadeCriadores);
        txtQuantidadeConteudosEnviados = findViewById(R.id.txtQuantidadeConteudosEnviados);
        txtNomePerfil = findViewById(R.id.txtNomePerfil);
        txtEmailPerfil = findViewById(R.id.txtEmailPerfil);

        txtQuantidadeCurtidas.setText("Conteudos curtidos: " + FavoritosManager.getFavoritos().size());
        txtQuantidadePlaylists.setText("Playlists disponiveis: carregando...");
        txtQuantidadeCriadores.setText("Criadores seguidos: " + SocialManager.getQuantidadeCriadoresSeguidos());
        txtQuantidadeConteudosEnviados.setText("Conteudos enviados: " + UploadsManager.getQuantidadeUploads(this));

        btnVoltarPerfil.setOnClickListener(v -> finish());

        btnEditarPerfil.setOnClickListener(v ->
                Toast.makeText(this, "Edicao de perfil em desenvolvimento", Toast.LENGTH_SHORT).show()
        );

        btnVerCurtidas.setOnClickListener(v -> {
            Intent intent = new Intent(PerfilActivity.this, PlaylistsActivity.class);
            startActivity(intent);
        });

        btnSairPerfil.setOnClickListener(v -> {
            AuthManager.clearToken(this);
            Toast.makeText(this, "Sessao encerrada.", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(PerfilActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finishAffinity();
        });

        carregarResumoPerfil();
        carregarUsuario();
    }

    @Override
    protected void onResume() {
        super.onResume();
        txtQuantidadeCurtidas.setText("Conteudos curtidos: " + FavoritosManager.getFavoritos().size());
        txtQuantidadeCriadores.setText("Criadores seguidos: " + SocialManager.getQuantidadeCriadoresSeguidos());
        txtQuantidadeConteudosEnviados.setText("Conteudos enviados: " + UploadsManager.getQuantidadeUploads(this));
    }

    private void carregarResumoPerfil() {
        ApiClient.getPlaylists(new ApiClient.ApiCallback<List<PlaylistItem>>() {
            @Override
            public void onSuccess(List<PlaylistItem> result) {
                runOnUiThread(() -> {
                    int quantidade = result != null ? result.size() : 0;
                    txtQuantidadePlaylists.setText("Playlists disponiveis: " + quantidade);
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() ->
                        txtQuantidadePlaylists.setText("Playlists disponiveis: indisponivel"));
            }
        });
    }

    private void carregarUsuario() {
        ApiClient.getUsuarios(new ApiClient.ApiCallback<List<UsuarioItem>>() {
            @Override
            public void onSuccess(List<UsuarioItem> result) {
                runOnUiThread(() -> {
                    if (result != null && !result.isEmpty()) {
                        UsuarioItem usuario = result.get(0);
                        txtNomePerfil.setText(usuario.nome);
                        txtEmailPerfil.setText(usuario.email);
                    }
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> Toast.makeText(PerfilActivity.this,
                        "Nao foi possivel carregar o perfil.",
                        Toast.LENGTH_SHORT).show());
            }
        });
    }
}
