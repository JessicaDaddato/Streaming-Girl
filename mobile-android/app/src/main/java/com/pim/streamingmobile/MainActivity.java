package com.pim.streamingmobile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button btnConteudos, btnPlaylists, btnPerfil;
    Button btnAbrirDestaque, btnAbrirSugestao1, btnAbrirSugestao2;
    TextView txtTituloDestaque, txtDescricaoDestaque;
    TextView txtSugestao1Titulo, txtSugestao1Categoria, txtSugestao2Titulo, txtSugestao2Categoria;
    private final java.util.List<ConteudoItem> conteudosHome = new java.util.ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnConteudos = findViewById(R.id.btnConteudos);
        btnPlaylists = findViewById(R.id.btnPlaylists);
        btnPerfil = findViewById(R.id.btnPerfil);
        btnAbrirDestaque = findViewById(R.id.btnAbrirDestaque);
        btnAbrirSugestao1 = findViewById(R.id.btnAbrirSugestao1);
        btnAbrirSugestao2 = findViewById(R.id.btnAbrirSugestao2);
        txtTituloDestaque = findViewById(R.id.txtTituloDestaque);
        txtDescricaoDestaque = findViewById(R.id.txtDescricaoDestaque);
        txtSugestao1Titulo = findViewById(R.id.txtSugestao1Titulo);
        txtSugestao1Categoria = findViewById(R.id.txtSugestao1Categoria);
        txtSugestao2Titulo = findViewById(R.id.txtSugestao2Titulo);
        txtSugestao2Categoria = findViewById(R.id.txtSugestao2Categoria);

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

        btnAbrirDestaque.setOnClickListener(v -> abrirDetalhe(0));
        btnAbrirSugestao1.setOnClickListener(v -> abrirDetalhe(1));
        btnAbrirSugestao2.setOnClickListener(v -> abrirDetalhe(2));
        carregarHome();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!AuthManager.isLoggedIn(this)) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }

    private void carregarHome() {
        definirHomeCarregando();

        ApiClient.getConteudos(new ApiClient.ApiCallback<java.util.List<ConteudoItem>>() {
            @Override
            public void onSuccess(java.util.List<ConteudoItem> result) {
                runOnUiThread(() -> atualizarHome(result));
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    txtTituloDestaque.setText("Nao foi possivel carregar o destaque");
                    txtDescricaoDestaque.setText("Tente novamente em alguns instantes.");
                });
            }
        });
    }

    private void definirHomeCarregando() {
        txtTituloDestaque.setText("Carregando destaques...");
        txtDescricaoDestaque.setText("Buscando os melhores conteudos para sua home.");
        txtSugestao1Titulo.setText("Carregando...");
        txtSugestao1Categoria.setText("Aguarde");
        txtSugestao2Titulo.setText("Carregando...");
        txtSugestao2Categoria.setText("Aguarde");
    }

    private void atualizarHome(java.util.List<ConteudoItem> result) {
        conteudosHome.clear();
        if (result != null) {
            conteudosHome.addAll(result);
        }

        preencherDestaque(0, txtTituloDestaque, txtDescricaoDestaque, btnAbrirDestaque, true);
        preencherSugestao(1, txtSugestao1Titulo, txtSugestao1Categoria, btnAbrirSugestao1);
        preencherSugestao(2, txtSugestao2Titulo, txtSugestao2Categoria, btnAbrirSugestao2);
    }

    private void preencherDestaque(int index, TextView titulo, TextView descricao, Button botao, boolean destaque) {
        if (index < conteudosHome.size()) {
            ConteudoItem conteudo = conteudosHome.get(index);
            titulo.setText(conteudo.titulo);
            descricao.setText(conteudo.getDescricao());
            botao.setEnabled(true);
            return;
        }

        titulo.setText("Conteudo indisponivel");
        descricao.setText("Volte mais tarde para ver novos destaques.");
        botao.setEnabled(false);
    }

    private void preencherSugestao(int index, TextView titulo, TextView categoria, Button botao) {
        if (index < conteudosHome.size()) {
            ConteudoItem conteudo = conteudosHome.get(index);
            titulo.setText(conteudo.titulo);
            categoria.setText(conteudo.getCategoria());
            botao.setEnabled(true);
            return;
        }

        titulo.setText("Conteudo indisponivel");
        categoria.setText("Sugestao temporariamente indisponivel");
        botao.setEnabled(false);
    }

    private void abrirDetalhe(int index) {
        if (index >= conteudosHome.size()) {
            Toast.makeText(this, "Conteudo ainda nao carregado.", Toast.LENGTH_SHORT).show();
            return;
        }

        ConteudoItem conteudo = conteudosHome.get(index);
        Intent intent = new Intent(MainActivity.this, DetalheConteudoActivity.class);
        intent.putExtra("titulo", conteudo.titulo);
        intent.putExtra("categoria", conteudo.getCategoria());
        intent.putExtra("descricao", conteudo.getDescricao());
        intent.putExtra("criador", conteudo.getNomeCriador());
        startActivity(intent);
    }
}
